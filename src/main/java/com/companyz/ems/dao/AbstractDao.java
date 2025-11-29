package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.companyz.ems.config.DatabaseConnector;

/**
 * Abstract base class for data access objects (DAOs).
 * <p>
 * Provides common JDBC utility methods for database operations,
 * including connection management and parameterized statement preparation.
 * </p>
 */
public abstract class AbstractDao {
    /**
     * Obtains a database connection from the connection pool.
     *
     * @return a {@link Connection} to the database
     * @throws SQLException if a database access error occurs
     */
    protected Connection getConnection() throws SQLException {
        return DatabaseConnector.getConnection();
    }

    /**
     * Prepares a parameterized SQL statement with the given parameters.
     * <p>
     * Safely binds all parameters to the statement to prevent SQL injection.
     * </p>
     *
     * @param conn the database connection
     * @param sql the SQL query string with placeholders
     * @param params variable arguments to bind to the statement
     * @return a prepared {@link PreparedStatement}
     * @throws SQLException if a database access error occurs
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]); // safely bind all params
        }
        return stmt;
    }
}
