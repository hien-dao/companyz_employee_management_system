package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.dao.helper.EmployeePersistenceHelper;
import com.companyz.ems.model.employee.BaseEmployee;
import com.companyz.ems.model.employee.FullTimeEmployee;
import com.companyz.ems.model.report.EmployeeHireReport;

/**
 * JDBC implementation of EmployeeDao.
 * Uses normalized schema: employees + related tables.
 */
public class EmployeeDaoImpl extends AbstractDao implements EmployeeDao {
    // --- Core Queries ---
    private static final String SELECT_EMPLOYEE =
        "SELECT empid, fname, lname, salary, employment_type_id, ssn_last4, ssn_hash, ssn_enc, ssn_iv, created_at, updated_at " +
        "FROM employees ";

    private static final String INSERT_EMPLOYEE =
        "INSERT INTO employees (fname, lname, employment_type_id, salary, ssn_last4, ssn_hash, ssn_enc, ssn_iv, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

    private static final String UPDATE_EMPLOYEE =
        "UPDATE employees SET fname=?, lname=?, employment_type_id=?, salary=?, updated_at=NOW() WHERE empid=?";

    private static final String DELETE_EMPLOYEE =
        "DELETE FROM employees WHERE empid=?";


    @Override
    public Optional<BaseEmployee> findById(int empId) {
        String sql = SELECT_EMPLOYEE + "WHERE empid=?";
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
    public Optional<BaseEmployee> findByName(String firstName, String lastName) {
        String sql = SELECT_EMPLOYEE + "WHERE fname = ? AND lname = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs, conn));
                }
            }
        } catch (SQLException e) {
            logError(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<BaseEmployee> findBySsn(String ssnHash) {
        String sql = SELECT_EMPLOYEE + "WHERE ssn_hash = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ssnHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs, conn));
                }
            }
        } catch (SQLException e) {
            logError(e);
        }
        return Optional.empty();
    }


    @Override
    public List<BaseEmployee> findByDob(LocalDate dob) {
        List<BaseEmployee> employees = new ArrayList<>();
        String sql =
            SELECT_EMPLOYEE +
            "WHERE empid IN (" +
            "  SELECT ed.empid FROM employee_demographic ed WHERE ed.dob = ?" +
            ")";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dob));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapEmployee(rs, conn));
                }
            }
        } catch (SQLException e) {
            logError(e);
        }
        return employees;
    }


    @Override
    public List<BaseEmployee> findAll() {
        List<BaseEmployee> employees = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_EMPLOYEE);
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
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setInt(3, employee.getEmploymentStatusId()); // maps to employment_type_id
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

            // Delegate to helper for related tables
            EmployeePersistenceHelper.saveContacts(conn, employee);
            EmployeePersistenceHelper.saveDemographics(conn, employee);
            EmployeePersistenceHelper.saveDivision(conn, employee);
            EmployeePersistenceHelper.saveJobTitle(conn, employee);
            EmployeePersistenceHelper.saveStatus(conn, employee);

            return employee;
        } catch (SQLException e) {
            logError(e);
            return null;
        }
    }

    @Override
    public BaseEmployee updateEmployee(BaseEmployee employee) {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_EMPLOYEE)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setInt(3, employee.getEmploymentStatusId());
            stmt.setDouble(4, employee instanceof FullTimeEmployee ? ((FullTimeEmployee) employee).getSalary() : 0.0);
            stmt.setInt(5, employee.getEmpId());
            stmt.executeUpdate();

            // Delegate updates to helper
            EmployeePersistenceHelper.updateContacts(conn, employee);
            EmployeePersistenceHelper.updateDemographics(conn, employee);
            EmployeePersistenceHelper.updateDivision(conn, employee);
            EmployeePersistenceHelper.updateJobTitle(conn, employee);
            EmployeePersistenceHelper.updateStatus(conn, employee);

            return employee;
        } catch (SQLException e) {
            logError(e);
            return null;
        }
    }

    @Override
    public boolean deleteEmployee(int empId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, DELETE_EMPLOYEE, empId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logError(e);
            return false;
        }
    }

    @Override
    public EmployeeHireReport getEmployeeHireByDateRange(LocalDate start, LocalDate end) {
        List<EmployeeHireReport.HireEntry> hires = new ArrayList<>();
        String sql = "SELECT e.empid, e.fname, e.lname, d.name AS division_name, j.job_title AS job_title_name, s.hire_date " +
                     "FROM employees e " +
                     "JOIN employee_status s ON e.empid = s.empid " +
                     "JOIN employee_division ed ON e.empid = ed.empid " +
                     "JOIN divisions d ON ed.divid = d.divid " +
                     "JOIN employee_job_title ej ON e.empid = ej.empid " +
                     "JOIN job_titles j ON ej.job_title_id = j.job_title_id " +
                     "WHERE s.hire_date BETWEEN ? AND ? ORDER BY s.hire_date ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    hires.add(new EmployeeHireReport.HireEntry(
                        rs.getInt("empid"),
                        rs.getString("fname"),
                        rs.getString("lname"),
                        rs.getString("division_name"),
                        rs.getString("job_title_name"),
                        rs.getDate("hire_date").toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            logError(e);
        }
        return new EmployeeHireReport(start, end, hires);
    }

    // --- Mapping Helpers ---
    private BaseEmployee mapEmployee(ResultSet rs, Connection conn) throws SQLException {
        FullTimeEmployee emp = new FullTimeEmployee();
        emp.setEmpId(rs.getInt("empid"));
        emp.setFirstName(rs.getString("fname"));
        emp.setLastName(rs.getString("lname"));
        emp.setSalary(rs.getDouble("salary"));
        emp.setSsnLast4(rs.getString("ssn_last4"));
        emp.setSsnHash(rs.getString("ssn_hash"));
        emp.setSsnEnc(rs.getBytes("ssn_enc"));
        emp.setSsnIv(rs.getBytes("ssn_iv"));

        // Load related info via helper
        emp.setContacts(EmployeePersistenceHelper.loadContacts(conn, emp.getEmpId()));
        emp.setDivisionString(EmployeePersistenceHelper.loadDivision(conn, emp.getEmpId()));
        emp.setJobTitleString(EmployeePersistenceHelper.loadJobTitle(conn, emp.getEmpId()));
        emp.setEmploymentStatusString(EmployeePersistenceHelper.loadStatus(conn, emp.getEmpId()));

        return emp;
    }

    private void logError(SQLException e) {
        System.err.println("DAO error: " + e.getMessage());
    }
}
