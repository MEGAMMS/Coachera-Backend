package com.coachera.backend.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Admin;
import com.coachera.backend.entity.User;

public class AdminGenerator {
    public static List<Admin> fromUsers(List<User> users) {
        return users.stream().map(user ->
            Instancio.of(Admin.class)
                .ignore(Select.field(Admin::getId)) // ← prevent random ID generation
                .supply(Select.field(Admin::getUser), () -> user)
                .supply(Select.field(Admin::getAdminName), () -> "System Admin")
                .create()
        ).collect(Collectors.toList());
    }
}
