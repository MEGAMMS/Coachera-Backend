package com.coachera.backend.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;

public class OrganizationGenerator {

    public static List<Organization> fromUsers(List<User> users) {
        return users.stream()
            .map(user -> {
                try {
                    Organization organization = Instancio.of(Organization.class)
                        .ignore(Select.field(Organization::getId))
                        .supply(Select.field(Organization::getUser), () -> user)
                        .supply(Select.field(Organization::getOrgName), () -> "Organization " + user.getId())
                        .supply(Select.field(Organization::getOrgDescription), () -> "Description for Organization " + user.getId())
                        .create();
                    
                    if (organization == null) {
                        throw new IllegalStateException("Instancio returned null Organization");
                    }
                    return organization;
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to create Organization for user " + user.getId(), e);
                }
            })
            .collect(Collectors.toList());
    }
}