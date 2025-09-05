package com.coachera.backend.cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.coachera.backend.seeder.AdminSeeder;
import com.coachera.backend.seeder.CoacheraOrgSeeder;
import com.coachera.backend.seeder.DatabaseSeeder;

import lombok.AllArgsConstructor;

@ShellComponent
@AllArgsConstructor
public class SeederShell {

    private final DatabaseSeeder seeder;
    private final AdminSeeder adminSeeder;
    private final CoacheraOrgSeeder coacheraOrgSeeder;

    @ShellMethod(key = "seed-db", value = "Seed the database with initial data.")
    public String seedDatabase() throws Exception {
        adminSeeder.run();
        coacheraOrgSeeder.run();
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
