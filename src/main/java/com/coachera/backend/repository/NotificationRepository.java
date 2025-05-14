package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByIdDesc(Integer userId);
    List<Notification> findByUserIdAndIsRead(Integer userId, Boolean isRead);
    long countByUserIdAndIsRead(Integer userId, Boolean isRead);
}
