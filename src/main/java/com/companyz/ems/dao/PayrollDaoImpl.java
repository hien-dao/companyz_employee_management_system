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

import com.companyz.ems.model.Payroll;
import com.companyz.ems.model.report.DivisionMonthlyPayReport;
import com.companyz.ems.model.report.EmployeePayrollReport;
import com.companyz.ems.model.report.JobTitleMonthlyPayReport;

public class PayrollDaoImpl extends AbstractDao implements PayrollDao {

    // --- CRUD ---

    @Override
    public Optional<Payroll> findById(int payrollId) {
        String sql = "SELECT * FROM payroll WHERE payid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, payrollId);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapPayroll(rs));
            }
        } catch (SQLException e) {
            logError(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Payroll> findAll() {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = "SELECT * FROM payroll";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                payrolls.add(mapPayroll(rs));
            }
        } catch (SQLException e) {
            logError(e);
        }
        return payrolls;
    }

    @Override
    public Payroll createPayroll(Payroll payroll) {
        String sql = "INSERT INTO payroll (payroll_run_id, empid, pay_date, earnings, " +
                     "fed_tax, fed_med, fed_ss, state_tax, retire_401k, health_care, net_pay, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payroll.getPayrollId()); // adjust if payroll_run_id is separate
            stmt.setInt(2, payroll.getEmpId());
            stmt.setDate(3, Date.valueOf(payroll.getPayDate()));
            stmt.setDouble(4, payroll.getEarnings());
            stmt.setDouble(5, payroll.getFedTax());
            stmt.setDouble(6, payroll.getFedMed());
            stmt.setDouble(7, payroll.getFedSs());
            stmt.setDouble(8, payroll.getStateTax());
            stmt.setDouble(9, payroll.getRetire401k());
            stmt.setDouble(10, payroll.getHealthCare());
            stmt.setDouble(11, payroll.getNetPay());

            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    payroll.setPayrollId(keys.getInt(1));
                }
            }
            return payroll;
        } catch (SQLException e) {
            logError(e);
            return null;
        }
    }

    @Override
    public Payroll updatePayroll(Payroll payroll) {
        String sql = "UPDATE payroll SET empid = ?, pay_date = ?, earnings = ?, fed_tax = ?, " +
                     "fed_med = ?, fed_ss = ?, state_tax = ?, retire_401k = ?, health_care = ?, net_pay = ?, " +
                     "updated_at = NOW() WHERE payid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payroll.getEmpId());
            stmt.setDate(2, Date.valueOf(payroll.getPayDate()));
            stmt.setDouble(3, payroll.getEarnings());
            stmt.setDouble(4, payroll.getFedTax());
            stmt.setDouble(5, payroll.getFedMed());
            stmt.setDouble(6, payroll.getFedSs());
            stmt.setDouble(7, payroll.getStateTax());
            stmt.setDouble(8, payroll.getRetire401k());
            stmt.setDouble(9, payroll.getHealthCare());
            stmt.setDouble(10, payroll.getNetPay());
            stmt.setInt(11, payroll.getPayrollId());

            stmt.executeUpdate();
            return payroll;
        } catch (SQLException e) {
            logError(e);
            return null;
        }
    }

    @Override
    public boolean deletePayroll(int payrollId) {
        String sql = "DELETE FROM payroll WHERE payid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, payrollId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logError(e);
            return false;
        }
    }

    // --- Reporting methods ---
    @Override
    public EmployeePayrollReport getPayrollsByEmployee(int empId) {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = "SELECT e.fname, e.lname, d.name AS division_name, j.job_title AS job_title_name, " +
                    "p.payid, p.empid, p.pay_date, p.earnings, p.fed_tax, p.fed_med, p.fed_ss, " +
                    "p.state_tax, p.retire_401k, p.health_care, p.net_pay " +
                    "FROM payroll p " +
                    "JOIN employees e ON p.empid = e.empid " +
                    "JOIN employee_division ed ON e.empid = ed.empid " +
                    "JOIN divisions d ON ed.divid = d.divid " +
                    "JOIN employee_job_title ej ON e.empid = ej.empid " +
                    "JOIN job_titles j ON ej.job_title_id = j.job_title_id " +
                    "WHERE p.empid = ? ORDER BY p.pay_date DESC";

        String firstName = null, lastName = null, divisionName = null, jobTitleName = null;

        try (Connection conn = getConnection();
            PreparedStatement stmt = prepareStatement(conn, sql, empId);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                if (firstName == null) { // set once
                    firstName = rs.getString("fname");
                    lastName = rs.getString("lname");
                    divisionName = rs.getString("division_name");
                    jobTitleName = rs.getString("job_title_name");
                }
                payrolls.add(mapPayroll(rs));
            }
        } catch (SQLException e) {
            logError(e);
        }
        return new EmployeePayrollReport(empId, firstName, lastName, divisionName, jobTitleName, payrolls);
    }


    @Override
    public DivisionMonthlyPayReport getTotalPayByDivision(int divisionId, int month, int year) {
        String sql = "SELECT d.name AS division_name, SUM(p.net_pay) AS total " +
                    "FROM payroll p " +
                    "JOIN employee_division ed ON p.empid = ed.empid " +
                    "JOIN divisions d ON ed.divid = d.divid " +
                    "WHERE d.divid = ? AND MONTH(p.pay_date) = ? AND YEAR(p.pay_date) = ? " +
                    "GROUP BY d.name";
        double total = 0.0;
        String divisionName = null;
        try (Connection conn = getConnection();
            PreparedStatement stmt = prepareStatement(conn, sql, divisionId, month, year);
            ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble("total");
                divisionName = rs.getString("division_name");
            }
        } catch (SQLException e) {
            logError(e);
        }
        return new DivisionMonthlyPayReport(divisionId, divisionName, month, year, total);
    }


    @Override
    public JobTitleMonthlyPayReport getTotalPayByJobTitle(int jobTitleId, int month, int year) {
        String sql = "SELECT j.job_title AS job_title_name, SUM(p.net_pay) AS total " +
                    "FROM payroll p " +
                    "JOIN employee_job_title ej ON p.empid = ej.empid " +
                    "JOIN job_titles j ON ej.job_title_id = j.job_title_id " +
                    "WHERE j.job_title_id = ? AND MONTH(p.pay_date) = ? AND YEAR(p.pay_date) = ? " +
                    "GROUP BY j.job_title";
        double total = 0.0;
        String jobTitleName = null;
        try (Connection conn = getConnection();
            PreparedStatement stmt = prepareStatement(conn, sql, jobTitleId, month, year);
            ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble("total");
                jobTitleName = rs.getString("job_title_name");
            }
        } catch (SQLException e) {
            logError(e);
        }
        return new JobTitleMonthlyPayReport(jobTitleId, jobTitleName, month, year, total);
    }


    // --- Helper mapping ---
    private Payroll mapPayroll(ResultSet rs) throws SQLException {
        Payroll p = new Payroll();
        p.setPayrollId(rs.getInt("payid"));
        p.setEmpId(rs.getInt("empid"));
        p.setPayDate(rs.getDate("pay_date").toLocalDate());
        p.setEarnings(rs.getDouble("earnings"));
        p.setFedTax(rs.getDouble("fed_tax"));
        p.setFedMed(rs.getDouble("fed_med"));
        p.setFedSs(rs.getDouble("fed_ss"));
        p.setStateTax(rs.getDouble("state_tax"));
        p.setRetire401k(rs.getDouble("retire_401k"));
        p.setHealthCare(rs.getDouble("health_care"));
        p.setNetPay(rs.getDouble("net_pay"));
        return p;
    }

    private void logError(SQLException e) {
        System.err.println("PayrollDao error: " + e.getMessage());
    }
}
