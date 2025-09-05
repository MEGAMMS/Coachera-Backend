package com.coachera.backend.generator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadLocalRandom;

import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.RoleType;

@Component
public class UserGenerator {

    private final PasswordEncoder passwordEncoder;
    private final AtomicInteger counter = new AtomicInteger(1);

    private static final List<String> FIRST_NAMES = Arrays.asList(
            "Emma", "Liam", "Olivia", "Noah", "Sophia",
            "Jackson", "Ava", "Lucas", "Isabella", "Mason",
            "Mia", "Ethan", "Amelia", "James", "Harper"
    );

    private static final List<String> LAST_NAMES = Arrays.asList(
            "Smith", "Johnson", "Williams", "Brown", "Jones",
            "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson"
    );

    public UserGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private String generateUsername() {
        String first = FIRST_NAMES.get(ThreadLocalRandom.current().nextInt(FIRST_NAMES.size())).toLowerCase();
        String last = LAST_NAMES.get(ThreadLocalRandom.current().nextInt(LAST_NAMES.size())).toLowerCase();
        int uniqueNum = counter.getAndIncrement();
        return first + "." + last + uniqueNum; // e.g. emma.smith1
    }

    public List<User> generate(int count) {
        return Instancio.ofList(User.class).size(count)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getEmail))
                .supply(Select.field(User::getUsername), this::generateUsername)
                .supply(Select.field(User::getPassword), () -> passwordEncoder.encode("password"))
                .set(Select.field(User::getRole), RoleType.ADMIN)
                .supply(Select.field(User::getIsVerified), () -> true)
                .ignore(Select.field(User::getProfileImage))
                .ignore(Select.field(User::getOrganization))
                .ignore(Select.field(User::getInstructor))
                .ignore(Select.field(User::getStudent))
                .ignore(Select.field(User::getDeviceTokens))
                .create();
    }
}
