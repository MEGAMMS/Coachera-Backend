package com.coachera.backend.generator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.coachera.backend.entity.User;

@Component
public class UserGenerator {
      

    private final PasswordEncoder passwordEncoder; // Non-static

    public UserGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> generate(int count) {

        // AtomicInteger emailCounter = new AtomicInteger(1);
        AtomicInteger usernameCounter = new AtomicInteger(1);


        return Instancio.ofList(User.class).size(count)
                .ignore(Select.field(User::getId)) // ← prevent random ID generation
                .ignore(Select.field(User::getEmail))
                .supply(Select.field(User::getUsername),
                        () -> "user" + usernameCounter.getAndIncrement())
                 .supply(Select.field(User::getPassword), 
                        () -> passwordEncoder.encode("password")) 
                .set(Select.field(User::getRole), "ADMIN")
                .supply(Select.field(User::getIsVerified), () -> true)
                .ignore(Select.field(User::getProfileImage))
                .ignore(Select.field(User::getOrganization))
                .ignore(Select.field(User::getInstructor))
                .ignore(Select.field(User::getStudent))
                .create();
    }
}
