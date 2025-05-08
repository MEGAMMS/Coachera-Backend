package com.coachera.backend.cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.coachera.backend.seeder.DatabaseSeeder;

@ShellComponent
public class SeederShell {

    private final DatabaseSeeder seeder;

    public SeederShell(DatabaseSeeder seeder) {
        this.seeder = seeder;
    }

    @ShellMethod(key = "seed-db", value = "Seed the database with initial data.")
    public String seedDatabase() {
        seeder.run();
        return "Database seeded successfully.";
    }

    @ShellMethod(key = "clear-db", value = "Clear the database of all seeded data.")
    public String cleanDatabase() {
        seeder.clean();
        return "Database cleared successfully.";
    }

}

