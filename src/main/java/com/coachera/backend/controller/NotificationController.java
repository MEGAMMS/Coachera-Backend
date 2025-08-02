package com.coachera.backend.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.NotificationDTO;
import com.coachera.backend.dto.SendNotificationRequest;
// import com.coachera.backend.dto.DeviceTokenRequest;
// import com.coachera.backend.dto.WebPushSubscriptionRequest;
import com.coachera.backend.dto.pagination.PaginatedResponse;
// import com.coachera.backend.dto.MarkAsReadRequest;
import com.coachera.backend.entity.Notification;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification management API")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private NotificationService notificationService;

    /**
     * Send notification to a single user
     */
    @PostMapping("/send")
    @Operation(summary = "Send notification to a user")
    public CompletableFuture<ApiResponse<Notification>> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {

        return notificationService.sendNotification(request)
            .thenApply(notification -> ApiResponse.success("Notification sent successfully", notification))
            .exceptionally(throwable -> {
                log.error("Error sending notification", throwable);
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to send notification: " + throwable.getMessage());
            });
    }


    /**
     * Send notification to multiple users
     */
    @PostMapping("/send/bulk")
    @Operation(summary = "Send notification to multiple users")
    public CompletableFuture<ApiResponse<List<Notification>>> sendBulkNotification(
            @Valid @RequestBody SendNotificationRequest request,
            @RequestParam List<Integer> userIds) {
        
        return notificationService.sendBulkNotification(request, userIds)
            .thenApply(notifications -> ApiResponse.success("Bulk Notifications sent successfully", notifications))
            .exceptionally(throwable -> {
                log.error("Error sending bulk notifications", throwable);
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to send bulk notifications: " + throwable.getMessage());
            });
    }

    /**
     * Get current user's notifications with pagination
     */
    @GetMapping("/my")
    @Operation(summary = "Get current user's notifications")
    public ApiResponse<PaginatedResponse<NotificationDTO>> getMyNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<NotificationDTO> notifications = notificationService
                .getUserNotifications(user.getId(), page, size);

            return ApiResponse.paginated(notifications);

        } catch (Exception e) {
            log.error("Error retrieving notifications for user {}", user.getId(), e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to retrieve notifications: " + e.getMessage());
        }
    }


    /**
     * Get notifications for a specific user (admin only)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get notifications for a specific user (admin only)")
    public ApiResponse<PaginatedResponse<NotificationDTO>> getUserNotifications(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Page<NotificationDTO> notifications = notificationService
                .getUserNotifications(userId, page, size);
            
            return ApiResponse.paginated(notifications);

        } catch (Exception e) {
            log.error("Error retrieving notifications for user {}", userId, e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to retrieve notifications: " + e.getMessage());
        }
    }

    /**
     * Mark notifications as read
     */
    // @PutMapping("/mark-read")
    // @Operation(summary = "Mark notifications as read")
    // public ApiResponse<Integer> markAsRead(
    //         @AuthenticationPrincipal User user,
    //         @Valid @RequestBody MarkAsReadRequest request) {

    //     try {
    //         int updatedCount = notificationService.markNotificationsAsRead(
    //             user.getId(),
    //             request.getNotificationIds()
    //         );

    //         return ApiResponse.success("Notifications marked as read", updatedCount);

    //     } catch (Exception e) {
    //         log.error("Error marking notifications as read for user {}", user.getId(), e);
    //         return ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to mark notifications as read: " + e.getMessage());
    //     }
    // }


    /**
     * Get unread notification count
     */
    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ApiResponse<Long> getUnreadCount(
            @AuthenticationPrincipal User user) {
        
        try {
            long count = notificationService.getUnreadCount(user.getId());
            
            return ApiResponse.success("Unread count retrieved successfully",count);

        } catch (Exception e) {
            log.error("Error getting unread count for user {}", user.getId(), e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST,"Failed to get unread count: " + e.getMessage());
        }
    }

    /**
     * Register device token for push notifications (mobile)
     */
    // @PostMapping("/register-device")
    // @Operation(summary = "Register device token for push notifications")
    // public ApiResponse<Void> registerDeviceToken(
    //         @AuthenticationPrincipal User user,
    //         @Valid @RequestBody DeviceTokenRequest request) {
        
    //     try {
    //         notificationService.registerDeviceToken(user.getId(), request.getDeviceToken());
            
    //         return ApiResponse.noContentResponse();
            
    //     } catch (Exception e) {
    //         log.error("Error registering device token for user {}", user.getId(), e);
    //         return ApiResponse.error(HttpStatus.BAD_REQUEST,"Failed to register device token: " + e.getMessage());
    //     }
    // }

    /**
     * Register web push subscription (web browsers)
     */
    // @PostMapping("/register-web-push")
    // @Operation(summary = "Register web push subscription")
    // public ApiResponse<Void> registerWebPushSubscription(
    //         @AuthenticationPrincipal User user,
    //         @Valid @RequestBody WebPushSubscriptionRequest request) {
        
    //     try {
    //         notificationService.registerWebPushSubscription(
    //             user.getId(), 
    //             request.getSubscriptionJson()
    //         );
            
    //         return ApiResponse.noContentResponse();

    //     } catch (Exception e) {
    //         log.error("Error registering web push subscription for user {}", user.getId(), e);
    //         return ApiResponse.error(HttpStatus.BAD_REQUEST,"Failed to register web push subscription: " + e.getMessage());
    //     }
    // }

    /**
     * Test endpoint to send a test notification
     */
    @PostMapping("/test")
    @Operation(summary = "Send a test notification (development only)")
    public CompletableFuture<ApiResponse<Notification>> sendTestNotification(
            @AuthenticationPrincipal User user) {
        
        SendNotificationRequest request = SendNotificationRequest.builder()
            .userId(user.getId())
            .type(com.coachera.backend.entity.enums.NotificationType.SYSTEM_ALERT)
            .title("Test Notification")
            .content("This is a test notification to verify the system is working correctly.")
            .actionUrl("/dashboard")
            .channels(List.of("mobile", "web"))
            .build();
        
        return sendNotification(request);
    }

    /**
     * Admin endpoint to retry failed notifications
     */
    @PostMapping("/retry-failed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Retry failed notifications (admin only)")
    public ApiResponse<Void> retryFailedNotifications() {
        
        try {
            notificationService.retryFailedNotifications();
            
            return ApiResponse.noContentResponse();

        } catch (Exception e) {
            log.error("Error retrying failed notifications", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST,"Failed to retry notifications: " + e.getMessage());
        }
    }
}