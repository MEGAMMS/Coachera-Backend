package com.coachera.backend.repository;

import com.coachera.backend.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, String> {
    Optional<AccessToken> findByToken(String token);
    void deleteByUserId(Integer userId);
}
