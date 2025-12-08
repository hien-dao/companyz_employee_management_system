package com.companyz.ems.services;

import com.companyz.ems.model.report.DivisionMonthlyPayReport;
import com.companyz.ems.model.report.EmployeeHireReport;
import com.companyz.ems.model.report.EmployeePayrollReport;
import com.companyz.ems.model.report.JobTitleMonthlyPayReport;
import com.companyz.ems.security.SessionContext;

/**
 * Service interface for generating employee and payroll-related reports.
 * Methods enforce role-based access using the provided SessionContext.
 */
public interface ReportService {

    /**
     * Retrieves the payroll statement history for the currently logged-in employee.
     * <p>
     * This report is intended for general employees (full-time only).
     * The payrolls are sorted by most recent pay date.
     *
     * @param ctx the current user session context
     * @return an EmployeePayrollReport containing payroll history for the employee
     */
    EmployeePayrollReport getEmployeePayrollHistory(SessionContext ctx);

    /**
     * Retrieves total pay for a specific job title in a given month and year.
     * <p>
     * This report is intended for HR Admins only.
     *
     * @param ctx          the current user session context
     * @param jobTitleName the name of the job title
     * @param year         the year of the report
     * @param month        the month of the report
     * @return a JobTitleMonthlyPayReport for the specified job title
     */
    JobTitleMonthlyPayReport getMonthlyPayByJobTitle(SessionContext ctx,
                                                     String jobTitleName,
                                                     int year,
                                                     int month);

    /**
     * Retrieves total pay for a specific division in a given month and year.
     * <p>
     * This report is intended for HR Admins only.
     *
     * @param ctx          the current user session context
     * @param divisionName the name of the division
     * @param year         the year of the report
     * @param month        the month of the report
     * @return a DivisionMonthlyPayReport for the specified division
     */
    DivisionMonthlyPayReport getMonthlyPayByDivision(SessionContext ctx,
                                                     String divisionName,
                                                     int year,
                                                     int month);

    /**
     * Retrieves a report of employees hired within a given date range.
     * <p>
     * This report is intended for HR Admins only.
     *
     * @param ctx       the current user session context
     * @param startDay  the starting day of the range
     * @param startMonth the starting month of the range
     * @param startYear the starting year of the range
     * @param endDay    the ending day of the range
     * @param endMonth  the ending month of the range
     * @param endYear   the ending year of the range
     * @return an EmployeeHireReport containing employees hired in the date range
     */
    EmployeeHireReport getEmployeesHiredWithinDateRange(SessionContext ctx,
                                                        int startDay, int startMonth, int startYear,
                                                        int endDay, int endMonth, int endYear);
}
