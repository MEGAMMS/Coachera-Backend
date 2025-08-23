package com.coachera.backend.seeder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.Admin;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.RoleType;
import com.coachera.backend.repository.AdminRepository;
import com.coachera.backend.repository.UserRepository;

import org.instancio.Instancio;
import org.instancio.Select;
// import org.springframework.boot.ApplicationArguments;
// import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class AdminSeeder {

    private final UserRepository userRepo;
    private final AdminRepository adminRepo;
    private final PasswordEncoder passwordEncoder; // Non-static

    public AdminSeeder(UserRepository userRepo, AdminRepository adminRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // @Override
    @Transactional
    public void run() throws Exception {

        System.out.println(">>> AdminSeeder is running...");

        if (userRepo.findByEmail("admin@gmail.com").isPresent()) {
            return;
        }

        User user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .set(Select.field(User::getEmail), "admin@gmail.com")
                .set(Select.field(User::getUsername), "admin")
                .supply(Select.field(User::getPassword), () -> passwordEncoder.encode("password"))
                .set(Select.field(User::getRole), RoleType.ADMIN)
                .set(Select.field(User::getIsVerified), true)
                .ignore(Select.field(User::getProfileImage))
                .ignore(Select.field(User::getOrganization))
                .ignore(Select.field(User::getStudent))
                .ignore(Select.field(User::getInstructor))
                .ignore(Select.field(User::getDeviceTokens))
                .create();

        userRepo.save(user);

        Admin admin = Instancio.of(Admin.class)
                .ignore(Select.field(Admin::getId))
                .set(Select.field(Admin::getAdminName), "System Admin")
                .set(Select.field(Admin::getUser), user)
                .create();

        adminRepo.save(admin);

    }

}
