package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Address;
import com.companyz.ems.model.Contact;
import com.companyz.ems.model.employee.BaseEmployee;
import com.companyz.ems.model.employee.FullTimeEmployee;
import com.companyz.ems.model.report.EmployeeHireReport;

/**
 * JDBC implementation of EmployeeDao.
 * Uses normalized schema: employees + related tables.
 */
public class EmployeeDaoImpl extends AbstractDao implements EmployeeDao {
    private static final String BASE_EMPLOYEE_SELECT =
        "SELECT e.empid, e.fname, e.lname, e.salary, " +
        "e.employment_type_id, et.employment_type_name, " +
        "e.ssn_last4, e.ssn_hash, e.ssn_enc, e.ssn_iv, " +
        "e.created_at, e.updated_at, " +
        "d.gender, d.race, d.dob, " +
        "s.status_id, s.status, s.hire_date, s.termination_date, " +
        "div.divid, div.name AS division_name, div.description AS division_desc, " +
        "div.address_line1, div.address_line2, div.zip_code, " +
        "c.city_name, st.state_name, co.country_name, " +
        "jt.job_title_id, jt.job_title, jt.description AS job_desc " +
        "FROM employees e " +
        "LEFT JOIN employment_types et ON e.employment_type_id = et.employment_type_id " +
        "LEFT JOIN employee_demographic d ON e.empid = d.empid " +
        "LEFT JOIN employee_status s ON e.empid = s.empid " +
        "LEFT JOIN employee_division ed ON e.empid = ed.empid " +
        "LEFT JOIN divisions div ON ed.divid = div.divid " +
        "LEFT JOIN cities c ON div.cityid = c.cityid " +
        "LEFT JOIN states st ON div.stateid = st.stateid " +
        "LEFT JOIN countries co ON div.countryid = co.countryid " +
        "LEFT JOIN employee_job_title ej ON e.empid = ej.empid " +
        "LEFT JOIN job_titles jt ON ej.job_title_id = jt.job_title_id ";


    @Override
    public Optional<BaseEmployee> findById(int empId) {
        String sql = BASE_EMPLOYEE_SELECT + "WHERE e.empid = ?";
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
        String sql = BASE_EMPLOYEE_SELECT + "WHERE e.fname = ? AND e.lname = ?";
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
        String sql = BASE_EMPLOYEE_SELECT + "WHERE e.ssn_hash = ?";
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
        String sql = BASE_EMPLOYEE_SELECT + "WHERE d.dob = ?";
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
        String sql = BASE_EMPLOYEE_SELECT; // no WHERE clause, fetch all employees
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
    public List<BaseEmployee> finByEmploymentStatus(int employmentStatusId) {
        List<BaseEmployee> employees = new ArrayList<>();
        String sql = "SELECT e.empid, e.fname, e.lname, e.salary, e.employment_type_id, " +
                    "d.gender, d.race, d.dob, s.status_id, s.hire_date " +
                    "FROM employees e " +
                    "LEFT JOIN employee_demographic d ON e.empid = d.empid " +
                    "LEFT JOIN employee_status s ON e.empid = s.empid " +
                    "WHERE s.status_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = prepareStatement(conn, sql, employmentStatusId);
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

    @Override
    public EmployeeHireReport getEmployeeHireByDateRange(LocalDate startDate, LocalDate endDate) {
        List<EmployeeHireReport.HireEntry> hires = new ArrayList<>();
        String sql = "SELECT e.empid, e.fname, e.lname, d.name AS division_name, j.job_title AS job_title_name, s.hire_date " +
                     "FROM employees e " +
                     "JOIN employee_status s ON e.empid = s.empid " +
                     "JOIN employee_division ed ON e.empid = ed.empid " +
                     "JOIN divisions d ON ed.divid = d.divid " +
                     "JOIN employee_job_title ej ON e.empid = ej.empid " +
                     "JOIN job_titles j ON ej.job_title_id = j.job_title_id " +
                     "WHERE s.hire_date BETWEEN ? AND ? " +
                     "ORDER BY s.hire_date ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
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
        return new EmployeeHireReport(startDate, endDate, hires);
    }

    /**
     * Maps a ResultSet row into a lean BaseEmployee.
     * Also loads contacts from employee_contact table.
     */
    private BaseEmployee mapEmployee(ResultSet rs, Connection conn) throws SQLException {
        FullTimeEmployee emp = new FullTimeEmployee();

        // Core identifiers
        emp.setEmpId(rs.getInt("empid"));
        emp.setFirstName(rs.getString("fname"));
        emp.setLastName(rs.getString("lname"));

        // Salary
        emp.setSalary(rs.getDouble("salary"));

        // Employment type
        emp.setEmploymentStatusId(rs.getInt("status_id"));
        emp.setEmploymentTypeString(rs.getString("employment_type_name"));

        // Status
        emp.setEmploymentStatusString(rs.getString("status"));
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) emp.setHireDate(hireDate.toLocalDate());

        // Demographics
        emp.setGender(rs.getString("gender"));
        emp.setRace(rs.getString("race"));
        Date dob = rs.getDate("dob");
        if (dob != null) emp.setDob(dob.toLocalDate());

        // Division
        emp.setDivisionId(rs.getInt("divid"));
        emp.setDivisionString(rs.getString("division_name"));

        // Address (from division join)
        Address addr = new Address();
        addr.setAddressLine1(rs.getString("address_line1"));
        addr.setAddressLine2(rs.getString("address_line2"));
        addr.setCity(rs.getString("city_name"));
        addr.setState(rs.getString("state_name"));
        addr.setCountry(rs.getString("country_name"));
        addr.setPostalCode(rs.getString("zip_code"));
        emp.setAddress(addr);

        // Job title
        emp.setJobTitleId(rs.getInt("job_title_id"));
        emp.setJobTitleString(rs.getString("job_title"));

        // Audit timestamps
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) emp.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) emp.setUpdatedAt(updated.toLocalDateTime());

        // SSN fields
        emp.setSsnLast4(rs.getString("ssn_last4"));
        emp.setSsnHash(rs.getString("ssn_hash"));
        emp.setSsnEnc(rs.getBytes("ssn_enc"));
        emp.setSsnIv(rs.getBytes("ssn_iv"));

        // Contacts (one-to-many, separate query)
        List<Contact> contacts = new ArrayList<>();
        String contactSql = "SELECT c.contact_id, ct.type_name, c.contact_value, c.is_primary " +
                            "FROM employee_contact c " +
                            "JOIN contact_types ct ON c.contact_type_id = ct.contact_type_id " +
                            "WHERE c.empid = ?";
        try (PreparedStatement cstmt = conn.prepareStatement(contactSql)) {
            cstmt.setInt(1, emp.getEmpId());
            try (ResultSet crs = cstmt.executeQuery()) {
                while (crs.next()) {
                    Contact contact = new Contact();
                    contact.setContactId(crs.getInt("contact_id"));
                    contact.setContactType(crs.getString("type_name"));
                    contact.setContactValue(crs.getString("contact_value"));
                    contact.setIsPrimary(crs.getBoolean("is_primary"));
                    contacts.add(contact);
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
