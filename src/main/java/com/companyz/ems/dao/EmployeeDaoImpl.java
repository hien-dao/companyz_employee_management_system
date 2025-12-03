package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Contact;
import com.companyz.ems.model.employee.BaseEmployee;
import com.companyz.ems.model.employee.FullTimeEmployee;

/**
 * JDBC implementation of EmployeeDao.
 * Uses normalized schema: employees + related tables.
 */
public class EmployeeDaoImpl extends AbstractDao implements EmployeeDao {

    @Override
    public Optional<BaseEmployee> findById(int empId) {
        String sql = "SELECT e.empid, e.fname, e.lname, e.salary, e.employment_type_id, " +
                     "d.gender, d.race, d.dob, " +
                     "s.status_id, s.hire_date " +
                     "FROM employees e " +
                     "LEFT JOIN employee_demographic d ON e.empid = d.empid " +
                     "LEFT JOIN employee_status s ON e.empid = s.empid " +
                     "WHERE e.empid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, empId);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapEmployee(rs, conn));
            }
        } catch (SQLException e) {
            logError(e);
        }
        return Optional.empty();
    }

    @Override
    public List<BaseEmployee> findAll() {
        List<BaseEmployee> employees = new ArrayList<>();
        String sql = "SELECT e.empid, e.fname, e.lname, e.salary, e.employment_type_id, " +
                     "d.gender, d.race, d.dob, " +
                     "s.status_id, s.hire_date " +
                     "FROM employees e " +
                     "LEFT JOIN employee_demographic d ON e.empid = d.empid " +
                     "LEFT JOIN employee_status s ON e.empid = s.empid";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapEmployee(rs, conn));
            }
        } catch (SQLException e) {
            logError(e);
        }
        return employees;
    }

    @Override
    public List<BaseEmployee> findByDivision(int divisionId) {
        List<BaseEmployee> employees = new ArrayList<>();
        String sql = "SELECT e.empid, e.fname, e.lname, e.salary, e.employment_type_id, " +
                     "d.gender, d.race, d.dob, " +
                     "s.status_id, s.hire_date " +
                     "FROM employees e " +
                     "JOIN employee_division ed ON e.empid = ed.empid " +
                     "LEFT JOIN employee_demographic d ON e.empid = d.empid " +
                     "LEFT JOIN employee_status s ON e.empid = s.empid " +
                     "WHERE ed.divid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, divisionId);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapEmployee(rs, conn));
            }
        } catch (SQLException e) {
            logError(e);
        }
        return employees;
    }

    @Override
    public List<BaseEmployee> findByJobTitle(int jobTitleId) {
        List<BaseEmployee> employees = new ArrayList<>();
        String sql = "SELECT e.empid, e.fname, e.lname, e.salary, e.employment_type_id, " +
                     "d.gender, d.race, d.dob, " +
                     "s.status_id, s.hire_date " +
                     "FROM employees e " +
                     "JOIN employee_job_title ej ON e.empid = ej.empid " +
                     "LEFT JOIN employee_demographic d ON e.empid = d.empid " +
                     "LEFT JOIN employee_status s ON e.empid = s.empid " +
                     "WHERE ej.job_title_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, jobTitleId);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapEmployee(rs, conn));
            }
        } catch (SQLException e) {
            logError(e);
        }
        return employees;
    }

    @Override
    public BaseEmployee createEmployee(BaseEmployee employee) {
        String sql = "INSERT INTO employees (fname, lname, employment_type_id, salary, ssn_last4, ssn_hash, ssn_enc, ssn_iv, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setInt(3, employee.getEmploymentStatusId()); // employment_type_id
            stmt.setDouble(4, employee instanceof FullTimeEmployee ? ((FullTimeEmployee) employee).getSalary() : 0.0);
            stmt.setString(5, employee.getSsnLast4());
            stmt.setString(6, employee.getSsnHash());
            stmt.setBytes(7, employee.getSsnEnc());
            stmt.setBytes(8, employee.getSsnIv());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    employee.setEmpId(keys.getInt(1));
                }
            }
            return employee;
        } catch (SQLException e) {
            logError(e);
            return null;
        }
    }

    @Override
    public BaseEmployee updateEmployee(BaseEmployee employee) {
        String sql = "UPDATE employees SET fname = ?, lname = ?, employment_type_id = ?, salary = ?, updated_at = NOW() WHERE empid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setInt(3, employee.getEmploymentStatusId());
            stmt.setDouble(4, employee instanceof FullTimeEmployee ? ((FullTimeEmployee) employee).getSalary() : 0.0);
            stmt.setInt(5, employee.getEmpId());
            stmt.executeUpdate();
            return employee;
        } catch (SQLException e) {
            logError(e);
            return null;
        }
    }

    @Override
    public boolean deleteEmployee(int empId) {
        String sql = "DELETE FROM employees WHERE empid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, empId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logError(e);
            return false;
        }
    }

    /**
     * Maps a ResultSet row into a lean BaseEmployee.
     * Also loads contacts from employee_contact table.
     */
    private BaseEmployee mapEmployee(ResultSet rs, Connection conn) throws SQLException {
        FullTimeEmployee emp = new FullTimeEmployee();
        emp.setEmpId(rs.getInt("empid"));
        emp.setFirstName(rs.getString("fname"));
        emp.setLastName(rs.getString("lname"));
        emp.setSalary(rs.getDouble("salary"));
        emp.setEmploymentStatusId(rs.getInt("status_id"));
        Date dob = rs.getDate("dob");
        if (dob != null) emp.setDob(dob.toLocalDate());
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) emp.setHireDate(hireDate.toLocalDate());

        // Load contacts
        List<Contact> contacts = new ArrayList<>();
        String contactSql = "SELECT contact_id, contact_type_id, contact_value, is_primary FROM employee_contact WHERE empid = ?";
        try (PreparedStatement cstmt = conn.prepareStatement(contactSql)) {
            cstmt.setInt(1, emp.getEmpId());
            try (ResultSet crs = cstmt.executeQuery()) {
                while (crs.next()) {
                    Contact c = new Contact();
                    c.setContactId(crs.getInt("contact_id"));
                    c.setContactType(String.valueOf(crs.getInt("contact_type_id"))); // map to type_name via ContactTypeDao if needed
                    c.setContactValue(crs.getString("contact_value"));
                    c.setIsPrimary(crs.getBoolean("is_primary"));
                    contacts.add(c);
                }
            }
        }
        emp.setContacts(contacts);

        return emp;
    }

    private void logError(SQLException e) {
        System.err.println("DAO error: " + e.getMessage());
    }
}
