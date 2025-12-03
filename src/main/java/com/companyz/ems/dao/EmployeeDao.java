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
    List<BaseEmployee> findAll();
    List<BaseEmployee> findByDivision(int divisionId);
    List<BaseEmployee> findByJobTitle(int jobTitleId);

    BaseEmployee createEmployee(BaseEmployee employee);   // return created entity with generated ID
    BaseEmployee updateEmployee(BaseEmployee employee);   // return updated entity
    boolean deleteEmployee(int empId);

    // --- Reporting ---
    EmployeeHireReport getEmployeeHireByDateRange(LocalDate startDate, LocalDate endDate);
}
