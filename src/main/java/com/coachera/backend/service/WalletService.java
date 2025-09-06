package com.coachera.backend.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.Wallet;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet createWallet(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet getOrCreateWallet(User user) {
        Optional<Wallet> existingWallet = walletRepository.findByUser(user);
        if (existingWallet.isPresent()) {
            return existingWallet.get();
        }
        return createWallet(user);
    }

    @Transactional
    public Wallet addMoney(User user, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(user);
        wallet.addMoney(amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public boolean payForCourse(User user, Course course) {
        Wallet wallet = getOrCreateWallet(user);
        
        if (wallet.hasSufficientBalance(course.getPrice())) {
            wallet.deductMoney(course.getPrice());
            walletRepository.save(wallet);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean hasSufficientBalance(User user, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(user);
        return wallet.hasSufficientBalance(amount);
    }

    public BigDecimal getBalance(User user) {
        Wallet wallet = getOrCreateWallet(user);
        return wallet.getBalance();
    }

    public Wallet getWallet(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + user.getId()));
    }
}
