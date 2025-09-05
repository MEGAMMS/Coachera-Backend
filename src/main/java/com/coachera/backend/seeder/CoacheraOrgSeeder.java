package com.coachera.backend.seeder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.RoleType;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.UserRepository;

import lombok.AllArgsConstructor;

import org.instancio.Instancio;
import org.instancio.Select;
// import org.springframework.boot.ApplicationArguments;
// import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@AllArgsConstructor
public class CoacheraOrgSeeder {
    private final UserRepository userRepo;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder; // Non-static

    @Transactional
    public void run() throws Exception {

        System.out.println(">>> Coachera Seeder is running...");

        if (userRepo.findByEmail("coachera@gmail.com").isPresent()) {
            return;
        }

        User user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .set(Select.field(User::getEmail), "coachera@gmail.com")
                .set(Select.field(User::getUsername), "Coachera.org")
                .supply(Select.field(User::getPassword), () -> passwordEncoder.encode("password"))
                .set(Select.field(User::getRole), RoleType.ORGANIZATION)
                .set(Select.field(User::getIsVerified), true)
                .ignore(Select.field(User::getProfileImage))
                .ignore(Select.field(User::getOrganization))
                .ignore(Select.field(User::getStudent))
                .ignore(Select.field(User::getInstructor))
                .ignore(Select.field(User::getDeviceTokens))
                .create();

        userRepo.save(user);

        Organization org = Instancio.of(Organization.class)
                .ignore(Select.field(Organization::getId))
                .set(Select.field(Organization::getOrgName), "Coachera")
                .set(Select.field(Organization::getUser), user)
                .set(Select.field(Organization::getOrgDescription),
                        "Coachera is a next-generation online learning and coaching platform that blends the flexibility of digital courses with the guidance of personalized coaching. "
                                +
                                "Unlike traditional MOOC platforms like Coursera, Coachera emphasizes a coach-centric approach, where learners not only access structured courses but also receive "
                                +
                                "one-on-one mentorship, feedback, and career guidance from industry professionals. " 
                                +
                                "Our mission is to make skill-building more human, interactive, and transformative by combining world-class learning content with tailored coaching experiences.")
                .create();

        organizationRepository.save(org);

    }
}
