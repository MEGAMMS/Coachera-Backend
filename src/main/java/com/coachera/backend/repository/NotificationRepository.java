package com.coachera.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coachera.backend.entity.Notification;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.NotificationStatus;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find notifications by recipient with pagination
    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    // Count unread notifications for a user
    long countByRecipientAndReadFalse(User recipient);

    // Find notifications by status
    List<Notification> findByStatus(NotificationStatus status);

    // Find notifications that need retry (failed and not too old)
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.sentAt > :cutoff")
    List<Notification> findFailedNotificationsForRetry(@Param("cutoff") LocalDateTime cutoff);

    // Mark notifications as read
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id IN :ids AND n.recipient = :user")
    int markAsRead(@Param("ids") List<Long> notificationIds, @Param("user") User user);

    // Find web push subscriptions by user
    @Query("SELECT n.webPushSubscriptionJson FROM Notification n WHERE n.recipient = :user AND n.webPushSubscriptionJson IS NOT NULL")
    List<String> findWebPushSubscriptionsByUser(@Param("user") User user);
}