package com.companyz.ems.dao.helper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.companyz.ems.model.Contact;
import com.companyz.ems.model.employee.BaseEmployee;

/**
 * EmployeePersistenceHelper
 * Encapsulates persistence logic for related employee tables:
 * contacts, demographics, division, job title, and status.
 * Used internally by EmployeeDaoImpl.
 */
public class EmployeePersistenceHelper {

    // --- CONTACTS ---
    public static void saveContacts(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "INSERT INTO employee_contact (empid, contact_type_id, contact_value, is_primary) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Contact c : emp.getContacts()) {
                stmt.setInt(1, emp.getEmpId());
                stmt.setInt(2, lookupContactTypeId(conn, c.getContactType()));
                stmt.setString(3, c.getContactValue());
                stmt.setBoolean(4, c.getIsPrimary());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public static void updateContacts(Connection conn, BaseEmployee emp) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM employee_contact WHERE empid=?")) {
            del.setInt(1, emp.getEmpId());
            del.executeUpdate();
        }
        saveContacts(conn, emp);
    }

    public static List<Contact> loadContacts(Connection conn, int empId) throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT c.contact_id, ct.type_name, c.contact_value, c.is_primary " +
                     "FROM employee_contact c JOIN contact_types ct ON c.contact_type_id = ct.contact_type_id " +
                     "WHERE c.empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Contact c = new Contact();
                    c.setContactId(rs.getInt("contact_id"));
                    c.setContactType(rs.getString("type_name"));
                    c.setContactValue(rs.getString("contact_value"));
                    c.setIsPrimary(rs.getBoolean("is_primary"));
                    contacts.add(c);
                }
            }
        }
        return contacts;
    }

    // --- DEMOGRAPHICS ---
    public static void saveDemographics(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "INSERT INTO employee_demographic (empid, gender, race, dob, address_line1, address_line2, city_id, state_id, country_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp.getEmpId());
            stmt.setString(2, emp.getGender());
            stmt.setString(3, emp.getRace());
            stmt.setDate(4, emp.getDob() != null ? Date.valueOf(emp.getDob()) : null);
            stmt.setString(5, emp.getAddress().getAddressLine1());
            stmt.setString(6, emp.getAddress().getAddressLine2());
            stmt.setInt(7, lookupCityId(conn, emp.getAddress().getCity()));
            stmt.setInt(8, lookupStateId(conn, emp.getAddress().getState()));
            stmt.setInt(9, lookupCountryId(conn, emp.getAddress().getCountry()));
            stmt.executeUpdate();
        }
    }

    public static void updateDemographics(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "UPDATE employee_demographic SET gender=?, race=?, dob=?, address_line1=?, address_line2=?, city_id=?, state_id=?, country_id=? WHERE empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, emp.getGender());
            stmt.setString(2, emp.getRace());
            stmt.setDate(3, emp.getDob() != null ? Date.valueOf(emp.getDob()) : null);
            stmt.setString(4, emp.getAddress().getAddressLine1());
            stmt.setString(5, emp.getAddress().getAddressLine2());
            stmt.setInt(6, lookupCityId(conn, emp.getAddress().getCity()));
            stmt.setInt(7, lookupStateId(conn, emp.getAddress().getState()));
            stmt.setInt(8, lookupCountryId(conn, emp.getAddress().getCountry()));
            stmt.setInt(9, emp.getEmpId());
            stmt.executeUpdate();
        }
    }

    // --- DIVISION ---
    public static void saveDivision(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "INSERT INTO employee_division (empid, divid, effective_start) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp.getEmpId());
            stmt.setInt(2, emp.getDivisionId());
            stmt.setDate(3, Date.valueOf(emp.getHireDate()));
            stmt.executeUpdate();
        }
    }

    public static void updateDivision(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "UPDATE employee_division SET divid=?, effective_start=? WHERE empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp.getDivisionId());
            stmt.setDate(2, Date.valueOf(emp.getHireDate()));
            stmt.setInt(3, emp.getEmpId());
            stmt.executeUpdate();
        }
    }

    public static String loadDivision(Connection conn, int empId) throws SQLException {
        String sql = "SELECT d.name FROM employee_division ed JOIN divisions d ON ed.divid=d.divid WHERE ed.empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("name");
            }
        }
        return null;
    }

    // --- JOB TITLE ---
    public static void saveJobTitle(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "INSERT INTO employee_job_title (empid, job_title_id, effective_start) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp.getEmpId());
            stmt.setInt(2, emp.getJobTitleId());
            stmt.setDate(3, Date.valueOf(emp.getHireDate()));
            stmt.executeUpdate();
        }
    }

    public static void updateJobTitle(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "UPDATE employee_job_title SET job_title_id=?, effective_start=? WHERE empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp.getJobTitleId());
            stmt.setDate(2, Date.valueOf(emp.getHireDate()));
            stmt.setInt(3, emp.getEmpId());
            stmt.executeUpdate();
        }
    }

    public static String loadJobTitle(Connection conn, int empId) throws SQLException {
        String sql = "SELECT j.job_title FROM employee_job_title ej JOIN job_titles j ON ej.job_title_id=j.job_title_id WHERE ej.empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("job_title");
            }
        }
        return null;
    }

    // --- STATUS ---
    public static void saveStatus(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "INSERT INTO employee_status (empid, status_id, hire_date, effective_start) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp.getEmpId());
            stmt.setInt(2, emp.getEmploymentStatusId());
            stmt.setDate(3, Date.valueOf(emp.getHireDate()));
            stmt.setDate(4, Date.valueOf(emp.getHireDate()));
            stmt.executeUpdate();
        }
    }

    public static void updateStatus(Connection conn, BaseEmployee emp) throws SQLException {
        String sql = "UPDATE employee_status SET status_id=?, effective_end=NOW() WHERE empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp.getEmploymentStatusId());
            stmt.setInt(2, emp.getEmpId());
            stmt.executeUpdate();
        }
    }

    public static String loadStatus(Connection conn, int empId) throws SQLException {
        String sql = "SELECT status FROM employee_status WHERE empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("status");
            }
        }
        return null;
    }

    // --- SALARY HISTORY ---
    public static void saveSalaryHistory(Connection conn, BaseEmployee emp, double previousSalary, double newSalary, int changedByUserId) throws SQLException {
        String sql = "INSERT INTO salary_history (empid, previous_salary, new_salary, change_reason, changed_by_user_id, changed_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp.getEmpId());
            stmt.setDouble(2, previousSalary);
            stmt.setDouble(3, newSalary);
            stmt.setString(4, "Update via EmployeeDao"); // or pass reason in
            stmt.setInt(5, changedByUserId);
            stmt.executeUpdate();
        }
    }

    public static List<Double> loadSalaryHistory(Connection conn, int empId) throws SQLException {
        List<Double> salaries = new ArrayList<>();
        String sql = "SELECT new_salary FROM salary_history WHERE empid=? ORDER BY changed_at ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    salaries.add(rs.getDouble("new_salary"));
                }
            }
        }
        return salaries;
    }

    // --- AUDIT / CHANGE LOG ---
    public static void logChange(Connection conn, String tableName, String recordPk, String operation, int userId, String oldValues, String newValues) throws SQLException {
        String sql = "INSERT INTO change_log (table_name, record_pk, operation, changed_by_user_id, changed_at, old_values, new_values) " +
                     "VALUES (?, ?, ?, ?, NOW(), ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            stmt.setString(2, recordPk);
            stmt.setString(3, operation);
            stmt.setInt(4, userId);
            stmt.setString(5, oldValues);
            stmt.setString(6, newValues);
            stmt.executeUpdate();
        }
    }

    // --- LOOKUPS ---
    private static int lookupCityId(Connection conn, String cityName) throws SQLException {
        String sql = "SELECT cityid FROM cities WHERE city_name=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cityName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("cityid");
            }
        }
        return 0;
    }

    private static int lookupStateId(Connection conn, String stateName) throws SQLException {
        String sql = "SELECT stateid FROM states WHERE state_name=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stateName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("stateid");
            }
        }
        return 0;
    }

    private static int lookupCountryId(Connection conn, String countryName) throws SQLException {
        String sql = "SELECT countryid FROM countries WHERE country_name=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, countryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("countryid");
            }
        }
        return 0;
    }

    private static int lookupContactTypeId(Connection conn, String typeName) throws SQLException {
        String sql = "SELECT contact_type_id FROM contact_types WHERE type_name=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, typeName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("contact_type_id");
            }
        }
        return 0;
    }
}
