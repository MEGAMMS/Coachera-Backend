package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.WebPushSubscription;
import com.coachera.backend.entity.User;

public interface WebPushSubscriptionRepository extends JpaRepository<WebPushSubscription, Long> {
    List<WebPushSubscription> findByUser(User user);
}
