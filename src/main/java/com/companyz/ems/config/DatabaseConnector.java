package com.companyz.ems.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnector {

    private static String url;
    private static String user;
    private static String password;
    private static String driver;
    private static boolean initialized = false;

    private DatabaseConnector() {}

    private static void loadConfig() {
        if (initialized) return;

        try (InputStream input = DatabaseConnector.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            Properties props = new Properties();
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.username");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver");

            Class.forName(driver);
            initialized = true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB config", e);
        }
    }

    public static Connection getConnection() {
        loadConfig();
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to open DB connection", e);
        }
    }
}
