package com.companyz.ems.model.employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.companyz.ems.model.Address;
import com.companyz.ems.model.Contact;
import com.companyz.ems.model.Person;

/**
 * Abstract base class for all employee types.
 * <p>
 * Extends {@link Person} to provide
 * common employee-specific functionality. Subclasses should specialize this class
 * for different employment types (full-time, part-time, contractor, etc.).
 * </p>
 */
public abstract class BaseEmployee extends Person {
    /** Primary key identifier for the employee. */
    private int empId;

    /** Contact records associated with the employee (optional). */
    private List<Contact> contacts;

    /** In-memory address for the employee (optional). */
    private Address address;

    /** Job title id the employee holds. */
    private int jobTitleId;

    /** Employment status id of the employee. */
    private int employmentStatusId;

    /** Division (business unit) id the employee is assigned to. */
    private int divisionId;

    /** Hire date as a {@link LocalDate}. */
    private LocalDate hireDate;

    /** Timestamp when the employee record was created. */
    private LocalDateTime createdAt;

    /** Timestamp when the employee record was last updated. */
    private LocalDateTime updatedAt;
    
    /**
     * Returns the employee identifier.
     *
     * @return the employee id
     */
    public int getEmpId() {
        return empId;
    }

    /**
     * Sets the employee identifier.
     *
     * @param empId the employee id to set
     */
    public void setEmpId(int empId) {
        this.empId = empId;
    }

    /**
     * Returns the contact records associated with the employee.
     *
     * @return list of {@link Contact} objects or {@code null} if none
     */
    public List<Contact> getContacts() {
        return contacts;
    }

    /**
     * Sets the contact records for the employee.
     *
     * @param contacts list of {@link Contact} objects
     */
    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    /**
     * Returns the in-memory {@link Address} for the employee.
     *
     * @return {@link Address} or {@code null}
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the in-memory {@link Address} for the employee.
     *
     * @param address the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Returns the identifier of the employee's job title.
     *
     * @return job title id
     */
    public int getJobTitleId() {
        return jobTitleId;
    }

    /**
     * Sets the identifier of the employee's job title.
     *
     * @param jobTitleId the job title id to set
     */
    public void setJobTitleId(int jobTitleId) {
        this.jobTitleId = jobTitleId;
    }

    /**
     * Returns the identifier of the employee's employment status.
     *
     * @return employment status id
     */
    public int getEmploymentStatusId() {
        return employmentStatusId;
    }

    /**
     * Sets the identifier of the employee's employment status.
     *
     * @param employmentStatusId the employment status id to set
     */
    public void setEmploymentStatusId(int employmentStatusId) {
        this.employmentStatusId = employmentStatusId;
    }

    /**
     * Returns the division id the employee is assigned to.
     *
     * @return division id
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Sets the division id the employee is assigned to.
     *
     * @param divisionId the division id to set
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * Returns the hire date for the employee.
     *
     * @return hire date or {@code null}
     */
    public LocalDate getHireDate() {
        return hireDate;
    }

    /**
     * Sets the hire date for the employee.
     *
     * @param hireDate the hire date to set
     */
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    /**
     * Returns the record creation timestamp.
     *
     * @return creation timestamp or {@code null}
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the record creation timestamp.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the record last-update timestamp.
     *
     * @return update timestamp or {@code null}
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the record last-update timestamp.
     *
     * @param updatedAt the update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
