package com.companyz.ems.ui;

import com.companyz.ems.model.report.EmployeePayrollReport;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.ReportService;
import com.companyz.ems.utils.DialogUtil;
import com.companyz.ems.utils.UIConstants;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Payroll tab for general employees.
 * Displays the logged-in employee's payroll history sorted by most recent pay date.
 */
public class PayrollUI {
    private final ReportService reportService;

    public PayrollUI(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Build the payroll tab for the current session.
     *
     * @param session current user session
     * @return a Tab showing payroll history
     */
    public Tab build(SessionContext session) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(15));

        Label title = new Label("Payroll History");
        title.setStyle(UIConstants.TITLE_STYLE);

        VBox reportBox = new VBox(10);
        reportBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; "
                + "-fx-padding: 15; -fx-background-color: #fafafa;");

        Label reportTitle = new Label("Your Pay Statements (Most recent first)");
        reportTitle.setStyle(UIConstants.SUBTITLE_STYLE);

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefHeight(400);

        Button generateButton = new Button("Generate Report");
        generateButton.setStyle(UIConstants.BUTTON_PRIMARY_STYLE);

        generateButton.setOnAction(e -> {
            try {
                EmployeePayrollReport payrollReport = reportService.getEmployeePayrollHistory(session);
                // Assuming EmployeePayrollReport has a meaningful toString() implementation
                reportArea.setText(payrollReport.toString());
            } catch (Exception ex) {
                DialogUtil.showError("Error generating payroll report: " + ex.getMessage());
            }
        });

        reportBox.getChildren().addAll(reportTitle, generateButton, reportArea);
        vbox.getChildren().addAll(title, reportBox);

        return new Tab("Payroll", vbox);
    }
}
