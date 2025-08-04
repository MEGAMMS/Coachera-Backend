package com.coachera.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.coachera.backend.dto.NotificationDTO;
import com.coachera.backend.dto.SendNotificationRequest;
import com.coachera.backend.dto.DeviceTokenRequest;
import com.coachera.backend.dto.WebPushSubscriptionRequest;
import com.coachera.backend.entity.Notification;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.NotificationType;
import com.coachera.backend.entity.enums.NotificationStatus;
import com.coachera.backend.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(NotificationController.class)
@DisplayName("NotificationController Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Notification testNotification;
    private NotificationDTO testNotificationDTO;
    private SendNotificationRequest testSendRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1)
            .email("test@example.com")
            .build();

        testNotification = Notification.builder()
            .id(1L)
            .recipient(testUser)
            .type(NotificationType.SYSTEM_ALERT)
            .title("Test Notification")
            .content("Test content")
            .status(NotificationStatus.SENT)
            .build();

        testNotificationDTO = new NotificationDTO(testNotification);

        testSendRequest = SendNotificationRequest.builder()
            .userId(1)
            .type(NotificationType.SYSTEM_ALERT)
            .title("Test Notification")
            .content("Test content")
            .actionUrl("/dashboard")
            .build();
    }

    @Nested
    @DisplayName("Send Notification Tests")
    class SendNotificationTests {

        @Test
        @WithMockUser
        @DisplayName("Should send notification successfully")
        void shouldSendNotificationSuccessfully() throws Exception {
            // Given
            when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(testNotification));

            // When & Then
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testSendRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Notification"));

            verify(notificationService).sendNotification(any(SendNotificationRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle send notification failure")
        void shouldHandleSendNotificationFailure() throws Exception {
            // Given
            CompletableFuture<Notification> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Service error"));
            when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(failedFuture);

            // When & Then
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testSendRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to send notification: Service error"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should validate request body")
        void shouldValidateRequestBody() throws Exception {
            // Given - Invalid request with missing required fields
            SendNotificationRequest invalidRequest = SendNotificationRequest.builder().build();

            // When & Then
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Send Bulk Notification Tests")
    class SendBulkNotificationTests {

        @Test
        @WithMockUser
        @DisplayName("Should send bulk notifications successfully")
        void shouldSendBulkNotificationsSuccessfully() throws Exception {
            // Given
            List<Integer> userIds = Arrays.asList(1, 2, 3);
            List<Notification> notifications = Arrays.asList(testNotification);
            when(notificationService.sendBulkNotification(any(SendNotificationRequest.class), eq(userIds)))
                .thenReturn(CompletableFuture.completedFuture(notifications));

            // When & Then
            mockMvc.perform(post("/api/notifications/send/bulk")
                    .param("userIds", "1", "2", "3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testSendRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Bulk Notifications sent successfully"))
                .andExpect(jsonPath("$.data").isArray());
                // .andExpected(jsonPath("$.data[0].id").value(1));

            verify(notificationService).sendBulkNotification(any(SendNotificationRequest.class), eq(userIds));
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle bulk notification failure")
        void shouldHandleBulkNotificationFailure() throws Exception {
            // Given
            List<Integer> userIds = Arrays.asList(1, 2, 3);
            CompletableFuture<List<Notification>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Bulk service error"));
            when(notificationService.sendBulkNotification(any(SendNotificationRequest.class), eq(userIds)))
                .thenReturn(failedFuture);

            // When & Then
            mockMvc.perform(post("/api/notifications/send/bulk")
                    .param("userIds", "1", "2", "3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testSendRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to send bulk notifications: Bulk service error"));
        }
    }

    @Nested
    @DisplayName("Get Notifications Tests")
    class GetNotificationsTests {

        @Test
        @WithMockUser
        @DisplayName("Should get current user notifications successfully")
        void shouldGetMyNotificationsSuccessfully() throws Exception {
            // Given
            Page<NotificationDTO> notificationPage = new PageImpl<>(
                Arrays.asList(testNotificationDTO),
                PageRequest.of(0, 20),
                1
            );
            when(notificationService.getUserNotifications(eq(1), eq(0), eq(20)))
                .thenReturn(notificationPage);

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    // .with(user(testUser))
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));

            verify(notificationService).getUserNotifications(1, 0, 20);
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle error when getting current user notifications")
        void shouldHandleErrorWhenGettingMyNotifications() throws Exception {
            // Given
            when(notificationService.getUserNotifications(eq(1), eq(0), eq(20)))
                .thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    // .with(user(testUser))
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to retrieve notifications: Database error"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get user notifications as admin")
        void shouldGetUserNotificationsAsAdmin() throws Exception {
            // Given
            Page<NotificationDTO> notificationPage = new PageImpl<>(
                Arrays.asList(testNotificationDTO),
                PageRequest.of(0, 20),
                1
            );
            when(notificationService.getUserNotifications(eq(2), eq(0), eq(20)))
                .thenReturn(notificationPage);

            // When & Then
            mockMvc.perform(get("/api/notifications/user/2")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

            verify(notificationService).getUserNotifications(2, 0, 20);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should deny access to user notifications for non-admin")
        void shouldDenyAccessToUserNotificationsForNonAdmin() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/notifications/user/2")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isForbidden());

            verify(notificationService, never()).getUserNotifications(anyInt(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Mark Notifications as Read Tests")
    class MarkAsReadTests {

        @Test
        @WithMockUser
        @DisplayName("Should mark notifications as read successfully")
        void shouldMarkNotificationsAsReadSuccessfully() throws Exception {
            // Given
            List<Long> notificationIds = Arrays.asList(1L, 2L, 3L);
            when(notificationService.markNotificationsAsRead(eq(1), eq(notificationIds)))
                .thenReturn(3);

            // When & Then
            mockMvc.perform(put("/api/notifications/mark-read")
                    // .with(user(testUser))
                    .param("notificationIds", "1", "2", "3"))
                .andExpect(status().isOk())
                // .andExpected(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notifications marked as read"))
                .andExpect(jsonPath("$.data").value(3));

            verify(notificationService).markNotificationsAsRead(1, notificationIds);
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle error when marking notifications as read")
        void shouldHandleErrorWhenMarkingNotificationsAsRead() throws Exception {
            // Given
            List<Long> notificationIds = Arrays.asList(1L, 2L, 3L);
            when(notificationService.markNotificationsAsRead(eq(1), eq(notificationIds)))
                .thenThrow(new RuntimeException("Update error"));

            // When & Then
            mockMvc.perform(put("/api/notifications/mark-read")
                    // .with(user(testUser))
                    .param("notificationIds", "1", "2", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to mark notifications as read: Update error"));
        }
    }

    @Nested
    @DisplayName("Get Unread Count Tests")
    class GetUnreadCountTests {

        @Test
        @WithMockUser
        @DisplayName("Should get unread count successfully")
        void shouldGetUnreadCountSuccessfully() throws Exception {
            // Given
            when(notificationService.getUnreadCount(eq(1)))
                .thenReturn(5L);

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count"))
                    // .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Unread count retrieved successfully"))
                .andExpect(jsonPath("$.data").value(5));

            verify(notificationService).getUnreadCount(1);
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle error when getting unread count")
        void shouldHandleErrorWhenGettingUnreadCount() throws Exception {
            // Given
            when(notificationService.getUnreadCount(eq(1)))
                .thenThrow(new RuntimeException("Count error"));

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count"))
                    // .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to get unread count: Count error"));
        }
    }

    @Nested
    @DisplayName("Device Registration Tests")
    class DeviceRegistrationTests {

        @Test
        @WithMockUser
        @DisplayName("Should register device token successfully")
        void shouldRegisterDeviceTokenSuccessfully() throws Exception {
            // Given
            DeviceTokenRequest deviceRequest = DeviceTokenRequest.builder()
                .deviceToken("test-device-token")
                .platform("ANDROID")
                .build();

            doNothing().when(notificationService)
                .registerDeviceToken(eq(1), eq("test-device-token"), eq("ANDROID"));

            // When & Then
            mockMvc.perform(post("/api/notifications/register-device")
                    // .with(user(testUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deviceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(notificationService).registerDeviceToken(1, "test-device-token", "ANDROID");
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle error when registering device token")
        void shouldHandleErrorWhenRegisteringDeviceToken() throws Exception {
            // Given
            DeviceTokenRequest deviceRequest = DeviceTokenRequest.builder()
                .deviceToken("test-device-token")
                .platform("ANDROID")
                .build();

            doThrow(new RuntimeException("Registration error"))
                .when(notificationService)
                .registerDeviceToken(eq(1), eq("test-device-token"), eq("ANDROID"));

            // When & Then
            mockMvc.perform(post("/api/notifications/register-device")
                    // .with(user(testUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deviceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to register device token: Registration error"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should register web push subscription successfully")
        void shouldRegisterWebPushSubscriptionSuccessfully() throws Exception {
            // Given
            WebPushSubscriptionRequest webPushRequest = WebPushSubscriptionRequest.builder()
                .subscriptionJson("{\"endpoint\":\"https://example.com\"}")
                .userAgent("Mozilla/5.0")
                .build();

            doNothing().when(notificationService)
                .registerWebPushSubscription(eq(1), eq("{\"endpoint\":\"https://example.com\"}"), eq("Mozilla/5.0"));

            // When & Then
            mockMvc.perform(post("/api/notifications/register-web-push")
                    // .with(user(testUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(webPushRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(notificationService).registerWebPushSubscription(1, "{\"endpoint\":\"https://example.com\"}", "Mozilla/5.0");
        }
    }

    // @Nested
    // @DisplayName("Test Notification Tests")
    // class TestNotificationTests {

    //     @Test
    //     @WithMockUser
    //     @DisplayName("Should send test notification successfully")
    //     void shouldSendTestNotificationSuccessfully() throws Exception {
    //         // Given
    //         when(notificationService.sendNotification(any(SendNotificationRequest.class)))
    //             .thenReturn(CompletableFuture.completedFuture(testNotification));

    //         // When & Then
    //         mockMvc.perform(post("/api/notifications/test"))
    //                 // .with(user(testUser)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.success").value(true))
    //             .andExpect(jsonPath("$.message").value("Notification sent successfully"));

    //         verify(notificationService).sendNotification(argThat(request -> 
    //             request.getUserId().equals(1) &&
    //             request.getType().equals(NotificationType.SYSTEM_ALERT) &&
    //             request.getTitle().equals("Test Notification") &&
    //             request.getContent().equals("This is a test notification to verify the system is working correctly.") &&
    //             request.getActionUrl().equals("/dashboard")
    //         ));
    //     }
    // }

    @Nested
    @DisplayName("Retry Failed Notifications Tests")
    class RetryFailedNotificationsTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should retry failed notifications as admin")
        void shouldRetryFailedNotificationsAsAdmin() throws Exception {
            // Given
            doNothing().when(notificationService).retryFailedNotifications();

            // When & Then
            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(notificationService).retryFailedNotifications();
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should deny access to retry failed notifications for non-admin")
        void shouldDenyAccessToRetryFailedNotificationsForNonAdmin() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isForbidden());

            verify(notificationService, never()).retryFailedNotifications();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle error when retrying failed notifications")
        void shouldHandleErrorWhenRetryingFailedNotifications() throws Exception {
            // Given
            doThrow(new RuntimeException("Retry error"))
                .when(notificationService).retryFailedNotifications();

            // When & Then
            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to retry notifications: Retry error"));
        }
    }

    @Nested
    @DisplayName("Authentication and Authorization Tests")
    class AuthTests {

        @Test
        @DisplayName("Should require authentication for all endpoints")
        void shouldRequireAuthenticationForAllEndpoints() throws Exception {
            // Test various endpoints without authentication
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testSendRequest)))
                .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/notifications/my"))
                .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for admin endpoints")
        void shouldRequireAdminRoleForAdminEndpoints() throws Exception {
            mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class ValidationTests {

        @Test
        @WithMockUser
        @DisplayName("Should validate SendNotificationRequest")
        void shouldValidateSendNotificationRequest() throws Exception {
            // Test with null values
            SendNotificationRequest invalidRequest = SendNotificationRequest.builder()
                .title("")  // empty title
                .content("")  // empty content
                .build();

            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should validate DeviceTokenRequest")
        void shouldValidateDeviceTokenRequest() throws Exception {
            // Test with empty device token
            DeviceTokenRequest invalidRequest = DeviceTokenRequest.builder()
                .deviceToken("")
                .platform("")
                .build();

            mockMvc.perform(post("/api/notifications/register-device")
                    // .with(user(testUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @WithMockUser
        @DisplayName("Should handle empty notification list")
        void shouldHandleEmptyNotificationList() throws Exception {
            // Given
            Page<NotificationDTO> emptyPage = new PageImpl<>(
                Arrays.asList(),
                PageRequest.of(0, 20),
                0
            );
            when(notificationService.getUserNotifications(eq(1), eq(0), eq(20)))
                .thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    // .with(user(testUser))
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle zero unread count")
        void shouldHandleZeroUnreadCount() throws Exception {
            // Given
            when(notificationService.getUnreadCount(eq(1)))
                .thenReturn(0L);

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count"))
                    // .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(0));
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle empty notification IDs list for mark as read")
        void shouldHandleEmptyNotificationIdsListForMarkAsRead() throws Exception {
            // Given
            when(notificationService.markNotificationsAsRead(eq(1), eq(Arrays.asList())))
                .thenReturn(0);

            // When & Then
            mockMvc.perform(put("/api/notifications/mark-read"))
                    // .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(0));
        }
    }
}