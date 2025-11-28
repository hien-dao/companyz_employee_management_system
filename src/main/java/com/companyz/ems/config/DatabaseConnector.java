package com.companyz.ems.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Utility class that provides a single shared JDBC {@link Connection}
 * for the application.
 * <p>
 * The connector reads database connection properties from
 * <code>application.properties</code> on the classpath (keys: <code>db.url</code>,
 * <code>db.username</code>, <code>db.password</code>, <code>db.driver</code>),
 * loads the JDBC driver and opens a connection via {@link DriverManager}.
 * </p>
 * <p>
 * This class implements a simple lazy-initialized singleton connection. The
 * returned {@link Connection} is shared across callers; callers should avoid
 * closing the connection directly. If you need connection pool behaviour
 * or concurrent connection management, replace this implementation with a
 * proper DataSource or connection pool (HikariCP, Apache DBCP, etc.).
 * </p>
 */
public class DatabaseConnector {
    /** Shared JDBC connection instance (lazy-initialized). */
    private static Connection connection;

    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseConnector() {}

    /**
     * Returns a shared {@link Connection} to the configured database.
     * <p>
     * On the first call this method will load <code>application.properties</code>
     * from the classpath, read the expected keys (<code>db.url</code>,
     * <code>db.username</code>, <code>db.password</code>, <code>db.driver</code>)
     * and create a JDBC connection. Subsequent calls return the same
     * {@code Connection} instance.
     * </p>
     *
     * @return shared JDBC {@link Connection}
     * @throws RuntimeException if the properties file is missing, the driver
     *                          cannot be loaded, or the connection cannot be
     *                          established
     */
    public static Connection getConnection() {
        if (connection == null) {
            try (InputStream input = DatabaseConnector.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties")) {

                Properties props = new Properties();
                props.load(input);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.username");
                String password = props.getProperty("db.password");
                String driver = props.getProperty("db.driver");

                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, password);

            } catch (Exception e) {
                // propagate as unchecked exception with original cause
                throw new RuntimeException("Failed to connect to database", e);
            }
        }
        return connection;
    }
}
