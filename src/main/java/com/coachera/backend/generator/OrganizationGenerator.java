package com.coachera.backend.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;

public class OrganizationGenerator {
    public static List<Organization> fromUsers(List<User> users) {
        return users.stream().map(user ->
            Instancio.of(Organization.class)
                .ignore(Select.field(Organization::getId))
                .supply(Select.field(Organization::getUser), () -> user)
                .create()
        ).collect(Collectors.toList());
    }
}
