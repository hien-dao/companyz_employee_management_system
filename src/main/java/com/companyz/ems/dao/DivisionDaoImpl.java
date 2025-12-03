package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Address;
import com.companyz.ems.model.Division;

/**
 * JDBC implementation of DivisionDao.
 * Provides CRUD operations for divisions and hydrates Address objects.
 */
public class DivisionDaoImpl extends AbstractDao implements DivisionDao {

    @Override
    public Optional<Division> findById(int divisionId) {
        String sql = "SELECT division_id, division_name, description, is_active, " +
                     "address_line1, address_line2, city, state, country, postal_code " +
                     "FROM divisions WHERE division_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, divisionId);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapDivision(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // replace with logger
        }
        return Optional.empty();
    }

    @Override
    public List<Division> findAll() {
        List<Division> divisions = new ArrayList<>();
        String sql = "SELECT division_id, division_name, description, is_active, " +
                     "address_line1, address_line2, city, state, country, postal_code " +
                     "FROM divisions";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                divisions.add(mapDivision(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisions;
    }

    @Override
    public boolean createDivision(Division division) {
        String sql = "INSERT INTO divisions (division_name, description, is_active, " +
                     "address_line1, address_line2, city, state, country, postal_code) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, division.getDivisionName());
            stmt.setString(2, division.getDescription());
            stmt.setBoolean(3, division.getIsActive() != null ? division.getIsActive() : true);

            Address addr = division.getAddress();
            stmt.setString(4, addr != null ? addr.getAddressLine1() : null);
            stmt.setString(5, addr != null ? addr.getAddressLine2() : null);
            stmt.setString(6, addr != null ? addr.getCity() : null);
            stmt.setString(7, addr != null ? addr.getState() : null);
            stmt.setString(8, addr != null ? addr.getCountry() : null);
            stmt.setString(9, addr != null ? addr.getPostalCode() : null);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDivision(Division division) {
        String sql = "UPDATE divisions SET division_name = ?, description = ?, is_active = ?, " +
                     "address_line1 = ?, address_line2 = ?, city = ?, state = ?, country = ?, postal_code = ? " +
                     "WHERE division_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, division.getDivisionName());
            stmt.setString(2, division.getDescription());
            stmt.setBoolean(3, division.getIsActive() != null ? division.getIsActive() : true);

            Address addr = division.getAddress();
            stmt.setString(4, addr != null ? addr.getAddressLine1() : null);
            stmt.setString(5, addr != null ? addr.getAddressLine2() : null);
            stmt.setString(6, addr != null ? addr.getCity() : null);
            stmt.setString(7, addr != null ? addr.getState() : null);
            stmt.setString(8, addr != null ? addr.getCountry() : null);
            stmt.setString(9, addr != null ? addr.getPostalCode() : null);

            stmt.setInt(10, division.getDivisionId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDivision(int divisionId) {
        String sql = "DELETE FROM divisions WHERE division_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, divisionId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Maps a ResultSet row into a Division object with embedded Address.
     */
    private Division mapDivision(ResultSet rs) throws SQLException {
        Division division = new Division();
        division.setDivisionId(rs.getInt("division_id"));
        division.setDivisionName(rs.getString("division_name"));
        division.setDescription(rs.getString("description"));
        division.setIsActive(rs.getBoolean("is_active"));

        Address address = new Address();
        address.setAddressLine1(rs.getString("address_line1"));
        address.setAddressLine2(rs.getString("address_line2"));
        address.setCity(rs.getString("city"));
        address.setState(rs.getString("state"));
        address.setCountry(rs.getString("country"));
        address.setPostalCode(rs.getString("postal_code"));
        division.setAddress(address);

        return division;
    }
}
