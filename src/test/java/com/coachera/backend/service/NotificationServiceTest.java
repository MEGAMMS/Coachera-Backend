package com.coachera.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.coachera.backend.dto.NotificationDTO;
import com.coachera.backend.dto.SendNotificationRequest;
import com.coachera.backend.entity.DeviceToken;
import com.coachera.backend.entity.Notification;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.NotificationStatus;
import com.coachera.backend.entity.enums.NotificationType;
import com.coachera.backend.repository.DeviceTokenRepository;
import com.coachera.backend.repository.NotificationRepository;
import com.coachera.backend.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceTest {

    @Mock
    private DeviceTokenRepository deviceTokenRepository;
    
    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private FirebaseMessaging firebaseMessaging;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private SendNotificationRequest testRequest;
    private Notification testNotification;
    private DeviceToken testDeviceToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1)
            .email("test@example.com")
            .build();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("channels", "mobile,email");

        testRequest = SendNotificationRequest.builder()
            .userId(1)
            .type(NotificationType.SOCIAL)
            .title("Test Notification")
            .content("Test content")
            .actionUrl("https://example.com")
            .metadata(metadata)
            .build();

        testNotification = Notification.builder()
            .id(1L)
            .recipient(testUser)
            .type(NotificationType.SOCIAL)
            .title("Test Notification")
            .content("Test content")
            .status(NotificationStatus.PENDING)
            .actionUrl("https://example.com")
            .metadata(metadata)
            .read(false)
            .sentAt(LocalDateTime.now())
            .build();

        testDeviceToken = DeviceToken.builder()
            .id(1L)
            .user(testUser)
            .token("device-token-123")
            .platform("android")
            .build();
    }

    @Nested
    @DisplayName("Send Single Notification Tests")
    class SendNotificationTests {

        @Test
        @DisplayName("Should send notification successfully")
        void shouldSendNotificationSuccessfully() throws Exception {
            // Given
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(deviceTokenRepository.findByUser(testUser)).thenReturn(List.of(testDeviceToken));
            when(firebaseMessaging.send(any(Message.class))).thenReturn("message-id-123");

            // When
            CompletableFuture<Notification> result = notificationService.sendNotification(testRequest);
            Notification notification = result.join();

            // Then
            assertNotNull(notification);
            assertEquals(testNotification.getId(), notification.getId());
            verify(userRepository).findById(1);
            verify(notificationRepository, times(2)).save(any(Notification.class)); // Once for creation, once for status update
            verify(firebaseMessaging).send(any(Message.class));
            verify(emailService).sendNotificationEmail(
                eq(testUser.getEmail()),
                eq(testRequest.getTitle()),
                eq(testRequest.getContent()),
                eq(testRequest.getActionUrl())
            );
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userRepository.findById(1)).thenReturn(Optional.empty());

            // When & Then
            CompletableFuture<Notification> result = notificationService.sendNotification(testRequest);
            
            assertThrows(RuntimeException.class, result::join);
            verify(userRepository).findById(1);
            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle Firebase messaging exception")
        void shouldHandleFirebaseMessagingException() throws FirebaseMessagingException {
            // Given
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(deviceTokenRepository.findByUser(testUser)).thenReturn(List.of(testDeviceToken));
            FirebaseMessagingException messagingException = mock(FirebaseMessagingException.class);
            when(firebaseMessaging.send(any(Message.class)))
                .thenThrow(messagingException);

            // When
            CompletableFuture<Notification> result = notificationService.sendNotification(testRequest);
            Notification notification = result.join();

            // Then
            assertNotNull(notification);
            verify(firebaseMessaging).send(any(Message.class));
            // Notification should still be created even if push fails
        }

        @Test
        @DisplayName("Should handle email service exception")
        void shouldHandleEmailServiceException() throws Exception {
            // Given
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(deviceTokenRepository.findByUser(testUser)).thenReturn(List.of(testDeviceToken));
            when(firebaseMessaging.send(any(Message.class))).thenReturn("message-id-123");
            doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());

            // When
            CompletableFuture<Notification> result = notificationService.sendNotification(testRequest);
            Notification notification = result.join();

            // Then
            assertNotNull(notification);
            verify(emailService).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should use default channels when metadata is null")
        void shouldUseDefaultChannelsWhenMetadataIsNull() {
            // Given
            SendNotificationRequest requestWithoutMetadata = SendNotificationRequest.builder()
                .userId(1)
                .type(NotificationType.SOCIAL)
                .title("Test")
                .content("Content")
                .build();

            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(deviceTokenRepository.findByUser(testUser)).thenReturn(List.of(testDeviceToken));

            // When
            CompletableFuture<Notification> result = notificationService.sendNotification(requestWithoutMetadata);
            
            // Then
            assertDoesNotThrow(result::join);
            verify(deviceTokenRepository).findByUser(testUser); // Should send to mobile (default)
        }
    }

    @Nested
    @DisplayName("Bulk Notification Tests")
    class BulkNotificationTests {

        @Test
        @DisplayName("Should send bulk notifications successfully")
        void shouldSendBulkNotificationsSuccessfully() {
            // Given
            List<Integer> userIds = List.of(1, 2, 3);
            User user2 = User.builder().id(2).email("user2@example.com").build();
            User user3 = User.builder().id(3).email("user3@example.com").build();

            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(2)).thenReturn(Optional.of(user2));
            when(userRepository.findById(3)).thenReturn(Optional.of(user3));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(deviceTokenRepository.findByUser(any())).thenReturn(List.of());

            // When
            CompletableFuture<List<Notification>> result = notificationService.sendBulkNotification(testRequest, userIds);
            List<Notification> notifications = result.join();

            // Then
            assertEquals(3, notifications.size());
            verify(userRepository).findById(1);
            verify(userRepository).findById(2);
            verify(userRepository).findById(3);
        }

        @Test
        @DisplayName("Should handle partial failures in bulk notification")
        void shouldHandlePartialFailuresInBulkNotification() {
            // Given
            List<Integer> userIds = List.of(1, 999); // 999 doesn't exist
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(999)).thenReturn(Optional.empty());
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(deviceTokenRepository.findByUser(any())).thenReturn(List.of());

            // When & Then
            CompletableFuture<List<Notification>> result = notificationService.sendBulkNotification(testRequest, userIds);
            
            assertThrows(RuntimeException.class, result::join);
        }
    }

    @Nested
    @DisplayName("User Notifications Retrieval Tests")
    class GetUserNotificationsTests {

        @Test
        @DisplayName("Should get user notifications with pagination")
        void shouldGetUserNotificationsWithPagination() {
            // Given
            int page = 0, size = 10;
            List<Notification> notifications = List.of(testNotification);
            Page<Notification> notificationPage = new PageImpl<>(notifications);
            
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.findByRecipientOrderByCreatedAtDesc(eq(testUser), any(Pageable.class)))
                .thenReturn(notificationPage);

            // When
            Page<NotificationDTO> result = notificationService.getUserNotifications(1, page, size);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            
            Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            verify(notificationRepository).findByRecipientOrderByCreatedAtDesc(testUser, expectedPageable);
        }

        @Test
        @DisplayName("Should throw exception when user not found for notifications retrieval")
        void shouldThrowExceptionWhenUserNotFoundForNotificationsRetrieval() {
            // Given
            when(userRepository.findById(999)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, 
                () -> notificationService.getUserNotifications(999, 0, 10));
        }
    }

    @Nested
    @DisplayName("Mark Notifications as Read Tests")
    class MarkNotificationsAsReadTests {

        @Test
        @DisplayName("Should mark notifications as read successfully")
        void shouldMarkNotificationsAsReadSuccessfully() {
            // Given
            List<Long> notificationIds = List.of(1L, 2L, 3L);
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.markAsRead(notificationIds, testUser)).thenReturn(3);

            // When
            int result = notificationService.markNotificationsAsRead(1, notificationIds);

            // Then
            assertEquals(3, result);
            verify(notificationRepository).markAsRead(notificationIds, testUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found for marking as read")
        void shouldThrowExceptionWhenUserNotFoundForMarkingAsRead() {
            // Given
            List<Long> notificationIds = List.of(1L, 2L);
            when(userRepository.findById(999)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, 
                () -> notificationService.markNotificationsAsRead(999, notificationIds));
        }
    }

    @Nested
    @DisplayName("Unread Count Tests")
    class UnreadCountTests {

        @Test
        @DisplayName("Should get unread count successfully")
        void shouldGetUnreadCountSuccessfully() {
            // Given
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.countByRecipientAndReadFalse(testUser)).thenReturn(5L);

            // When
            long result = notificationService.getUnreadCount(1);

            // Then
            assertEquals(5L, result);
            verify(notificationRepository).countByRecipientAndReadFalse(testUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found for unread count")
        void shouldThrowExceptionWhenUserNotFoundForUnreadCount() {
            // Given
            when(userRepository.findById(999)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, 
                () -> notificationService.getUnreadCount(999));
        }
    }

    @Nested
    @DisplayName("Device Token Registration Tests")
    class DeviceTokenRegistrationTests {

        @Test
        @DisplayName("Should register new device token successfully")
        void shouldRegisterNewDeviceTokenSuccessfully() {
            // Given
            String token = "new-device-token";
            String platform = "ios";
            
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(deviceTokenRepository.findByToken(token)).thenReturn(Optional.empty());
            when(deviceTokenRepository.save(any(DeviceToken.class))).thenReturn(testDeviceToken);

            // When
            assertDoesNotThrow(() -> 
                notificationService.registerDeviceToken(1, token, platform));

            // Then
            verify(deviceTokenRepository).findByToken(token);
            verify(deviceTokenRepository).save(any(DeviceToken.class));
        }

        @Test
        @DisplayName("Should not register duplicate device token")
        void shouldNotRegisterDuplicateDeviceToken() {
            // Given
            String token = "existing-token";
            String platform = "android";
            
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(deviceTokenRepository.findByToken(token)).thenReturn(Optional.of(testDeviceToken));

            // When
            assertDoesNotThrow(() -> 
                notificationService.registerDeviceToken(1, token, platform));

            // Then
            verify(deviceTokenRepository).findByToken(token);
            verify(deviceTokenRepository, never()).save(any(DeviceToken.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found for device token registration")
        void shouldThrowExceptionWhenUserNotFoundForDeviceTokenRegistration() {
            // Given
            when(userRepository.findById(999)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, 
                () -> notificationService.registerDeviceToken(999, "token", "platform"));
        }
    }

    @Nested
    @DisplayName("Retry Failed Notifications Tests")
    class RetryFailedNotificationsTests {

        @Test
        @DisplayName("Should retry failed notifications successfully")
        void shouldRetryFailedNotificationsSuccessfully() {
            // Given
            List<Notification> failedNotifications = List.of(testNotification);
            when(notificationRepository.findFailedNotificationsForRetry(any(LocalDateTime.class)))
                .thenReturn(failedNotifications);
            when(deviceTokenRepository.findByUser(testUser)).thenReturn(List.of(testDeviceToken));

            // When
            assertDoesNotThrow(() -> notificationService.retryFailedNotifications());

            // Then
            verify(notificationRepository).findFailedNotificationsForRetry(any(LocalDateTime.class));
            verify(deviceTokenRepository).findByUser(testUser);
        }

        @Test
        @DisplayName("Should handle exceptions during retry")
        void shouldHandleExceptionsDuringRetry() {
            // Given
            Notification failedNotification = Notification.builder()
                .id(2L)
                .recipient(testUser)
                .type(NotificationType.SOCIAL)
                .title("Failed Notification")
                .content("Content")
                .status(NotificationStatus.FAILED)
                .metadata(Map.of("channels", "mobile"))
                .build();

            List<Notification> failedNotifications = List.of(failedNotification);
            when(notificationRepository.findFailedNotificationsForRetry(any(LocalDateTime.class)))
                .thenReturn(failedNotifications);
            when(deviceTokenRepository.findByUser(testUser))
                .thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertDoesNotThrow(() -> notificationService.retryFailedNotifications());
            
            verify(notificationRepository).findFailedNotificationsForRetry(any(LocalDateTime.class));
        }
    }

    @Nested
    @DisplayName("Channel Handling Tests")
    class ChannelHandlingTests {

        @Test
        @DisplayName("Should handle different channel combinations")
        void shouldHandleDifferentChannelCombinations() {
            // Test push channels
            testChannelCombination("push", true, false);
            testChannelCombination("mobile", true, false);
            testChannelCombination("web", true, false);
            testChannelCombination("browser", true, false);
            
            // Test email channel
            testChannelCombination("email", false, true);
            
            // Test combined channels
            testChannelCombination("mobile,email", true, true);
        }

        private void testChannelCombination(String channels, boolean expectPush, boolean expectEmail) {
            // Given
            Map<String, String> metadata = Map.of("channels", channels);
            SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(1)
                .type(NotificationType.SOCIAL)
                .title("Test")
                .content("Content")
                .actionUrl("https://example.com")
                .metadata(metadata)
                .build();

            Notification notification = Notification.builder()
                .id(123L) 
                .recipient(testUser)
                .metadata(metadata)
                .actionUrl("https://example.com")
                .title("Test")      // <-- ADD THIS
                .content("Content")
                .build();

            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
            
            if (expectPush) {
                when(deviceTokenRepository.findByUser(testUser)).thenReturn(List.of(testDeviceToken));
            }

            // When
            notificationService.sendNotification(request).join();

            // Then
            if (expectPush) {
                verify(deviceTokenRepository, atLeastOnce()).findByUser(testUser);
            }
            if (expectEmail) {
                verify(emailService, atLeastOnce()).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());
            }

            // Reset mocks for next test
            reset(deviceTokenRepository, emailService);
        }

        @Test
        @DisplayName("Should handle missing device tokens gracefully")
        void shouldHandleMissingDeviceTokensGracefully() throws FirebaseMessagingException {
            // Given
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(deviceTokenRepository.findByUser(testUser)).thenReturn(List.of()); // No device tokens

            // When & Then
            assertDoesNotThrow(() -> notificationService.sendNotification(testRequest).join());
            
            verify(deviceTokenRepository).findByUser(testUser);
            verify(firebaseMessaging, never()).send(any(Message.class));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null metadata gracefully")
        void shouldHandleNullMetadataGracefully() {
            // Given
            SendNotificationRequest requestWithNullMetadata = SendNotificationRequest.builder()
                .userId(1)
                .type(NotificationType.SOCIAL)
                .title("Test")
                .content("Content")
                .metadata(null)
                .build();

            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
                Notification saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });
            when(deviceTokenRepository.findByUser(testUser)).thenReturn(List.of());

            // When & Then
            assertDoesNotThrow(() -> notificationService.sendNotification(requestWithNullMetadata).join());
        }

        @Test
        @DisplayName("Should set email address when not provided")
        void shouldSetEmailAddressWhenNotProvided() {
            // Given
            Map<String, String> emailMetadata = Map.of("channels", "email");
            testNotification.setMetadata(emailMetadata);
            testNotification.setEmailAddress(null); // Not set initially

            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // When
            notificationService.sendNotification(testRequest).join();

            // Then
            verify(emailService).sendNotificationEmail(
                eq(testUser.getEmail()), // Should use user's email
                anyString(),
                anyString(),
                anyString()
            );
        }
    }
}