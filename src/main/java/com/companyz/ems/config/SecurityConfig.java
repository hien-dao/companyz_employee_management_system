package com.companyz.ems.config;

public class SecurityConfig {
    public static int getPasswordStrength() {
        return AppConfig.getInt("security.password.hash.strength");
    }

    public static int getSessionTimeoutMinutes() {
        return AppConfig.getInt("security.session.timeout.minutes");
    }
}
