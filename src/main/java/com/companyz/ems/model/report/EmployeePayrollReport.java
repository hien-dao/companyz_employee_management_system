package com.companyz.ems.model.report;

import java.util.List;

import com.companyz.ems.model.Payroll;

/**
 * Report: full-time employee pay statement history.
 * Audience: General employee.
 */
public class EmployeePayrollReport {
    private final int empId;
    private final String firstName;
    private final String lastName;
    private final String divisionName;
    private final String jobTitleName;
    private final List<Payroll> payrolls; // reuse your Payroll model

    public EmployeePayrollReport(int empId,
                                 String firstName,
                                 String lastName,
                                 String divisionName,
                                 String jobTitleName,
                                 List<Payroll> payrolls) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.divisionName = divisionName;
        this.jobTitleName = jobTitleName;
        this.payrolls = payrolls;
    }

    public int getEmpId() { return empId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDivisionName() { return divisionName; }
    public String getJobTitleName() { return jobTitleName; }
    public List<Payroll> getPayrolls() { return payrolls; }
}
