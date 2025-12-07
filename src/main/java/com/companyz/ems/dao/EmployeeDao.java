package com.companyz.ems.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.employee.BaseEmployee;
import com.companyz.ems.model.report.EmployeeHireReport;

/**
 * DAO contract for lean employee persistence.
 * Works only with BaseEmployee (IDs + core fields).
 */
public interface EmployeeDao {
    Optional<BaseEmployee> findById(int empId);
    Optional<BaseEmployee> findByName(String firstName, String lastName);
    Optional<BaseEmployee> findBySsn(String ssnHash);

    List<BaseEmployee> findByDob(LocalDate dob);
    List<BaseEmployee> findAll();

    BaseEmployee createEmployee(BaseEmployee employee);   // return created entity with generated ID
    BaseEmployee updateEmployee(BaseEmployee employee);   // return updated entity
    boolean deleteEmployee(int empId);

    // --- Reporting ---
    /**
     * Generates a report of employees hired within the specified date range.
     * Returns an EmployeeHireReport containing the relevant data.
     * @param startDate
     * @param endDate
     * @return
     */
    EmployeeHireReport getEmployeeHireByDateRange(LocalDate startDate, LocalDate endDate);

    // --- Increase salary ---
    /**
     * Increases salary by percent for employees within the specified salary range.
     * Records the reason and the user who made the change.
     * Returns the number of employees whose salaries were increased.
     * @param percent
     * @param minSalary
     * @param maxSalary
     * @param reason
     * @param changedByUserId
     * @return
     */
    int increaseSalaryByRange(double percent, double minSalary, double maxSalary, 
        String reason, int changedByUserId);
}
