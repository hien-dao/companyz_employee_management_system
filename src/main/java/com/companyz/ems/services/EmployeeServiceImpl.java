package com.companyz.ems.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.dao.DivisionDao;
import com.companyz.ems.dao.DivisionDaoImpl;
import com.companyz.ems.dao.EmployeeDao;
import com.companyz.ems.dao.EmployeeDaoImpl;
import com.companyz.ems.dao.EmploymentStatusDao;
import com.companyz.ems.dao.EmploymentStatusDaoImpl;
import com.companyz.ems.dao.JobTitleDao;
import com.companyz.ems.dao.JobTitleDaoImpl;
import com.companyz.ems.dao.PayrollDao;
import com.companyz.ems.dao.PayrollDaoImpl;
import com.companyz.ems.model.Division;
import com.companyz.ems.model.Payroll;
import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.model.employee.EmploymentStatus;
import com.companyz.ems.model.employee.FullTimeEmployee;
import com.companyz.ems.model.employee.JobTitle;
import com.companyz.ems.security.AuthorizationService;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.utils.SsnEncryptor;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao employeeDao;
    private final DivisionDao divisionDao;
    private final JobTitleDao jobTitleDao;
    private final EmploymentStatusDao statusDao;
    private final PayrollDao payrollDao;
    private final SsnEncryptor ssnEncryptor;
    private final AuthorizationService authzService;

    public EmployeeServiceImpl(EmployeeDao employeeDao,
                               DivisionDao divisionDao,
                               JobTitleDao jobTitleDao,
                               EmploymentStatusDao statusDao,
                               PayrollDao payrollDao,
                               SsnEncryptor ssnEncryptor,
                               AuthorizationService authzService) {
        this.employeeDao = employeeDao;
        this.divisionDao = divisionDao;
        this.jobTitleDao = jobTitleDao;
        this.statusDao = statusDao;
        this.payrollDao = payrollDao;
        this.ssnEncryptor = ssnEncryptor;
        this.authzService = authzService;
    }

    public EmployeeServiceImpl() {
        this.employeeDao = new EmployeeDaoImpl();
        this.divisionDao = new DivisionDaoImpl();
        this.jobTitleDao = new JobTitleDaoImpl();
        this.statusDao = new EmploymentStatusDaoImpl();
        this.payrollDao = new PayrollDaoImpl();
        this.ssnEncryptor = new SsnEncryptor();
        this.authzService = new AuthorizationService();
    }

    // --- Employee CRUD ---
    @Override
    public Optional<Employee> getEmployeeByName(SessionContext ctx, String firstName, String lastName) {
        authzService.requireAdmin(ctx);
        return employeeDao.findByName(firstName, lastName);
    }

    @Override
    public List<Employee> getAllEmployees(SessionContext ctx) {
        authzService.requireAdmin(ctx);
        return employeeDao.findAll();
    }

    @Override
    public boolean createEmployee(SessionContext ctx,
                                  String firstName,
                                  String lastName,
                                  String gender,
                                  String race,
                                  LocalDate dob,
                                  String ssn,
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
                                  LocalDate hireDate) {
        authzService.requireAdmin(ctx);

        try {
            String ssnLast4 = ssnEncryptor.extractLast4(ssn);
            byte[] ssnHash = ssnEncryptor.hashSsn(ssn);
            byte[] iv = ssnEncryptor.generateIv();
            byte[] ssnEnc = ssnEncryptor.encryptSsn(ssn, iv);

            FullTimeEmployee emp = new FullTimeEmployee();
            emp.setFirstName(firstName);
            emp.setLastName(lastName);
            emp.setGender(gender);
            emp.setRace(race);
            emp.setDob(dob);
            emp.setSsnLast4(ssnLast4);
            emp.setSsnHash(new String(ssnHash)); // or store as bytes depending on schema
            emp.setSsnEnc(ssnEnc);
            emp.setSsnIv(iv);
            emp.setPrimaryEmail(primaryEmail);
            emp.setPrimaryPhoneNumber(primaryPhoneNumber);
            emp.setHireDate(hireDate);
            // set other fields like address, divisionId, jobTitleId, etc.

            employeeDao.createEmployee(emp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateEmployee(SessionContext ctx,
                                  int empId,
                                  String firstName,
                                  String lastName,
                                  String gender,
                                  String race,
                                  LocalDate dob,
                                  String ssn,
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
                                  LocalDate hireDate) {
        authzService.requireAdmin(ctx);

        try {
            String ssnLast4 = ssnEncryptor.extractLast4(ssn);
            byte[] ssnHash = ssnEncryptor.hashSsn(ssn);
            byte[] iv = ssnEncryptor.generateIv();
            byte[] ssnEnc = ssnEncryptor.encryptSsn(ssn, iv);

            FullTimeEmployee emp = new FullTimeEmployee();
            emp.setEmpId(empId);
            emp.setFirstName(firstName);
            emp.setLastName(lastName);
            emp.setGender(gender);
            emp.setRace(race);
            emp.setDob(dob);
            emp.setSsnLast4(ssnLast4);
            emp.setSsnHash(new String(ssnHash));
            emp.setSsnEnc(ssnEnc);
            emp.setSsnIv(iv);
            emp.setPrimaryEmail(primaryEmail);
            emp.setPrimaryPhoneNumber(primaryPhoneNumber);
            emp.setHireDate(hireDate);
            // set other fields

            employeeDao.updateEmployee(emp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteEmployee(SessionContext ctx, int empId) {
        authzService.requireAdmin(ctx);
        return employeeDao.deleteEmployee(empId);
    }

    // --- Employee Self Access ---
    @Override
    public Optional<Employee> getSelfEmployeeInfo(SessionContext ctx) {
        return employeeDao.findById(ctx.getEmployeeId());
    }

    // --- Employee Search ---
    @Override
    public Optional<Employee> searchByName(SessionContext ctx, String firstName, String lastName) {
        authzService.requireAdmin(ctx);
        return employeeDao.findByName(firstName, lastName);
    }

    @Override
    public Optional<Employee> searchBySsn(SessionContext ctx, String ssn) {
        authzService.requireAdmin(ctx);
        try {
            byte[] hash = ssnEncryptor.hashSsn(ssn);
            return employeeDao.findBySsn(new String(hash));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Employee> searchByDob(SessionContext ctx, LocalDate dob) {
        authzService.requireAdmin(ctx);
        return employeeDao.findByDob(dob);
    }

    @Override
    public Optional<Employee> searchByEmpId(SessionContext ctx, int empId) {
        authzService.requireAdmin(ctx);
        return employeeDao.findById(empId);
    }

    // --- Division CRUD ---
    @Override
    public Optional<Division> getDivisionByName(SessionContext ctx, String name) {
        authzService.requireAdmin(ctx);
        return divisionDao.findByName(name);
    }

    @Override
    public List<Division> getAllDivisions(SessionContext ctx) {
        authzService.requireAdmin(ctx);
        return divisionDao.findAll();
    }

    @Override
    public boolean createDivision(SessionContext ctx, String name, String description,
                                  String addressLine1, String addressLine2,
                                  String city, String state, String country, String postalCode,
                                  boolean isActive) {
        authzService.requireAdmin(ctx);

        Division div = new Division();
        div.setDivisionName(name);
        div.setDescription(description);
        // set address object
        div.setIsActive(isActive);
        divisionDao.createDivision(div);
        return true;
    }

    @Override
    public boolean updateDivision(SessionContext ctx, int divisionId, String name, String description,
                                  String addressLine1, String addressLine2,
                                  String city, String state, String country, String postalCode,
                                  boolean isActive) {
        authzService.requireAdmin(ctx);

        Division div = new Division();
        div.setDivisionId(divisionId);
        div.setDivisionName(name);
        div.setDescription(description);
        div.setIsActive(isActive);
        divisionDao.updateDivision(div);
        return true;
    }

    @Override
    public boolean deleteDivision(SessionContext ctx, int divisionId) {
        authzService.requireAdmin(ctx);
        return divisionDao.deleteDivision(divisionId);
    }

    // --- Job Title CRUD ---
    @Override
    public Optional<JobTitle> getJobTitleByName(SessionContext ctx, String name) {
        authzService.requireAdmin(ctx);
        return jobTitleDao.findByName(name);
    }

    @Override
    public List<JobTitle> getAllJobTitles(SessionContext ctx) {
        authzService.requireAdmin(ctx);
        return jobTitleDao.findAll();
    }

    @Override
    public boolean createJobTitle(SessionContext ctx, String name, String description) {
        authzService.requireAdmin(ctx);

        JobTitle jt = new JobTitle();
        jt.setTitleName(name);
        jt.setDescription(description);
        jobTitleDao.createJobTitle(jt);
        return true;
    }

    @Override
    public boolean updateJobTitle(SessionContext ctx, int jobTitleId, String name, String description) {
        authzService.requireAdmin(ctx);

        JobTitle jt = new JobTitle();
        jt.setJobTitleId(jobTitleId);
        jt.setTitleName(name);
        jt.setDescription(description);
        jobTitleDao.updateJobTitle(jt);
        return true;
    }

    @Override
    public boolean deleteJobTitle(SessionContext ctx, int jobTitleId) {
        authzService.requireAdmin(ctx);

        return jobTitleDao.deleteJobTitle(jobTitleId);
    }

    // --- Employment Status CRUD ---
    @Override
    public Optional<EmploymentStatus> getEmploymentStatusByName(SessionContext ctx, String name) {
        authzService.requireAdmin(ctx);
        return statusDao.findByName(name);
    }

    @Override
    public List<EmploymentStatus> getAllEmploymentStatuses(SessionContext ctx) {
        authzService.requireAdmin(ctx);
        return statusDao.findAll();
    }

        // --- Employment Status CRUD ---
    @Override
    public boolean createEmploymentStatus(SessionContext ctx, String name, String description,
                                          LocalDate effectiveStart, LocalDate effectiveEnd) {
        authzService.requireAdmin(ctx);

        EmploymentStatus status = new EmploymentStatus();
        status.setStatusName(name);
        status.setDescription(description);
        status.setEffectiveStart(effectiveStart);
        status.setEffectiveEnd(effectiveEnd);
        statusDao.createStatus(status);
        return true;
    }

    @Override
    public boolean updateEmploymentStatus(SessionContext ctx, int statusId, String name, String description,
                                          LocalDate effectiveStart, LocalDate effectiveEnd) {
        authzService.requireAdmin(ctx);

        EmploymentStatus status = new EmploymentStatus();
        status.setStatusId(statusId);
        status.setStatusName(name);
        status.setDescription(description);
        status.setEffectiveStart(effectiveStart);
        status.setEffectiveEnd(effectiveEnd);
        statusDao.updateStatus(status);
        return true;
    }

    @Override
    public boolean deleteEmploymentStatus(SessionContext ctx, int statusId) {
        authzService.requireAdmin(ctx);
        return statusDao.deleteStatus(statusId);
    }

    // --- Payroll CRUD ---
    @Override
    public List<Payroll> getAllPayrolls(SessionContext ctx) {
        authzService.requireAdmin(ctx);
        return payrollDao.findAll();
    }

    @Override
    public boolean createPayroll(SessionContext ctx, int empId, LocalDate payDate,
                                 double earnings, double fedTax, double fedMed, double fedSs,
                                 double stateTax, double retire401k, double healthCare, double netPay) {
        authzService.requireAdmin(ctx);

        Payroll payroll = new Payroll();
        payroll.setEmpId(empId);
        payroll.setPayDate(payDate);
        payroll.setEarnings(earnings);
        payroll.setFedTax(fedTax);
        payroll.setFedMed(fedMed);
        payroll.setFedSs(fedSs);
        payroll.setStateTax(stateTax);
        payroll.setRetire401k(retire401k);
        payroll.setHealthCare(healthCare);
        payroll.setNetPay(netPay);
        payrollDao.createPayroll(payroll);
        return true;
    }

    @Override
    public boolean updatePayroll(SessionContext ctx, int payrollId, LocalDate payDate,
                                 double earnings, double fedTax, double fedMed, double fedSs,
                                 double stateTax, double retire401k, double healthCare, double netPay) {
        authzService.requireAdmin(ctx);

        Payroll payroll = new Payroll();
        payroll.setPayrollId(payrollId);
        payroll.setPayDate(payDate);
        payroll.setEarnings(earnings);
        payroll.setFedTax(fedTax);
        payroll.setFedMed(fedMed);
        payroll.setFedSs(fedSs);
        payroll.setStateTax(stateTax);
        payroll.setRetire401k(retire401k);
        payroll.setHealthCare(healthCare);
        payroll.setNetPay(netPay);
        payrollDao.updatePayroll(payroll);
        return true;
    }

    @Override
    public boolean deletePayroll(SessionContext ctx, int payrollId) {
        authzService.requireAdmin(ctx);
        return payrollDao.deletePayroll(payrollId);
    }

    // --- Increase salary by range ---
    @Override
    public int increaseSalaryByRange(SessionContext ctx, double percent, double minSalary, double maxSalary,
                                    String reason) {
        authzService.requireAdmin(ctx);
        return employeeDao.increaseSalaryByRange(percent, minSalary, maxSalary, reason, ctx.getUserId());
    }
}
