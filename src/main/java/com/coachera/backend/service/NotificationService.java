package com.coachera.backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.NotificationDTO;
import com.coachera.backend.dto.SendNotificationRequest;
import com.coachera.backend.entity.DeviceToken;
import com.coachera.backend.entity.Notification;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.WebPushSubscription;
import com.coachera.backend.entity.enums.NotificationStatus;
import com.coachera.backend.repository.DeviceTokenRepository;
import com.coachera.backend.repository.NotificationRepository;
import com.coachera.backend.repository.UserRepository;
import com.coachera.backend.repository.WebPushSubscriptionRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final WebPushSubscriptionRepository webPushSubscriptionRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private NotificationRepository notificationRepository;
    private FirebaseMessaging firebaseMessaging;
    private UserRepository userRepository;
    private WebPushService webPushService;
    private EmailService emailService;

    /**
     * Send notification to a single user across all their devices/channels
     */
    public CompletableFuture<Notification> sendNotification(SendNotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User recipient = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

                Notification notification = createNotification(request, recipient);
                notification = notificationRepository.save(notification);

                // Send to all channels asynchronously
                sendToAllChannels(notification);

                return notification;

            } catch (Exception e) {
                log.error("Error sending notification", e);
                throw new RuntimeException("Failed to send notification", e);
            }
        });
    }

    /**
     * Send notification to multiple users
     */
    public CompletableFuture<List<Notification>> sendBulkNotification(SendNotificationRequest request, List<Integer> userIds) {
        return CompletableFuture.supplyAsync(() -> {
            return userIds.stream()
                .map(userId -> {
                    SendNotificationRequest userRequest = SendNotificationRequest.builder()
                        .userId(userId)
                        .type(request.getType())
                        .title(request.getTitle())
                        .content(request.getContent())
                        .actionUrl(request.getActionUrl())
                        .metadata(request.getMetadata())
                        // .channels(request.getChannels())
                        .build();
                    return sendNotification(userRequest).join();
                })
                .toList();
        });
    }

    /**
     * Get paginated notifications for a user
     */
    public Page<NotificationDTO> getUserNotifications(int userId, int page, int size) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);

        return notifications.map(NotificationDTO::new);
    }

    /**
     * Mark notifications as read
     */
    public int markNotificationsAsRead(int userId, List<Long> notificationIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.markAsRead(notificationIds, user);
    }

    /**
     * Get unread notification count for user
     */
    public long getUnreadCount(int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    /**
     * Register device token for push notifications
     */
    public void registerDeviceToken(int userId, String token, String platform) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Avoid duplicates
        deviceTokenRepository.findByToken(token).ifPresentOrElse(
            existing -> log.info("Device token already registered for user {}", userId),
            () -> {
                DeviceToken deviceToken = DeviceToken.builder()
                    .user(user)
                    .token(token)
                    .platform(platform)
                    .build();

                deviceTokenRepository.save(deviceToken);

                log.info("Registered new device token for user {}: {}", userId, token);
            }
        );
    }


    /**
     * Register web push subscription
     */
    public void registerWebPushSubscription(int userId, String subscriptionJson, String userAgent) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        WebPushSubscription subscription = WebPushSubscription.builder()
            .subscriptionJson(subscriptionJson)
            .userAgent(userAgent)
            .user(user)
            .build();

        webPushSubscriptionRepository.save(subscription);

        log.info("Registered new web push subscription for user {}", userId);
    }


    /**
     * Retry failed notifications
     */
    public void retryFailedNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24); // Don't retry notifications older than 24 hours
        List<Notification> failedNotifications = notificationRepository.findFailedNotificationsForRetry(cutoff);

        failedNotifications.forEach(notification -> {
            try {
                sendToAllChannels(notification);
                log.info("Retried notification {}", notification.getId());
            } catch (Exception e) {
                log.error("Failed to retry notification {}", notification.getId(), e);
            }
        });
    }

    private Notification createNotification(SendNotificationRequest request, User recipient) {
        return Notification.builder()
            .recipient(recipient)
            .type(request.getType())
            .title(request.getTitle())
            .content(request.getContent())
            .status(NotificationStatus.PENDING)
            .actionUrl(request.getActionUrl())
            .metadata(request.getMetadata() != null ? request.getMetadata() : new HashMap<>())
            .read(false)
            .sentAt(LocalDateTime.now())
            .build();
    }

    private void sendToAllChannels(Notification notification) {
        List<String> channels = getChannelsFromMetadata(notification);

        // Send to mobile (FCM)
        if (channels.contains("mobile") || channels.contains("push")) {
            sendMobilePushNotification(notification);
        }

        // Send to web (Web Push API)
        if (channels.contains("web") || channels.contains("browser")) {
            sendWebPushNotification(notification);
        }

        // Send email
        if (channels.contains("email")) {
            sendEmailNotification(notification);
        }

        // Update status
        notification.setStatus(NotificationStatus.SENT);
        notificationRepository.save(notification);
    }

    private void sendMobilePushNotification(Notification notification) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUser(notification.getRecipient());

        if (tokens.isEmpty()) {
            log.warn("No device tokens for user {}", notification.getRecipient().getId());
            return;
        }

        for (DeviceToken deviceToken : tokens) {
            try {
                Map<String, String> data = new HashMap<>(notification.getMetadata());
                data.put("notificationId", notification.getId().toString());
                data.put("actionUrl", notification.getActionUrl());

                Message message = Message.builder()
                    .setToken(deviceToken.getToken())
                    .putAllData(data)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getContent())
                        .build())
                    .build();

                String response = firebaseMessaging.send(message);
                log.info("Successfully sent mobile push notification: {}", response);

            } catch (FirebaseMessagingException e) {
                log.error("Error sending mobile push notification", e);
                notification.setStatus(NotificationStatus.FAILED);
            }
        }
    }

    private void sendWebPushNotification(Notification notification) {
        try {
            List<WebPushSubscription> subscriptions =
                webPushSubscriptionRepository.findByUser(notification.getRecipient());

            if (subscriptions.isEmpty()) {
                log.warn("No web push subscriptions for user {}", notification.getRecipient().getId());
                return;
            }

            for (WebPushSubscription sub : subscriptions) {
                try {
                    webPushService.sendNotification(
                        sub.getSubscriptionJson(),
                        notification.getTitle(),
                        notification.getContent(),
                        notification.getActionUrl(),
                        notification.getMetadata()
                    );
                } catch (Exception ex) {
                    log.error("Failed to send to subscription ID {}: {}", sub.getId(), ex.getMessage());
                }
            }

            log.info("Successfully sent web push notifications for user {}", notification.getRecipient().getId());

        } catch (Exception e) {
            log.error("Error sending web push notification", e);
            notification.setStatus(NotificationStatus.FAILED);
        }
    }


    private void sendEmailNotification(Notification notification) {
        try {
            if (notification.getEmailAddress() == null) {
                notification.setEmailAddress(notification.getRecipient().getEmail());
            }

            emailService.sendNotificationEmail(
                notification.getEmailAddress(),
                notification.getTitle(),
                notification.getContent(),
                notification.getActionUrl()
            );

            log.info("Successfully sent email notification for notification {}", notification.getId());

        } catch (Exception e) {
            log.error("Error sending email notification", e);
            notification.setStatus(NotificationStatus.FAILED);
        }
    }

    private List<String> getChannelsFromMetadata(Notification notification) {
        String channelsStr = notification.getMetadata().get("channels");
        if (channelsStr != null) {
            return List.of(channelsStr.split(","));
        }
        return List.of("mobile", "web"); // Default channels
    }
}