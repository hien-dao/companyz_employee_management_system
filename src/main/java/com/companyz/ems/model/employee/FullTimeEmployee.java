package com.companyz.ems.model.employee;

import com.companyz.ems.model.Contact;

/**
 * Represents a full-time employee with a fixed annual salary.
 * Extends {@link BaseEmployee} and provides salary-specific functionality
 * for employees on full-time employment contracts.
 */
public class FullTimeEmployee extends BaseEmployee implements Employee {
    /** Employment type description (e.g., "Full-Time"). */
    private String employmentTypeString;

    /** Employment status description (e.g., "Active"). */
    private String employmentStatusString;

    /** Job title description (e.g., "Software Engineer"). */
    private String jobTitleString;

    /** Division name description (e.g., "Engineering"). */
    private String divisionNameString;

    /** Annual salary for the full-time employee. */
    private double annualSalary;

    @Override
    public String getEmploymentTypeString() {
        return employmentTypeString;
    }
    public void setEmploymentTypeString(String employmentTypeString) {
        this.employmentTypeString = employmentTypeString;
    }

    @Override
    public String getEmploymentStatusString() {
        return employmentStatusString;
    }
    public void setEmploymentStatusString(String employmentStatusString) {
        this.employmentStatusString = employmentStatusString;
    }

    @Override
    public String getJobTitleString() {
        return jobTitleString;
    }
    public void setJobTitleString(String jobTitleString) {
        this.jobTitleString = jobTitleString;
    }

    @Override
    public String getDivisionString() {
        return divisionNameString;
    }
    public void setDivisionString(String divisionNameString) {
        this.divisionNameString = divisionNameString;
    }

    @Override
    public double getSalary() {
        return annualSalary;
    }
    public void setSalary(double annualSalary) {
        this.annualSalary = annualSalary;
    }

    @Override
public String getPrimaryEmail() {
    if (getContacts() == null) return null;
    return getContacts().stream()
        .filter(c -> "EMAIL".equalsIgnoreCase(c.getContactType()) && Boolean.TRUE.equals(c.getIsPrimary()))
        .map(Contact::getContactValue)
        .findFirst()
        .orElse(null);
}

    @Override
    public String getPrimaryPhoneNumber() {
        if (getContacts() == null) return null;
        return getContacts().stream()
            .filter(c -> ("PHONE".equalsIgnoreCase(c.getContactType()) || "MOBILE".equalsIgnoreCase(c.getContactType()))
                        && Boolean.TRUE.equals(c.getIsPrimary()))
            .map(Contact::getContactValue)
            .findFirst()
            .orElse(null);
    }

    @Override
    public String getAddressLine1() {
        return getAddress() != null ? getAddress().getAddressLine1() : null;
    }

    @Override
    public String getAddressLine2() {
        return getAddress() != null ? getAddress().getAddressLine2() : null;
    }

    @Override
    public String getCity() {
        return getAddress() != null ? getAddress().getCity() : null;
    }

    @Override
    public String getState() {
        return getAddress() != null ? getAddress().getState() : null;
    }

    @Override
    public String getCountry() {
        return getAddress() != null ? getAddress().getCountry() : null;
    }

    @Override
    public String getZipCode() {
        return getAddress() != null ? getAddress().getPostalCode() : null;
    }
}
