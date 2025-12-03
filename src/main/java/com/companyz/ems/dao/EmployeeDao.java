package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.employee.BaseEmployee;

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
}
