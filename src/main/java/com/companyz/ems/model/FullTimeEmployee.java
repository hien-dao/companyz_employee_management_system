package com.companyz.ems.model;

/**
 * Concrete employee type for full-time employees.
 * <p>
 * Stores compensation information specific to full-time staff and
 * implements {@link Payable} to produce payroll-related values.
 * </p>
 */
public class FullTimeEmployee extends Employee {
    /** Annual salary in the company's base currency. */
    private double salary;

    /**
     * Returns the employee's annual salary.
     *
     * @return annual salary as a double
     */
    public double getSalary() {
        return salary;
    }

    /**
     * Sets the employee's annual salary.
     *
     * @param salary annual salary to set
     */
    public void setSalary(double salary) {
        this.salary = salary;
    }

    // Implementation of Payable is provided by getSalary().
}
