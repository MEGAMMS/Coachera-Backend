package com.coachera.backend.seeder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.Admin;
import com.coachera.backend.entity.User;
import com.coachera.backend.repository.AdminRepository;
import com.coachera.backend.repository.UserRepository;

import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.boot.CommandLineRunner;

@Component
public class AdminSeeder implements CommandLineRunner{

    private final UserRepository userRepo;
    private final AdminRepository adminRepo;

    public AdminSeeder(UserRepository userRepo, AdminRepository adminRepo)
    {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        User user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .set(Select.field(User::getEmail),"admin@example.com")
                .set(Select.field(User::getUsername),"admin")
                .supply(Select.field(User::getPassword),() -> "admin1234")
                .set(Select.field(User::getRole),"ADMIN")
                .set(Select.field(User::getIsVerified),true)
                .ignore(Select.field(User::getProfileImage))
                .create();

        userRepo.save(user);
        
        Admin admin = Instancio.of(Admin.class)
                .ignore(Select.field(Admin::getId))
                .set(Select.field(Admin::getAdminName),"System Admin")
                .set(Select.field(Admin::getUser), user)
                .create();

        adminRepo.save(admin);

    }
    
}
