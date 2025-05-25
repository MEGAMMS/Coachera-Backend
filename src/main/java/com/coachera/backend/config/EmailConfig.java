package com.coachera.backend.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailConfig {
    public static final String EMAIL = Dotenv.load().get("GMAIL_APP_EMAIL");
    public static final String PASSWORD = Dotenv.load().get("GMAIL_APP_PASSWORD");
}
