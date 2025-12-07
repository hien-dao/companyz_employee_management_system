package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthEventLogger extends AbstractDao {

    public void logEvent(int userId, String eventType, String ipAddress, String userAgent) {
        String sql = "INSERT INTO auth_events (user_id, event_type, event_time, ip_address, user_agent) " +
                     "VALUES (?, ?, NOW(), ?, ?)";
        try (Connection conn = getConnection();
             var stmt = prepareStatement(conn, sql, userId, eventType, ipAddress, userAgent)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // replace with proper logging
        }
    }
}
