package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class ChangeLogger extends AbstractDao {

    public void logChange(String tableName, String recordPk, String operation,
                          int changedByUserId, String oldValuesJson, String newValuesJson) {
        String sql = "INSERT INTO change_log (table_name, record_pk, operation, " +
                     "changed_by_user_id, changed_at, old_values, new_values) " +
                     "VALUES (?, ?, ?, ?, NOW(), ?, ?)";
        try (Connection conn = getConnection();
             var stmt = prepareStatement(conn, sql,
                     tableName, recordPk, operation, changedByUserId, oldValuesJson, newValuesJson)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // replace with proper logging
        }
    }
}
