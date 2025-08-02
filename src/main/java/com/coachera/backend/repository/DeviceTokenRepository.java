package com.coachera.backend.repository;

import com.coachera.backend.entity.DeviceToken;
import com.coachera.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findByUser(User user);
    Optional<DeviceToken> findByToken(String token);
}
