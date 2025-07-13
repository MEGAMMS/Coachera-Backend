package com.coachera.backend.cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.coachera.backend.seeder.AdminSeeder;
import com.coachera.backend.seeder.DatabaseSeeder;

@ShellComponent
public class SeederShell {

    private final DatabaseSeeder seeder;
    private final AdminSeeder adminSeeder;

    public SeederShell(DatabaseSeeder seeder, AdminSeeder adminSeeder) {
        this.seeder = seeder;
        this.adminSeeder = adminSeeder;
    }

    @ShellMethod(key = "seed-db", value = "Seed the database with initial data.")
    public String seedDatabase() throws Exception {
        adminSeeder.run();
        seeder.run();
        return "Database seeded successfully.";
    }

    @ShellMethod(key = "seed-admin", value = "Seed the Admin to datatbase.")
    public String seedAdmin() throws Exception {
        adminSeeder.run();
        return "Admin seeded successfully.";
    }

    @ShellMethod(key = "clear-db", value = "Clear the database of all seeded data.")
    public String cleanDatabase() {
        seeder.clean();
        return "Database cleared successfully.";
    }

}
