package com.coachera.backend.generator;

import java.util.List;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.User;

public class UserGenerator {
    public static List<User> generate(int count) {
        return Instancio.ofList(User.class).size(count)
                .supply(Select.field(User::getPassword), () -> "password123")
                .supply(Select.field(User::getIsVerified), () -> true)
                .create();
    }
}
