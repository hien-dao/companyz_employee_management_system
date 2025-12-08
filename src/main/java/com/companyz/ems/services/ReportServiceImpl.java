package com.companyz.ems.services;

import java.time.LocalDate;

import com.companyz.ems.dao.DivisionDao;
import com.companyz.ems.dao.DivisionDaoImpl;
import com.companyz.ems.dao.EmployeeDao;
import com.companyz.ems.dao.EmployeeDaoImpl;
import com.companyz.ems.dao.JobTitleDao;
import com.companyz.ems.dao.JobTitleDaoImpl;
import com.companyz.ems.dao.PayrollDao;
import com.companyz.ems.dao.PayrollDaoImpl;
import com.companyz.ems.model.report.DivisionMonthlyPayReport;
import com.companyz.ems.model.report.EmployeeHireReport;
import com.companyz.ems.model.report.EmployeePayrollReport;
import com.companyz.ems.model.report.JobTitleMonthlyPayReport;
import com.companyz.ems.security.AuthorizationService;
import com.companyz.ems.security.SessionContext;

public class ReportServiceImpl implements ReportService {

    private final PayrollDao payrollDao;
    private final EmployeeDao employeeDao;
    private final DivisionDao divisionDao;
    private final JobTitleDao jobTitleDao;
    private final AuthorizationService authzService;

    public ReportServiceImpl(PayrollDao payrollDao,
                             EmployeeDao employeeDao,
                             DivisionDao divisionDao,
                             JobTitleDao jobTitleDao,
                             AuthorizationService authzService) {
        this.payrollDao = payrollDao;
        this.employeeDao = employeeDao;
        this.divisionDao = divisionDao;
        this.jobTitleDao = jobTitleDao;
        this.authzService = authzService;
    }

    public ReportServiceImpl() {
        this.payrollDao = new PayrollDaoImpl();
        this.employeeDao = new EmployeeDaoImpl();
        this.divisionDao = new DivisionDaoImpl();
        this.jobTitleDao = new JobTitleDaoImpl();
        this.authzService = new AuthorizationService();
    }

    @Override
    public EmployeePayrollReport getEmployeePayrollHistory(SessionContext ctx) {
        return payrollDao.getPayrollsByEmployee(ctx.getEmployeeId());
    }

    @Override
    public JobTitleMonthlyPayReport getMonthlyPayByJobTitle(SessionContext ctx,
                                                            String jobTitleName,
                                                            int year,
                                                            int month) {
        authzService.requireAdmin(ctx);
        int jobTitleId = jobTitleDao.findByName(jobTitleName)
                                    .orElseThrow(() -> new IllegalArgumentException("Job title not found"))
                                    .getJobTitleId();
        return payrollDao.getTotalPayByJobTitle(jobTitleId, month, year);
    }

    @Override
    public DivisionMonthlyPayReport getMonthlyPayByDivision(SessionContext ctx,
                                                            String divisionName,
                                                            int year,
                                                            int month) {
        authzService.requireAdmin(ctx);
        int divisionId = divisionDao.findByName(divisionName)
                                    .orElseThrow(() -> new IllegalArgumentException("Division not found"))
                                    .getDivisionId();
        return payrollDao.getTotalPayByDivision(divisionId, month, year);
    }

    @Override
    public EmployeeHireReport getEmployeesHiredWithinDateRange(SessionContext ctx,
                                                               int startDay, int startMonth, int startYear,
                                                               int endDay, int endMonth, int endYear) {
        authzService.requireAdmin(ctx);
        LocalDate start = LocalDate.of(startYear, startMonth, startDay);
        LocalDate end = LocalDate.of(endYear, endMonth, endDay);
        return employeeDao.getEmployeeHireByDateRange(start, end);
    }
}
