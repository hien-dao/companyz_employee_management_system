package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.employee.EmploymentStatus;

public class EmploymentStatusDaoImpl extends AbstractDao implements EmploymentStatusDao {

    @Override
    public Optional<EmploymentStatus> findById(int statusId) {
        String sql = "SELECT * FROM employee_status WHERE status_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, statusId);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapStatus(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<EmploymentStatus> findByName(String statusName) {
        String sql = "SELECT * FROM employee_status WHERE status_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, statusName);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapStatus(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<EmploymentStatus> findAll() {
        List<EmploymentStatus> statuses = new ArrayList<>();
        String sql = "SELECT * FROM employee_status";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statuses.add(mapStatus(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuses;
    }

    @Override
    public EmploymentStatus createStatus(EmploymentStatus status) {
        String sql = "INSERT INTO employee_status (status_name, description, effective_start, effective_end) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.getStatusName());
            stmt.setString(2, status.getDescription());
            stmt.setDate(3, status.getEffectiveStart() != null ? Date.valueOf(status.getEffectiveStart()) : null);
            stmt.setDate(4, status.getEffectiveEnd() != null ? Date.valueOf(status.getEffectiveEnd()) : null);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    status.setStatusId(generatedKeys.getInt(1));
                }
            }
            return status;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public EmploymentStatus updateStatus(EmploymentStatus status) {
        String sql = "UPDATE employee_status SET status_name = ?, description = ?, effective_start = ?, effective_end = ? WHERE status_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.getStatusName());
            stmt.setString(2, status.getDescription());
            stmt.setDate(3, status.getEffectiveStart() != null ? Date.valueOf(status.getEffectiveStart()) : null);
            stmt.setDate(4, status.getEffectiveEnd() != null ? Date.valueOf(status.getEffectiveEnd()) : null);
            stmt.setInt(5, status.getStatusId());
            stmt.executeUpdate();
            return status;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteStatus(int statusId) {
        String sql = "DELETE FROM employee_status WHERE status_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, statusId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private EmploymentStatus mapStatus(ResultSet rs) throws SQLException {
        return new EmploymentStatus(
            rs.getInt("status_id"),
            rs.getString("status_name"),
            rs.getString("description"),
            rs.getDate("effective_start") != null ? rs.getDate("effective_start").toLocalDate() : null,
            rs.getDate("effective_end") != null ? rs.getDate("effective_end").toLocalDate() : null
        );
    }
}
