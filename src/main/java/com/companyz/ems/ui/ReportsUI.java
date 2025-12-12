package com.companyz.ems.ui;

import java.time.LocalDate;

import com.companyz.ems.model.report.DivisionMonthlyPayReport;
import com.companyz.ems.model.report.EmployeeHireReport;
import com.companyz.ems.model.report.JobTitleMonthlyPayReport;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.ReportService;
import com.companyz.ems.utils.DialogUtil;
import com.companyz.ems.utils.UIConstants;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Reports tab for HR Admin.
 * Provides job title monthly pay, division monthly pay, and hire date range reports.
 */
public class ReportsUI {
    private final ReportService reportService;

    public ReportsUI(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Build the Reports tab for HR Admin.
     *
     * @param session current user session
     * @return a Tab with reporting features
     */
    public Tab build(SessionContext session) {
        VBox vbox = new VBox(UIConstants.DEFAULT_SPACING);
        vbox.setPadding(new javafx.geometry.Insets(UIConstants.DEFAULT_PADDING));

        Label title = new Label("Reports (HR Admin)");
        title.setStyle(UIConstants.TITLE_STYLE);

        if (!isAdmin(session)) {
            VBox infoBox = new VBox(UIConstants.DEFAULT_SPACING);
            infoBox.setPadding(new javafx.geometry.Insets(UIConstants.DEFAULT_PADDING));
            infoBox.setStyle(UIConstants.BORDER_STYLE);
            infoBox.getChildren().add(new Label("You do not have permission to view this section."));
            vbox.getChildren().addAll(title, infoBox);
            return new Tab("Reports", vbox);
        }

        VBox reportOptions = new VBox(UIConstants.DEFAULT_SPACING);
        reportOptions.setStyle(UIConstants.BORDER_STYLE);
        Label reportTitle = new Label("Available Admin Reports");
        reportTitle.setStyle(UIConstants.SUBTITLE_STYLE);

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);

        // Employees hired within date range
        HBox hireReportBox = new HBox(UIConstants.DEFAULT_SPACING);
        hireReportBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label hireLabel = new Label("Employees hired within date range:");
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        Button hireReportBtn = new Button("Generate");
        hireReportBtn.setStyle(UIConstants.BUTTON_PRIMARY_STYLE);
        hireReportBtn.setOnAction(e -> {
            if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                try {
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();
                    EmployeeHireReport report = reportService.getEmployeesHiredWithinDateRange(session,
                            startDate.getDayOfMonth(), startDate.getMonthValue(), startDate.getYear(),
                            endDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getYear());
                    outputArea.setText(report.toString());
                } catch (Exception ex) {
                    DialogUtil.showError("Error generating hire report: " + ex.getMessage());
                }
            } else {
                DialogUtil.showError("Please select both start and end dates");
            }
        });
        hireReportBox.getChildren().addAll(hireLabel, startDatePicker, endDatePicker, hireReportBtn);

        // Job Title Monthly Pay
        HBox jobTitleBox = new HBox(UIConstants.DEFAULT_SPACING);
        jobTitleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label jobLabel = new Label("Total pay for month by Job Title:");
        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Job Title");
        Spinner<Integer> jtYearSpinner = new Spinner<>(2020, 2050, LocalDate.now().getYear());
        Spinner<Integer> jtMonthSpinner = new Spinner<>(1, 12, LocalDate.now().getMonthValue());
        Button jobReportBtn = new Button("Generate");
        jobReportBtn.setStyle(UIConstants.BUTTON_PRIMARY_STYLE);
        jobReportBtn.setOnAction(e -> {
            if (!jobTitleField.getText().isEmpty()) {
                try {
                    JobTitleMonthlyPayReport report = reportService.getMonthlyPayByJobTitle(session,
                            jobTitleField.getText(), jtYearSpinner.getValue(), jtMonthSpinner.getValue());
                    outputArea.setText(report.toString());
                } catch (Exception ex) {
                    DialogUtil.showError("Error generating job title report: " + ex.getMessage());
                }
            } else {
                DialogUtil.showError("Please enter a job title");
            }
        });
        jobTitleBox.getChildren().addAll(jobLabel, jobTitleField, jtYearSpinner, jtMonthSpinner, jobReportBtn);

        // Division Monthly Pay
        HBox divisionBox = new HBox(UIConstants.DEFAULT_SPACING);
        divisionBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label divLabel = new Label("Total pay for month by Division:");
        TextField divisionField = new TextField();
        divisionField.setPromptText("Division");
        Spinner<Integer> divYearSpinner = new Spinner<>(2020, 2050, LocalDate.now().getYear());
        Spinner<Integer> divMonthSpinner = new Spinner<>(1, 12, LocalDate.now().getMonthValue());
        Button divisionReportBtn = new Button("Generate");
        divisionReportBtn.setStyle(UIConstants.BUTTON_PRIMARY_STYLE);
        divisionReportBtn.setOnAction(e -> {
            if (!divisionField.getText().isEmpty()) {
                try {
                    DivisionMonthlyPayReport report = reportService.getMonthlyPayByDivision(session,
                            divisionField.getText(), divYearSpinner.getValue(), divMonthSpinner.getValue());
                    outputArea.setText(report.toString());
                } catch (Exception ex) {
                    DialogUtil.showError("Error generating division report: " + ex.getMessage());
                }
            } else {
                DialogUtil.showError("Please enter a division");
            }
        });
        divisionBox.getChildren().addAll(divLabel, divisionField, divYearSpinner, divMonthSpinner, divisionReportBtn);

        reportOptions.getChildren().addAll(reportTitle, hireReportBox, jobTitleBox, divisionBox, outputArea);
        vbox.getChildren().addAll(title, reportOptions);

        return new Tab("Reports", vbox);
    }

    private boolean isAdmin(SessionContext session) {
        return session != null && session.getRole() != null
                && "HR_ADMIN".equalsIgnoreCase(session.getRole());
    }
}

