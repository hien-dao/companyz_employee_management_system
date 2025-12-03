package com.companyz.ems.model.report;

import java.time.LocalDate;
import java.util.List;

/**
 * Report: employees hired within a given date range.
 * Audience: HR Admin.
 */
public class EmployeeHireReport {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<HireEntry> hires;

    public EmployeeHireReport(LocalDate startDate, LocalDate endDate, List<HireEntry> hires) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.hires = hires;
    }

    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public List<HireEntry> getHires() { return hires; }

    /**
     * Nested DTO representing one employee hired in the range.
     */
    public static class HireEntry {
        private final int empId;
        private final String firstName;
        private final String lastName;
        private final String divisionName;
        private final String jobTitleName;
        private final LocalDate hireDate;

        public HireEntry(int empId,
                         String firstName,
                         String lastName,
                         String divisionName,
                         String jobTitleName,
                         LocalDate hireDate) {
            this.empId = empId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.divisionName = divisionName;
            this.jobTitleName = jobTitleName;
            this.hireDate = hireDate;
        }

        public int getEmpId() { return empId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getDivisionName() { return divisionName; }
        public String getJobTitleName() { return jobTitleName; }
        public LocalDate getHireDate() { return hireDate; }
    }
}
