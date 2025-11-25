package com.companyz.ems.model;

/**
 * Abstract employee base class containing employment-specific attributes.
 * <p>
 * Subclasses should extend this class to provide role-specific behaviour.
 * This class stores identifiers and contact/job-related fields. Basic
 * getters and setters are provided for each field.
 * </p>
 */
public abstract class Employee extends Person {
    /** Numeric primary identifier for the employee. */
    private int empId;

    /** Primary contact email for the employee. */
    private String email;

    /** Primary contact phone number for the employee. */
    private String phoneNumber;

    /** Current job title for the employee (e.g. "Software Engineer"). */
    private String jobTitle;

    /** Employment status label (e.g. "ACTIVE", "ON_LEAVE"). */
    private String employmentStatus;

    /** Hire date as a string (recommended format: yyyy-MM-dd). */
    private String hireDate;

    /**
     * Returns the employee's numeric identifier.
     *
     * @return the empId
     */
    public int getEmpId() {
        return empId;
    }

    /**
     * Sets the employee's numeric identifier.
     *
     * @param empId the employee id to set
     */
    public void setEmpId(int empId) {
        this.empId = empId;
    }

    /**
     * Returns the employee's email address.
     *
     * @return email address or {@code null} if not set
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the employee's email address.
     *
     * @param email email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the employee's phone number.
     *
     * @return phone number or {@code null} if not set
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the employee's phone number.
     *
     * @param phoneNumber phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the employee's job title.
     *
     * @return job title string or {@code null} if not set
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the employee's job title.
     *
     * @param jobTitle job title to set
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Returns the employment status label.
     *
     * @return employment status (e.g. "ACTIVE") or {@code null}
     */
    public String getEmploymentStatus() {
        return employmentStatus;
    }

    /**
     * Sets the employment status label.
     *
     * @param employmentStatus status to set
     */
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    /**
     * Returns the hire date string.
     *
     * @return hire date (recommended yyyy-MM-dd) or {@code null}
     */
    public String getHireDate() {
        return hireDate;
    }

    /**
     * Sets the hire date.
     *
     * @param hireDate hire date string (recommended format: yyyy-MM-dd)
     */
    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }
}
