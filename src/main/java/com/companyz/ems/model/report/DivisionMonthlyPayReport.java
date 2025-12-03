package com.companyz.ems.model.report;

/**
 * Report: total pay for a division in a given month/year.
 * Audience: HR Admin.
 */
public class DivisionMonthlyPayReport {
    private final int divisionId;
    private final String divisionName;
    private final int month; // 1-12
    private final int year;
    private final double totalPay;

    public DivisionMonthlyPayReport(int divisionId,
                                    String divisionName,
                                    int month,
                                    int year,
                                    double totalPay) {
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        this.month = month;
        this.year = year;
        this.totalPay = totalPay;
    }

    public int getDivisionId() { return divisionId; }
    public String getDivisionName() { return divisionName; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getTotalPay() { return totalPay; }
}
