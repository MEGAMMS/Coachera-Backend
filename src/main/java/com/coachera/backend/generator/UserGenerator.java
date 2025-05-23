package com.coachera.backend.generator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.User;

public class UserGenerator {
    public static List<User> generate(int count) {

        AtomicInteger emailCounter = new AtomicInteger(1);
        AtomicInteger usernameCounter = new AtomicInteger(1);

        return Instancio.ofList(User.class).size(count)
                .ignore(Select.field(User::getId)) // ← prevent random ID generation
                .supply(Select.field(User::getEmail),
                        () -> "user" + emailCounter.getAndIncrement() + "@example.com")
                .supply(Select.field(User::getUsername),
                        () -> "user" + usernameCounter.getAndIncrement())
                .supply(Select.field(User::getPassword), () -> "password123!")
                .set(Select.field(User::getRole), "ADMIN")
                .supply(Select.field(User::getIsVerified), () -> true)
                .supply(Select.field(User::getProfileImage), () -> ImageGenerator.createImage())
                .create();
    }
}
