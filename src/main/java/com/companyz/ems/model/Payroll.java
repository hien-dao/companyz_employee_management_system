package com.companyz.ems.model;

import java.time.LocalDate;

/**
 * Represents a payroll entry for a single employee within a payroll run.
 * <p>
 * Stores earnings and standard deductions; `netPay` may be calculated
 * by subtracting deductions from gross earnings.
 * </p>
 */
public class Payroll {
    /** Primary identifier for the payroll entry. */
    private int payrollId;

    /** Employee this payroll entry belongs to. */
    private Employee employee;

    /** Actual pay date for this payroll entry. */
    private LocalDate payDate;

    /** Gross earnings for the pay period. */
    private double earnings;

    /** Federal tax withheld. */
    private double fedTax;

    /** Medicare withheld. */
    private double fedMed;

    /** Social Security withheld. */
    private double fedSs;

    /** State tax withheld. */
    private double stateTax;

    /** Retirement (401k) contribution. */
    private double retire401k;

    /** Healthcare deduction. */
    private double healthCare;

    /** Calculated net pay after deductions. */
    private double netPay;

    /**
     * Returns the payroll entry identifier.
     *
     * @return payroll id
     */
    public int getPayrollId() {
        return payrollId;
    }

    /**
     * Sets the payroll entry identifier.
     *
     * @param payrollId id to set
     */
    public void setPayrollId(int payrollId) {
        this.payrollId = payrollId;
    }

    /**
     * Returns the employee for this payroll entry.
     *
     * @return {@link Employee} or {@code null}
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets the employee for this payroll entry.
     *
     * @param employee employee to set
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * Returns the pay date.
     *
     * @return pay date or {@code null}
     */
    public LocalDate getPayDate() {
        return payDate;
    }

    /**
     * Sets the pay date.
     *
     * @param payDate date to set
     */
    public void setPayDate(LocalDate payDate) {
        this.payDate = payDate;
    }

    /**
     * Returns gross earnings.
     *
     * @return earnings
     */
    public double getEarnings() {
        return earnings;
    }

    /**
     * Sets gross earnings.
     *
     * @param earnings earnings to set
     */
    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    /**
     * Returns federal tax withheld.
     *
     * @return federal tax amount
     */
    public double getFedTax() {
        return fedTax;
    }

    /**
     * Sets federal tax withheld.
     *
     * @param fedTax federal tax amount
     */
    public void setFedTax(double fedTax) {
        this.fedTax = fedTax;
    }

    /**
     * Returns Medicare withheld.
     *
     * @return medicare amount
     */
    public double getFedMed() {
        return fedMed;
    }

    /**
     * Sets Medicare withheld.
     *
     * @param fedMed medicare amount
     */
    public void setFedMed(double fedMed) {
        this.fedMed = fedMed;
    }

    /**
     * Returns Social Security withheld.
     *
     * @return social security amount
     */
    public double getFedSs() {
        return fedSs;
    }

    /**
     * Sets Social Security withheld.
     *
     * @param fedSs social security amount
     */
    public void setFedSs(double fedSs) {
        this.fedSs = fedSs;
    }

    /**
     * Returns state tax withheld.
     *
     * @return state tax amount
     */
    public double getStateTax() {
        return stateTax;
    }

    /**
     * Sets state tax withheld.
     *
     * @param stateTax state tax amount
     */
    public void setStateTax(double stateTax) {
        this.stateTax = stateTax;
    }

    /**
     * Returns retirement (401k) contribution.
     *
     * @return retirement contribution
     */
    public double getRetire401k() {
        return retire401k;
    }

    /**
     * Sets retirement (401k) contribution.
     *
     * @param retire401k retirement contribution to set
     */
    public void setRetire401k(double retire401k) {
        this.retire401k = retire401k;
    }

    /**
     * Returns healthcare deduction.
     *
     * @return healthcare deduction
     */
    public double getHealthCare() {
        return healthCare;
    }

    /**
     * Sets healthcare deduction.
     *
     * @param healthCare healthcare amount to set
     */
    public void setHealthCare(double healthCare) {
        this.healthCare = healthCare;
    }

    /**
     * Returns calculated net pay.
     *
     * @return net pay amount
     */
    public double getNetPay() {
        return netPay;
    }

    /**
     * Sets the calculated net pay.
     *
     * @param netPay net pay amount to set
     */
    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

}
