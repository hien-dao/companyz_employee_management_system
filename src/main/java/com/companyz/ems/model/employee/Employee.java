package com.companyz.ems.model.employee;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Interface defining the contract for employee objects.
 * <p>
 * All employee implementations must provide access to core employee information
 * including identification, contact details, job assignments, and employment status.
 * </p>
 */
public interface Employee {
    int getEmpId();
    String getFirstName();
    String getLastName();

    String getGender();
    String getRace();
    LocalDate getDob();
    String getSsnLast4();

    String getPrimaryEmail();
    String getPrimaryPhoneNumber();
    String getAddressLine1();
    String getAddressLine2();
    String getCity();
    String getState();
    String getCountry();
    String getZipCode();

    String getEmploymentTypeString();
    String getEmploymentStatusString();
    String getJobTitleString();
    String getDivisionString();

    double getSalary();

    LocalDate getHireDate();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
