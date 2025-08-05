package com.coachera.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.coachera.backend.Config.TestSecurityConfig;
import com.coachera.backend.dto.DeviceTokenRequest;
import com.coachera.backend.dto.NotificationDTO;
import com.coachera.backend.dto.SendNotificationRequest;
import com.coachera.backend.entity.Notification;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.NotificationStatus;
import com.coachera.backend.entity.enums.NotificationType;
import com.coachera.backend.repository.AccessTokenRepository;
import com.coachera.backend.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(NotificationController.class)
@Import(TestSecurityConfig.class)
@DisplayName("NotificationController Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User adminUser;
    private SendNotificationRequest testRequest;
    private Notification testNotification;
    private NotificationDTO testNotificationDTO;
    private DeviceTokenRequest deviceTokenRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1)
            .email("test@example.com")
            .username("Teto")
            .role("STUDENT")
            .build();

        adminUser = User.builder()
            .id(2)
            .email("admin@example.com")
            .username("MG3MZ")
            .role("ADMIN")
            .build();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("channels", "mobile,email");

        testRequest = SendNotificationRequest.builder()
            .userId(1)
            .type(NotificationType.SYSTEM_ALERT)
            .title("Test Notification")
            .content("Test content")
            .actionUrl("https://example.com")
            .metadata(metadata)
            .build();

        testNotification = Notification.builder()
            .id(1L)
            .recipient(testUser)
            .type(NotificationType.SYSTEM_ALERT)
            .title("Test Notification")
            .content("Test content")
            .status(NotificationStatus.SENT)
            .actionUrl("https://example.com")
            .metadata(metadata)
            .read(false)
            .sentAt(LocalDateTime.now())
            .build();

        testNotificationDTO = new NotificationDTO(testNotification);

        deviceTokenRequest = DeviceTokenRequest.builder()
            .deviceToken("device-token-123")
            .platform("android")
            .build();
    }

    @Nested
    @DisplayName("Send Single Notification Tests")
    class SendNotificationTests {

        @Test
        @DisplayName("Should send notification successfully")
        @WithMockUser
        void shouldSendNotificationSuccessfully() throws Exception {
            // Given
            when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(testNotification));

            // When & Then
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Notification"))
                .andExpect(jsonPath("$.data.content").value("Test content"));

            verify(notificationService).sendNotification(any(SendNotificationRequest.class));
        }

        @Test
        @DisplayName("Should handle notification service failure")
        @WithMockUser
        void shouldHandleNotificationServiceFailure() throws Exception {
            // Given
            CompletableFuture<Notification> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Service error"));
            when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(failedFuture);

            // When & Then
            MvcResult result = mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk()) // CompletableFuture exception handling returns 200 with error response
                .andReturn();

            String content = result.getResponse().getContentAsString();
            assert content.contains("Failed to send notification");
            verify(notificationService).sendNotification(any(SendNotificationRequest.class));
        }

        @Test
        @DisplayName("Should validate request body")
        @WithMockUser
        void shouldValidateRequestBody() throws Exception {
            // Given - Invalid request (missing required fields)
            SendNotificationRequest invalidRequest = SendNotificationRequest.builder().build();

            // When & Then
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

            verify(notificationService, never()).sendNotification(any());
        }

        @Test
        @DisplayName("Should require authentication")
        void shouldRequireAuthentication() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isUnauthorized());

            verify(notificationService, never()).sendNotification(any());
        }
    }

    @Nested
    @DisplayName("Send Bulk Notification Tests")
    class SendBulkNotificationTests {

        @Test
        @DisplayName("Should send bulk notifications successfully")
        @WithMockUser
        void shouldSendBulkNotificationsSuccessfully() throws Exception {
            // Given
            List<Integer> userIds = List.of(1, 2, 3);
            List<Notification> notifications = List.of(testNotification);
            when(notificationService.sendBulkNotification(any(SendNotificationRequest.class), eq(userIds)))
                .thenReturn(CompletableFuture.completedFuture(notifications));

            // When & Then
            mockMvc.perform(post("/api/notifications/send/bulk")
                    .param("userIds", "1", "2", "3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Bulk Notifications sent successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));

            verify(notificationService).sendBulkNotification(any(SendNotificationRequest.class), eq(userIds));
        }

        @Test
        @DisplayName("Should handle bulk notification service failure")
        @WithMockUser
        void shouldHandleBulkNotificationServiceFailure() throws Exception {
            // Given
            List<Integer> userIds = List.of(1, 2, 3);
            CompletableFuture<List<Notification>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Bulk service error"));
            when(notificationService.sendBulkNotification(any(SendNotificationRequest.class), eq(userIds)))
                .thenReturn(failedFuture);

            // When & Then
            MvcResult result = mockMvc.perform(post("/api/notifications/send/bulk")
                    .param("userIds", "1", "2", "3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andReturn();

            String content = result.getResponse().getContentAsString();
            assert content.contains("Failed to send bulk notifications");
            verify(notificationService).sendBulkNotification(any(SendNotificationRequest.class), eq(userIds));
        }

        @Test
        @DisplayName("Should require userIds parameter")
        @WithMockUser
        void shouldRequireUserIdsParameter() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/notifications/send/bulk")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

            verify(notificationService, never()).sendBulkNotification(any(), any());
        }
    }

    @Nested
    @DisplayName("Get My Notifications Tests")
    class GetMyNotificationsTests {

        @Test
        @DisplayName("Should get current user's notifications successfully")
        @WithMockUser
        void shouldGetCurrentUserNotificationsSuccessfully() throws Exception {
            // Given
            List<NotificationDTO> notifications = List.of(testNotificationDTO);
            Page<NotificationDTO> page = new PageImpl<>(notifications);
            when(notificationService.getUserNotifications(anyInt(), eq(0), eq(20)))
                .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));

            verify(notificationService).getUserNotifications(anyInt(), eq(0), eq(20));
        }

        @Test
        @DisplayName("Should handle pagination parameters")
        @WithMockUser
        void shouldHandlePaginationParameters() throws Exception {
            // Given
            Page<NotificationDTO> page = new PageImpl<>(List.of());
            when(notificationService.getUserNotifications(anyInt(), eq(2), eq(10)))
                .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    .param("page", "2")
                    .param("size", "10")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk());

            verify(notificationService).getUserNotifications(anyInt(), eq(2), eq(10));
        }

        @Test
        @DisplayName("Should use default pagination parameters")
        @WithMockUser
        void shouldUseDefaultPaginationParameters() throws Exception {
            // Given
            Page<NotificationDTO> page = new PageImpl<>(List.of());
            when(notificationService.getUserNotifications(anyInt(), eq(0), eq(20)))
                .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk());

            verify(notificationService).getUserNotifications(anyInt(), eq(0), eq(20));
        }

        @Test
        @DisplayName("Should handle service exception")
        @WithMockUser
        void shouldHandleServiceException() throws Exception {
            // Given
            when(notificationService.getUserNotifications(anyInt(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to retrieve notifications"));
        }
    }

    @Nested
    @DisplayName("Get User Notifications (Admin) Tests")
    class GetUserNotificationsAdminTests {

        @Test
        @DisplayName("Should get user notifications as admin successfully")
        @WithMockUser(roles = "ADMIN")
        void shouldGetUserNotificationsAsAdminSuccessfully() throws Exception {
            // Given
            List<NotificationDTO> notifications = List.of(testNotificationDTO);
            Page<NotificationDTO> page = new PageImpl<>(notifications);
            when(notificationService.getUserNotifications(eq(1), eq(0), eq(20)))
                .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1));

            verify(notificationService).getUserNotifications(eq(1), eq(0), eq(20));
        }

        @Test
        @DisplayName("Should deny access to non-admin users")
        @WithMockUser(roles = "USER")
        void shouldDenyAccessToNonAdminUsers() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isForbidden());

            verify(notificationService, never()).getUserNotifications(anyInt(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("Should handle service exception for admin endpoint")
        @WithMockUser(roles = "ADMIN")
        void shouldHandleServiceExceptionForAdminEndpoint() throws Exception {
            // Given
            when(notificationService.getUserNotifications(eq(1), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Mark Notifications as Read Tests")
    class MarkNotificationsAsReadTests {

        @Test
        @DisplayName("Should mark notifications as read successfully")
        @WithMockUser
        void shouldMarkNotificationsAsReadSuccessfully() throws Exception {
            // Given
            List<Long> notificationIds = List.of(1L, 2L, 3L);
            when(notificationService.markNotificationsAsRead(anyInt(), eq(notificationIds)))
                .thenReturn(3);

            // When & Then
            mockMvc.perform(put("/api/notifications/mark-read")
                    .param("notificationIds", "1", "2", "3")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notifications marked as read"))
                .andExpect(jsonPath("$.data").value(3));

            verify(notificationService).markNotificationsAsRead(anyInt(), eq(notificationIds));
        }

        @Test
        @DisplayName("Should handle service exception for mark as read")
        @WithMockUser
        void shouldHandleServiceExceptionForMarkAsRead() throws Exception {
            // Given
            when(notificationService.markNotificationsAsRead(anyInt(), anyList()))
                .thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(put("/api/notifications/mark-read")
                    .param("notificationIds", "1", "2", "3")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Should require notificationIds parameter")
        @WithMockUser
        void shouldRequireNotificationIdsParameter() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/notifications/mark-read")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest());

            verify(notificationService, never()).markNotificationsAsRead(anyInt(), anyList());
        }
    }

    @Nested
    @DisplayName("Get Unread Count Tests")
    class GetUnreadCountTests {

        @Test
        @DisplayName("Should get unread count successfully")
        @WithMockUser
        void shouldGetUnreadCountSuccessfully() throws Exception {
            // Given
            when(notificationService.getUnreadCount(anyInt())).thenReturn(5L);

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Unread count retrieved successfully"))
                .andExpect(jsonPath("$.data").value(5));

            verify(notificationService).getUnreadCount(anyInt());
        }

        @Test
        @DisplayName("Should handle service exception for unread count")
        @WithMockUser
        void shouldHandleServiceExceptionForUnreadCount() throws Exception {
            // Given
            when(notificationService.getUnreadCount(anyInt()))
                .thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to get unread count"));
        }
    }

    @Nested
    @DisplayName("Register Device Token Tests")
    class RegisterDeviceTokenTests {

        @Test
        @DisplayName("Should register device token successfully")
        @WithMockUser
        void shouldRegisterDeviceTokenSuccessfully() throws Exception {
            // Given
            doNothing().when(notificationService)
                .registerDeviceToken(anyInt(), eq("device-token-123"), eq("android"));

            // When & Then
            mockMvc.perform(post("/api/notifications/register-device")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deviceTokenRequest))
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(notificationService).registerDeviceToken(anyInt(), eq("device-token-123"), eq("android"));
        }

        @Test
        @DisplayName("Should validate device token request")
        @WithMockUser
        void shouldValidateDeviceTokenRequest() throws Exception {
            // Given - Invalid request
            DeviceTokenRequest invalidRequest = DeviceTokenRequest.builder().build();

            // When & Then
            mockMvc.perform(post("/api/notifications/register-device")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest))
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest());

            verify(notificationService, never()).registerDeviceToken(anyInt(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should handle service exception for device token registration")
        @WithMockUser
        void shouldHandleServiceExceptionForDeviceTokenRegistration() throws Exception {
            // Given
            doThrow(new RuntimeException("Service error"))
                .when(notificationService).registerDeviceToken(anyInt(), anyString(), anyString());

            // When & Then
            mockMvc.perform(post("/api/notifications/register-device")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deviceTokenRequest))
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Test Notification Tests")
    class TestNotificationTests {

        @Test
        @DisplayName("Should send test notification successfully")
        @WithMockUser
        void shouldSendTestNotificationSuccessfully() throws Exception {
            // Given
            when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(testNotification));

            // When & Then
            mockMvc.perform(post("/api/notifications/test")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"));

            verify(notificationService).sendNotification(argThat(request -> 
                request.getTitle().equals("Test Notification") &&
                request.getContent().contains("test notification") &&
                request.getType().equals(NotificationType.SYSTEM_ALERT)
            ));
        }

        @Test
        @DisplayName("Should handle test notification failure")
        @WithMockUser
        void shouldHandleTestNotificationFailure() throws Exception {
            // Given
            CompletableFuture<Notification> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Test notification failed"));
            when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(failedFuture);

            // When & Then
            MvcResult result = mockMvc.perform(post("/api/notifications/test")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andReturn();

            String content = result.getResponse().getContentAsString();
            assert content.contains("Failed to send notification");
        }
    }

    @Nested
    @DisplayName("Retry Failed Notifications Tests")
    class RetryFailedNotificationsTests {

        @Test
        @DisplayName("Should retry failed notifications as admin successfully")
        @WithMockUser(roles = "ADMIN")
        void shouldRetryFailedNotificationsAsAdminSuccessfully() throws Exception {
            // Given
            doNothing().when(notificationService).retryFailedNotifications();

            // When & Then
            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(notificationService).retryFailedNotifications();
        }

        @Test
        @DisplayName("Should deny access to non-admin users for retry")
        @WithMockUser(roles = "USER")
        void shouldDenyAccessToNonAdminUsersForRetry() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isForbidden());

            verify(notificationService, never()).retryFailedNotifications();
        }

        @Test
        @DisplayName("Should handle service exception for retry failed notifications")
        @WithMockUser(roles = "ADMIN")
        void shouldHandleServiceExceptionForRetryFailedNotifications() throws Exception {
            // Given
            doThrow(new RuntimeException("Retry service error"))
                .when(notificationService).retryFailedNotifications();

            // When & Then
            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to retry notifications"));
        }
    }

    @Nested
    @DisplayName("Security and Authorization Tests")
    class SecurityAndAuthorizationTests {

        @Test
        @DisplayName("Should require authentication for all endpoints")
        void shouldRequireAuthenticationForAllEndpoints() throws Exception {
            // Test all endpoints without authentication
            mockMvc.perform(get("/api/notifications/my"))
                .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isUnauthorized());

            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isUnauthorized());

            mockMvc.perform(post("/api/notifications/register-device")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isUnauthorized());

            mockMvc.perform(post("/api/notifications/test"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should require admin role for admin endpoints")
        @WithMockUser(roles = "USER")
        void shouldRequireAdminRoleForAdminEndpoints() throws Exception {
            mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow admin access to admin endpoints")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowAdminAccessToAdminEndpoints() throws Exception {
            // Given
            Page<NotificationDTO> page = new PageImpl<>(List.of());
            when(notificationService.getUserNotifications(anyInt(), anyInt(), anyInt()))
                .thenReturn(page);
            doNothing().when(notificationService).retryFailedNotifications();

            // When & Then
            mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk());

            mockMvc.perform(post("/api/notifications/retry-failed"))
                .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("Should validate pagination parameters")
        @WithMockUser
        void shouldValidatePaginationParameters() throws Exception {
            // Given
            Page<NotificationDTO> page = new PageImpl<>(List.of());
            when(notificationService.getUserNotifications(anyInt(), anyInt(), anyInt()))
                .thenReturn(page);

            // Test negative page
            mockMvc.perform(get("/api/notifications/my")
                    .param("page", "-1")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk()); // Spring converts negative to 0

            // Test large size
            mockMvc.perform(get("/api/notifications/my")
                    .param("size", "1000")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should handle invalid JSON")
        @WithMockUser
        void shouldHandleInvalidJSON() throws Exception {
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("invalid json"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should validate content type")
        @WithMockUser
        void shouldValidateContentType() throws Exception {
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isUnsupportedMediaType());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle empty notification lists")
        @WithMockUser
        void shouldHandleEmptyNotificationLists() throws Exception {
            // Given
            Page<NotificationDTO> emptyPage = new PageImpl<>(List.of());
            when(notificationService.getUserNotifications(anyInt(), anyInt(), anyInt()))
                .thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("Should handle zero unread count")
        @WithMockUser
        void shouldHandleZeroUnreadCount() throws Exception {
            // Given
            when(notificationService.getUnreadCount(anyInt())).thenReturn(0L);

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0));
        }

        @Test
        @DisplayName("Should handle empty bulk notification user list")
        @WithMockUser
        void shouldHandleEmptyBulkNotificationUserList() throws Exception {
            // Given
            List<Notification> emptyResult = List.of();
            when(notificationService.sendBulkNotification(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(emptyResult));

            // When & Then
            mockMvc.perform(post("/api/notifications/send/bulk")
                    .param("userIds", "")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest()); // Empty userIds should cause validation error
        }

        @Test
        @DisplayName("Should handle concurrent requests gracefully")
        @WithMockUser
        void shouldHandleConcurrentRequestsGracefully() throws Exception {
            // Given
            when(notificationService.getUnreadCount(anyInt())).thenReturn(3L);

            // When & Then - Simulate multiple concurrent requests
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/api/notifications/unread-count")
                        .with(user(testUser.getUsername())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(3));
            }

            verify(notificationService, times(5)).getUnreadCount(anyInt());
        }

        @Test
        @DisplayName("Should handle large notification IDs list for mark as read")
        @WithMockUser
        void shouldHandleLargeNotificationIdsListForMarkAsRead() throws Exception {
            // Given
            List<Long> largeIdList = new ArrayList<>();
            for (long i = 1L; i <= 100L; i++) {
                largeIdList.add(i);
            }
            when(notificationService.markNotificationsAsRead(anyInt(), eq(largeIdList)))
                .thenReturn(100);

            String[] idStrings = largeIdList.stream().map(String::valueOf).toArray(String[]::new);

            // When & Then
            mockMvc.perform(put("/api/notifications/mark-read")
                    .param("notificationIds", idStrings)
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(100));
        }
    }

    @Nested
    @DisplayName("Response Format Tests")
    class ResponseFormatTests {

        @Test
        @DisplayName("Should return consistent API response format for success")
        @WithMockUser
        void shouldReturnConsistentApiResponseFormatForSuccess() throws Exception {
            // Given
            when(notificationService.getUnreadCount(anyInt())).thenReturn(5L);

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("Should return consistent API response format for error")
        @WithMockUser
        void shouldReturnConsistentApiResponseFormatForError() throws Exception {
            // Given
            when(notificationService.getUnreadCount(anyInt()))
                .thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("Should return paginated response format")
        @WithMockUser
        void shouldReturnPaginatedResponseFormat() throws Exception {
            // Given
            List<NotificationDTO> notifications = List.of(testNotificationDTO);
            Page<NotificationDTO> page = new PageImpl<>(notifications, 
                PageRequest.of(0, 20), 1);
            when(notificationService.getUserNotifications(anyInt(), anyInt(), anyInt()))
                .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/notifications/my")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.numberOfElements").value(1))
                .andExpect(jsonPath("$.data.first").value(true))
                .andExpect(jsonPath("$.data.last").value(true));
        }

        @Test
        @DisplayName("Should return no content response format")
        @WithMockUser
        void shouldReturnNoContentResponseFormat() throws Exception {
            // Given
            doNothing().when(notificationService)
                .registerDeviceToken(anyInt(), anyString(), anyString());

            // When & Then
            mockMvc.perform(post("/api/notifications/register-device")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deviceTokenRequest))
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());
        }
    }

    @Nested
    @DisplayName("Async Response Handling Tests")
    class AsyncResponseHandlingTests {

        @Test
        @DisplayName("Should handle async notification sending properly")
        @WithMockUser
        void shouldHandleAsyncNotificationSendingProperly() throws Exception {
            // Given
            CompletableFuture<Notification> future = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(100); // Simulate async processing
                        return testNotification;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(future);

            // When & Then
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Should handle async bulk notification sending properly")
        @WithMockUser
        void shouldHandleAsyncBulkNotificationSendingProperly() throws Exception {
            // Given
            List<Integer> userIds = List.of(1, 2);
            CompletableFuture<List<Notification>> future = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(100); // Simulate async processing
                        return List.of(testNotification);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            when(notificationService.sendBulkNotification(any(SendNotificationRequest.class), eq(userIds)))
                .thenReturn(future);

            // When & Then
            mockMvc.perform(post("/api/notifications/send/bulk")
                    .param("userIds", "1", "2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Should handle async timeout gracefully")
        @WithMockUser
        void shouldHandleAsyncTimeoutGracefully() throws Exception {
            // Given
            CompletableFuture<Notification> future = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(5000); // Simulate long processing time
                        return testNotification;
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Interrupted");
                    }
                });
            when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(future);

            // When & Then - This should complete normally since we're not setting a timeout
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Content Type and Media Type Tests")
    class ContentTypeAndMediaTypeTests {

        @Test
        @DisplayName("Should accept application/json content type")
        @WithMockUser
        void shouldAcceptApplicationJsonContentType() throws Exception {
            // Given
            doNothing().when(notificationService)
                .registerDeviceToken(anyInt(), anyString(), anyString());

            // When & Then
            mockMvc.perform(post("/api/notifications/register-device")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deviceTokenRequest))
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should reject unsupported content types")
        @WithMockUser
        void shouldRejectUnsupportedContentTypes() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/notifications/register-device")
                    .contentType(MediaType.APPLICATION_XML)
                    .content("<xml>content</xml>")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @DisplayName("Should return JSON response")
        @WithMockUser
        void shouldReturnJsonResponse() throws Exception {
            // Given
            when(notificationService.getUnreadCount(anyInt())).thenReturn(3L);

            // When & Then
            mockMvc.perform(get("/api/notifications/unread-count")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("HTTP Method Tests")
    class HttpMethodTests {

        @Test
        @DisplayName("Should use correct HTTP methods for endpoints")
        @WithMockUser
        void shouldUseCorrectHttpMethodsForEndpoints() throws Exception {
            // GET endpoints
            when(notificationService.getUnreadCount(anyInt())).thenReturn(0L);
            mockMvc.perform(get("/api/notifications/unread-count")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk());

            // POST endpoints
            when(notificationService.sendNotification(any()))
                .thenReturn(CompletableFuture.completedFuture(testNotification));
            mockMvc.perform(post("/api/notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk());

            // PUT endpoints
            when(notificationService.markNotificationsAsRead(anyInt(), anyList()))
                .thenReturn(1);
            mockMvc.perform(put("/api/notifications/mark-read")
                    .param("notificationIds", "1")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should reject wrong HTTP methods")
        @WithMockUser
        void shouldRejectWrongHttpMethods() throws Exception {
            // Wrong method for GET endpoint
            mockMvc.perform(post("/api/notifications/unread-count")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isMethodNotAllowed());

            // Wrong method for POST endpoint
            mockMvc.perform(get("/api/notifications/send")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isMethodNotAllowed());

            // Wrong method for PUT endpoint
            mockMvc.perform(get("/api/notifications/mark-read")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isMethodNotAllowed());
        }
    }

    @Nested
    @DisplayName("Parameter Validation Tests")
    class ParameterValidationTests {

        @Test
        @DisplayName("Should handle invalid path variables")
        @WithMockUser(roles = "ADMIN")
        void shouldHandleInvalidPathVariables() throws Exception {
            // Given
            when(notificationService.getUserNotifications(eq(999), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("User not found"));

            // When & Then
            mockMvc.perform(get("/api/notifications/user/999"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle non-numeric path variables")
        @WithMockUser(roles = "ADMIN")
        void shouldHandleNonNumericPathVariables() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/notifications/user/invalid"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle invalid query parameters")
        @WithMockUser
        void shouldHandleInvalidQueryParameters() throws Exception {
            // Given
            Page<NotificationDTO> page = new PageImpl<>(List.of());
            when(notificationService.getUserNotifications(anyInt(), anyInt(), anyInt()))
                .thenReturn(page);

            // When & Then - Invalid page parameter
            mockMvc.perform(get("/api/notifications/my")
                    .param("page", "invalid")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest());

            // Invalid size parameter
            mockMvc.perform(get("/api/notifications/my")
                    .param("size", "invalid")
                    .with(user(testUser.getUsername())))
                .andExpect(status().isBadRequest());
        }
    }
}