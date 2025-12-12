package com.companyz.ems.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Division;
import com.companyz.ems.model.Payroll;
import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.model.employee.EmploymentStatus;
import com.companyz.ems.model.employee.JobTitle;
import com.companyz.ems.security.SessionContext;

public interface EmployeeService {

    // --- Employee CRUD ---
    List<Employee> getAllEmployees(SessionContext ctx);

    boolean createEmployee(SessionContext ctx,
                           String firstName,
                           String lastName,
                           String gender,
                           String race,
                           LocalDate dob,
                           String ssn, // full SSN that will turn into ssnLast4, ssnHash, ssnEnc, ssnIv
                           String primaryEmail,
                           String primaryPhoneNumber,
                           String addressLine1,
                           String addressLine2,
                           String city,
                           String state,
                           String country,
                           String zipCode,
                           String employmentType,
                           String employmentStatus,
                           String jobTitle,
                           String division,
                           double salary,
                           LocalDate hireDate);

    boolean updateEmployee(SessionContext ctx,
                           int empId,
                           String firstName,
                           String lastName,
                           String gender,
                           String race,
                           LocalDate dob,
                           String ssn, // full SSN that will turn into ssnLast4, ssnHash, ssnEnc, ssnIv
                           String primaryEmail,
                           String primaryPhoneNumber,
                           String addressLine1,
                           String addressLine2,
                           String city,
                           String state,
                           String country,
                           String zipCode,
                           String employmentType,
                           String employmentStatus,
                           String jobTitle,
                           String division,
                           double salary,
                           LocalDate hireDate);

    boolean deleteEmployee(SessionContext ctx, int empId);

    // --- Employee Self Access ---
    Optional<Employee> getSelfEmployeeInfo(SessionContext ctx);

    // --- Employee Search (Admin only) ---
    Optional<Employee> searchByName(SessionContext ctx, String firstName, String lastName);
    Optional<Employee> searchBySsn(SessionContext ctx, String ssn); // full SSN will turn to hash to search
    List<Employee> searchByDob(SessionContext ctx, LocalDate dob);
    Optional<Employee> searchByEmpId(SessionContext ctx, int empId);

    // --- Division CRUD (get by name only) ---
    Optional<Division> getDivisionByName(SessionContext ctx, String name);
    List<Division> getAllDivisions(SessionContext ctx);
    boolean createDivision(SessionContext ctx, String name, String description,
                           String addressLine1, String addressLine2,
                           String city, String state, String country, String postalCode,
                           boolean isActive);
    boolean updateDivision(SessionContext ctx, int divisionId, String name, String description,
                           String addressLine1, String addressLine2,
                           String city, String state, String country, String postalCode,
                           boolean isActive);
    boolean deleteDivision(SessionContext ctx, int divisionId);

    // --- Job Title CRUD (get by name only) ---
    Optional<JobTitle> getJobTitleByName(SessionContext ctx, String name);
    List<JobTitle> getAllJobTitles(SessionContext ctx);
    boolean createJobTitle(SessionContext ctx, String name, String description);
    boolean updateJobTitle(SessionContext ctx, int jobTitleId, String name, String description);
    boolean deleteJobTitle(SessionContext ctx, int jobTitleId);

    // --- Employment Status CRUD (get by name only) ---
    Optional<EmploymentStatus> getEmploymentStatusByName(SessionContext ctx, String name);
    List<EmploymentStatus> getAllEmploymentStatuses(SessionContext ctx);
    boolean createEmploymentStatus(SessionContext ctx, String name, String description,
                                   LocalDate effectiveStart, LocalDate effectiveEnd);
    boolean updateEmploymentStatus(SessionContext ctx, int statusId, String name, String description,
                                   LocalDate effectiveStart, LocalDate effectiveEnd);
    boolean deleteEmploymentStatus(SessionContext ctx, int statusId);

    // --- Payroll CRUD (get by employee only) ---
    List<Payroll> getAllPayrolls(SessionContext ctx);
    boolean createPayroll(SessionContext ctx, int empId, LocalDate payDate,
                          double earnings, double fedTax, double fedMed, double fedSs,
                          double stateTax, double retire401k, double healthCare, double netPay);
    boolean updatePayroll(SessionContext ctx, int payrollId, LocalDate payDate,
                          double earnings, double fedTax, double fedMed, double fedSs,
                          double stateTax, double retire401k, double healthCare, double netPay);
    boolean deletePayroll(SessionContext ctx, int payrollId);

    //Increase salary by range
    int increaseSalaryByRange(SessionContext ctx, double percent, double minSalary, double maxSalary,
                             String reason);
}

