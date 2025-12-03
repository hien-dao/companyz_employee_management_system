package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Payroll;
import com.companyz.ems.model.report.DivisionMonthlyPayReport;
import com.companyz.ems.model.report.EmployeePayrollReport;
import com.companyz.ems.model.report.JobTitleMonthlyPayReport;

public interface PayrollDao {
    // --- Standard CRUD ---
    Optional<Payroll> findById(int payrollId);
    List<Payroll> findAll();
    Payroll createPayroll(Payroll payroll);
    Payroll updatePayroll(Payroll payroll);
    boolean deletePayroll(int payrollId);

    // --- Reporting methods ---
    EmployeePayrollReport getPayrollsByEmployee(int empId);
    DivisionMonthlyPayReport getTotalPayByDivision(int divisionId, int month, int year);
    JobTitleMonthlyPayReport getTotalPayByJobTitle(int jobTitleId, int month, int year);
}
