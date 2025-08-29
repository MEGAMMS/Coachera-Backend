package com.coachera.backend.generator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;

public class OrganizationGenerator {

    private static final Random random = new Random();

    private static final String[] ORG_TYPES = {
        "Institute", "Academy", "Center", "School", "Company", "Foundation", "Studio", "Association"
    };

    private static final String[] DOMAINS = {
        "Technology", "Health", "Education", "Design", "Finance", "Sports", "Music", "Science"
    };

    private static final String[] MISSIONS = {
        "empowering communities", 
        "innovating for the future", 
        "delivering world-class training", 
        "advancing research and development",
        "supporting lifelong learning", 
        "building sustainable solutions"
    };

    private static final String[] VALUES = {
        "collaboration", "excellence", "innovation", "integrity", "creativity", "sustainability"
    };

    public static List<Organization> fromUsers(List<User> users) {
        return users.stream()
            .map(user -> {
                try {
                    Organization organization = Instancio.of(Organization.class)
                        .ignore(Select.field(Organization::getId))
                        .supply(Select.field(Organization::getUser), () -> user)
                        .supply(Select.field(Organization::getOrgName), () -> generateOrgTitle())
                        .supply(Select.field(Organization::getOrgDescription), () -> generateOrgDescription())
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

    private static String generateOrgTitle() {
        return ORG_TYPES[random.nextInt(ORG_TYPES.length)] + " of " + 
               DOMAINS[random.nextInt(DOMAINS.length)];
    }

    // Generates descriptions with a mission + values
    private static String generateOrgDescription() {
        String domain = DOMAINS[random.nextInt(DOMAINS.length)].toLowerCase();
        String mission = MISSIONS[random.nextInt(MISSIONS.length)];
        String value = VALUES[random.nextInt(VALUES.length)];
        return String.format(
            "We are a leading organization in the field of %s, dedicated to %s while upholding our core value of %s.",
            domain, mission, value
        );
    }
}