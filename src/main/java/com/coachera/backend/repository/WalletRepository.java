package com.coachera.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.User;
import com.coachera.backend.entity.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Optional<Wallet> findByUser(User user);
    Optional<Wallet> findByUserId(Integer userId);
}
