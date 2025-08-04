package com.coachera.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coachera.backend.dto.SendNotificationRequest;
import com.coachera.backend.entity.Notification;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.NotificationStatus;
import com.coachera.backend.entity.enums.NotificationType;
import com.coachera.backend.repository.DeviceTokenRepository;
import com.coachera.backend.repository.NotificationRepository;
import com.coachera.backend.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private DeviceTokenRepository deviceTokenRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private WebPushService webPushService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private SendNotificationRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1)
            .email("test@example.com")
            .username("Test")
            .build();

        testRequest = SendNotificationRequest.builder()
            .userId(1)
            .type(NotificationType.SYSTEM_ALERT)
            .title("Test Notification")
            .content("This is a test notification")
            .actionUrl("/dashboard")
            // .channels(List.of("mobile", "web"))
            .build();
    }

    @Test
    void sendNotification_Success() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        
        Notification mockNotification = Notification.builder()
            .id(1L)
            .recipient(testUser)
            .type(NotificationType.SYSTEM_ALERT)
            .title("Test Notification")
            .content("This is a test notification")
            .status(NotificationStatus.PENDING)
            .build();
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        // Act
        CompletableFuture<Notification> result = notificationService.sendNotification(testRequest);
        Notification notification = result.join();

        // Assert
        assertNotNull(notification);
        assertEquals("Test Notification", notification.getTitle());
        assertEquals(testUser, notification.getRecipient());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void sendNotification_UserNotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        CompletableFuture<Notification> result = notificationService.sendNotification(testRequest);
        
        assertThrows(RuntimeException.class, () -> result.join());
    }

    @Test
    void getUnreadCount_Success() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(notificationRepository.countByRecipientAndReadFalse(testUser)).thenReturn(5L);

        // Act
        long count = notificationService.getUnreadCount(1);

        // Assert
        assertEquals(5L, count);
    }

    @Test
    void markNotificationsAsRead_Success() {
        // Arrange
        List<Long> notificationIds = List.of(1L, 2L, 3L);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(notificationRepository.markAsRead(notificationIds, testUser)).thenReturn(3);

        // Act
        int updatedCount = notificationService.markNotificationsAsRead(1, notificationIds);

        // Assert
        assertEquals(3, updatedCount);
    }
}