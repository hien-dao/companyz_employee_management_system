package com.companyz.ems.model.report;

/**
 * Report: total pay for a job title in a given month/year.
 * Audience: HR Admin.
 */
public class JobTitleMonthlyPayReport {
    private final int jobTitleId;
    private final String jobTitleName;
    private final int month; // 1-12
    private final int year;
    private final double totalPay;

    public JobTitleMonthlyPayReport(int jobTitleId,
                                    String jobTitleName,
                                    int month,
                                    int year,
                                    double totalPay) {
        this.jobTitleId = jobTitleId;
        this.jobTitleName = jobTitleName;
        this.month = month;
        this.year = year;
        this.totalPay = totalPay;
    }

    public int getJobTitleId() { return jobTitleId; }
    public String getJobTitleName() { return jobTitleName; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getTotalPay() { return totalPay; }
}
