package com.companyz.ems.model.employee;

import java.time.LocalDate;

/**
 * Represents the employment status of an employee.
 * <p>
 * Employment statuses are stored in the database (employee_status table)
 * and can include values such as ACTIVE, TERMINATED, ON_LEAVE, etc.
 * </p>
 */
public class EmploymentStatus {
    private int statusId;           // PK from employee_status table
    private String statusName;      // e.g., "ACTIVE", "ON_LEAVE"
    private String description;     // optional human-readable description
    private LocalDate effectiveStart;
    private LocalDate effectiveEnd;

    public EmploymentStatus() {}

    public EmploymentStatus(int statusId, String statusName, String description,
                            LocalDate effectiveStart, LocalDate effectiveEnd) {
        this.statusId = statusId;
        this.statusName = statusName;
        this.description = description;
        this.effectiveStart = effectiveStart;
        this.effectiveEnd = effectiveEnd;
    }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getEffectiveStart() { return effectiveStart; }
    public void setEffectiveStart(LocalDate effectiveStart) { this.effectiveStart = effectiveStart; }

    public LocalDate getEffectiveEnd() { return effectiveEnd; }
    public void setEffectiveEnd(LocalDate effectiveEnd) { this.effectiveEnd = effectiveEnd; }

}
