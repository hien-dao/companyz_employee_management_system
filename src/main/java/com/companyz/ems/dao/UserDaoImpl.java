package com.companyz.ems.dao;

import com.companyz.ems.model.User;
import com.companyz.ems.model.Role;
import com.companyz.ems.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl extends AbstractDao implements UserDao {

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT user_id, username, password_hash, password_salt, is_active, created_at, updated_at " +
                     "FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, userId);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                User user = mapUser(rs, conn);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // replace with logger
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, password_salt, is_active, created_at, updated_at " +
                     "FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, username);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                User user = mapUser(rs, conn);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean createUser(User user, int empId, List<Role> roles) {
        String sql = "INSERT INTO users (username, password_hash, password_salt, is_active, created_at) " +
                     "VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setBytes(2, user.getPasswordHash());
            stmt.setBytes(3, user.getPasswordSalt());
            stmt.setBoolean(4, user.isActive());

            int affected = stmt.executeUpdate();
            if (affected == 0) return false;

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int newUserId = keys.getInt(1);

                // Link to employee
                try (PreparedStatement empStmt = prepareStatement(conn,
                        "INSERT INTO user_employee_link (user_id, empid) VALUES (?, ?)",
                        newUserId, empId)) {
                    empStmt.executeUpdate();
                }

                // Assign roles
                try (PreparedStatement roleStmt = conn.prepareStatement(
                        "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)")) {
                    for (Role role : roles) {
                        roleStmt.setInt(1, newUserId);
                        roleStmt.setInt(2, role.getRoleId());
                        roleStmt.addBatch();
                    }
                    roleStmt.executeBatch();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updatePassword(int userId, byte[] newHash, byte[] newSalt) {
        String sql = "UPDATE users SET password_hash = ?, password_salt = ?, updated_at = NOW() WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, newHash, newSalt, userId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = 0, updated_at = NOW() WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, userId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Role> getUserRoles(int userId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.role_id, r.role_name, r.description " +
                     "FROM roles r JOIN user_roles ur ON r.role_id = ur.role_id " +
                     "WHERE ur.user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, userId);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Role role = new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("description")
                );
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    // Helper method to map User from ResultSet
    private User mapUser(ResultSet rs, Connection conn) throws SQLException {
        int userId = rs.getInt("user_id");
        User user = new User();
        user.setUserId(userId);
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getBytes("password_hash"));
        user.setPasswordSalt(rs.getBytes("password_salt"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        // Attach roles
        user.setRoles(getUserRoles(userId));

        // Attach employee link
        String empSql = "SELECT e.empid, e.fname, e.lname, e.salary " +
                        "FROM employees e JOIN user_employee_link uel ON e.empid = uel.empid " +
                        "WHERE uel.user_id = ?";
        try (PreparedStatement empStmt = prepareStatement(conn, empSql, userId);
             ResultSet empRs = empStmt.executeQuery()) {
            if (empRs.next()) {
                Employee emp = new Employee();
                emp.setEmpId(empRs.getInt("empid"));
                emp.setFname(empRs.getString("fname"));
                emp.setLname(empRs.getString("lname"));
                emp.setSalary(empRs.getBigDecimal("salary"));
                user.setEmployee(emp);
            }
        }
        return user;
    }
}
