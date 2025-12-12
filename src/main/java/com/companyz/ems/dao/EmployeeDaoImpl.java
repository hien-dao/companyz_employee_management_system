package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.dao.helper.EmployeePersistenceHelper;
import com.companyz.ems.model.employee.BaseEmployee;
import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.model.employee.FullTimeEmployee;
import com.companyz.ems.model.report.EmployeeHireReport;

/**
 * JDBC implementation of EmployeeDao.
 * Uses normalized schema: employees + related tables.
 */
public class EmployeeDaoImpl extends AbstractDao implements EmployeeDao {
    // --- Core Queries ---
    private static final String SELECT_EMPLOYEE =
        "SELECT empid, fname, lname, salary, ssn_last4, ssn_hash, ssn_enc, ssn_iv, created_at, updated_at " +
        "FROM employees ";

    private static final String INSERT_EMPLOYEE =
        "INSERT INTO employees (fname, lname, salary, ssn_last4, ssn_hash, ssn_enc, ssn_iv, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

    private static final String UPDATE_EMPLOYEE =
        "UPDATE employees SET fname=?, lname=?, salary=?, updated_at=NOW() WHERE empid=?";

    private static final String DELETE_EMPLOYEE =
        "DELETE FROM employees WHERE empid=?";


    @Override
    public Optional<Employee> findById(int empId) {
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
    public Optional<Employee> findByName(String firstName, String lastName) {
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
    public Optional<Employee> findBySsn(String ssnHash) {
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
    public List<Employee> findByDob(LocalDate dob) {
        List<Employee> employees = new ArrayList<>();
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
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
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
    public Employee createEmployee(BaseEmployee employee) {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS)) {

            // Bind core fields
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setDouble(3, employee instanceof FullTimeEmployee ? ((FullTimeEmployee) employee).getSalary() : 0.0);
            stmt.setString(4, employee.getSsnLast4());
            stmt.setBytes(5, Base64.getDecoder().decode(employee.getSsnHash())); // decode string back to bytes
            stmt.setBytes(6, employee.getSsnEnc());
            stmt.setBytes(7, employee.getSsnIv());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    employee.setEmpId(keys.getInt(1));
                }
            }

            // Persist normalized data
            EmployeePersistenceHelper.saveEmploymentType(conn, employee);
            EmployeePersistenceHelper.saveStatus(conn, employee);
            EmployeePersistenceHelper.saveContacts(conn, employee);
            EmployeePersistenceHelper.saveDemographics(conn, employee);
            EmployeePersistenceHelper.saveDivision(conn, employee);
            EmployeePersistenceHelper.saveJobTitle(conn, employee);

            return (Employee) employee;
        } catch (Exception e) {
            logError(e);
            return null;
        }
    }


    @Override
    public Employee updateEmployee(BaseEmployee employee) {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_EMPLOYEE)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setDouble(3, employee instanceof FullTimeEmployee ? ((FullTimeEmployee) employee).getSalary() : 0.0);
            stmt.setInt(4, employee.getEmpId());
            stmt.executeUpdate();

            // Update normalized data
            EmployeePersistenceHelper.updateEmploymentType(conn, employee);   // TYPE
            EmployeePersistenceHelper.updateStatus(conn, employee);    // STATUS
            EmployeePersistenceHelper.updateContacts(conn, employee);
            EmployeePersistenceHelper.updateDemographics(conn, employee);
            EmployeePersistenceHelper.updateDivision(conn, employee);
            EmployeePersistenceHelper.updateJobTitle(conn, employee);

            return (Employee) employee;
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

    @Override
    public int increaseSalaryByRange(double percent, double minSalary, double maxSalary,
                                    String reason, int changedByUserId) {
        String selectSql = "SELECT empid, salary FROM employees WHERE salary >= ? AND salary < ?";
        String updateSql = "UPDATE employees SET salary = ?, updated_at = NOW() WHERE empid = ?";
        String insertHistorySql = "INSERT INTO salary_history " +
            "(empid, previous_salary, new_salary, change_reason, changed_by_user_id, changed_at) " +
            "VALUES (?, ?, ?, ?, ?, NOW())";

        int updatedCount = 0;

        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql);
             PreparedStatement historyStmt = conn.prepareStatement(insertHistorySql)) {

            conn.setAutoCommit(false);

            selectStmt.setDouble(1, minSalary);
            selectStmt.setDouble(2, maxSalary);

            try (ResultSet rs = selectStmt.executeQuery()) {
                while (rs.next()) {
                    int empId = rs.getInt("empid");
                    double oldSalary = rs.getDouble("salary");
                    double newSalary = Math.round(oldSalary * (1 + percent / 100.0) * 100.0) / 100.0;

                    // update employees
                    updateStmt.setDouble(1, newSalary);
                    updateStmt.setInt(2, empId);
                    updateStmt.addBatch();

                    // insert salary_history
                    historyStmt.setInt(1, empId);
                    historyStmt.setDouble(2, oldSalary);
                    historyStmt.setDouble(3, newSalary);
                    historyStmt.setString(4, reason);
                    historyStmt.setInt(5, changedByUserId);
                    historyStmt.addBatch();

                    updatedCount++;
                }
            }

            updateStmt.executeBatch();
            historyStmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            logError(e);
            updatedCount = 0;
        }

        return updatedCount;
    }

    // --- Mapping Helpers ---
    private Employee mapEmployee(ResultSet rs, Connection conn) throws SQLException {
        FullTimeEmployee emp = new FullTimeEmployee();
        emp.setEmpId(rs.getInt("empid"));
        emp.setFirstName(rs.getString("fname"));
        emp.setLastName(rs.getString("lname"));
        emp.setSalary(rs.getDouble("salary"));
        emp.setSsnLast4(rs.getString("ssn_last4"));

        // Convert BINARY(32) back to a readable string if your model expects String
        byte[] ssnHashBytes = rs.getBytes("ssn_hash");
        emp.setSsnHash(ssnHashBytes != null ? Base64.getEncoder().encodeToString(ssnHashBytes) : null);

        emp.setSsnEnc(rs.getBytes("ssn_enc"));
        emp.setSsnIv(rs.getBytes("ssn_iv"));

        // ❌ Removed created_at / updated_at mapping for now
        // If you want later: add null-safe conversion to LocalDateTime

        // Load related info via helper
        emp.setContacts(EmployeePersistenceHelper.loadContacts(conn, emp.getEmpId()));
        emp.setDivisionString(EmployeePersistenceHelper.loadDivision(conn, emp.getEmpId()));
        emp.setJobTitleString(EmployeePersistenceHelper.loadJobTitle(conn, emp.getEmpId()));
        emp.setEmploymentStatusString(EmployeePersistenceHelper.loadStatus(conn, emp.getEmpId()));
        emp.setEmploymentTypeString(EmployeePersistenceHelper.loadEmploymentType(conn, emp.getEmpId()));

        // New helpers
        EmployeePersistenceHelper.loadDemographics(conn, emp);
        EmployeePersistenceHelper.loadHireDate(conn, emp);

        return emp;
    }



    private void logError(Exception e) {
        System.err.println("DAO error: " + e.getMessage());
    }
}
