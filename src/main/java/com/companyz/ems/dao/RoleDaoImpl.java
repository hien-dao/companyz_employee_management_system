package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Role;

public class RoleDaoImpl extends AbstractDao implements RoleDao {

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT role_id, role_name, description FROM roles";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Role(
                    rs.getInt("role_id"),
                    rs.getString("role_name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public Optional<Role> findById(int roleId) {
        String sql = "SELECT role_id, role_name, description FROM roles WHERE role_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, roleId);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(new Role(
                    rs.getInt("role_id"),
                    rs.getString("role_name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean createRole(Role role) {
        String sql = "INSERT INTO roles (role_name, description) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, role.getRoleName(), role.getDescription())) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateRole(Role role) {
        String sql = "UPDATE roles SET role_name = ?, description = ? WHERE role_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, role.getRoleName(), role.getDescription(), role.getRoleId())) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteRole(int roleId) {
        String sql = "DELETE FROM roles WHERE role_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, roleId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
