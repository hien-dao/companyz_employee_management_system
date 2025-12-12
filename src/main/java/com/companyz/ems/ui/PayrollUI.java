package com.companyz.ems.ui;

import com.companyz.ems.model.Payroll;
import com.companyz.ems.model.report.EmployeePayrollReport;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.ReportService;
import com.companyz.ems.utils.DialogUtil;
import com.companyz.ems.utils.UIConstants;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

        // TableView for Payroll entries
        TableView<Payroll> payrollTable = new TableView<>();
        payrollTable.setPrefHeight(500);

        // Define columns based on UML
        TableColumn<Payroll, Integer> idCol = new TableColumn<>("Payroll ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("payrollId"));

        TableColumn<Payroll, String> dateCol = new TableColumn<>("Pay Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("payDate"));

        TableColumn<Payroll, Double> earningsCol = new TableColumn<>("Earnings");
        earningsCol.setCellValueFactory(new PropertyValueFactory<>("earnings"));

        TableColumn<Payroll, Double> fedTaxCol = new TableColumn<>("Fed Tax");
        fedTaxCol.setCellValueFactory(new PropertyValueFactory<>("fedTax"));

        TableColumn<Payroll, Double> medCol = new TableColumn<>("Medicare");
        medCol.setCellValueFactory(new PropertyValueFactory<>("fedMed"));

        TableColumn<Payroll, Double> ssCol = new TableColumn<>("Social Security");
        ssCol.setCellValueFactory(new PropertyValueFactory<>("fedSs"));

        TableColumn<Payroll, Double> stateCol = new TableColumn<>("State Tax");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("stateTax"));

        TableColumn<Payroll, Double> retireCol = new TableColumn<>("401k");
        retireCol.setCellValueFactory(new PropertyValueFactory<>("retire401k"));

        TableColumn<Payroll, Double> healthCol = new TableColumn<>("Healthcare");
        healthCol.setCellValueFactory(new PropertyValueFactory<>("healthCare"));

        TableColumn<Payroll, Double> netCol = new TableColumn<>("Net Pay");
        netCol.setCellValueFactory(new PropertyValueFactory<>("netPay"));

        payrollTable.getColumns().addAll(idCol, dateCol, earningsCol, fedTaxCol, medCol,
                                         ssCol, stateCol, retireCol, healthCol, netCol);

        Button generateButton = new Button("Generate Report");
        generateButton.setStyle(UIConstants.BUTTON_PRIMARY_STYLE);

        generateButton.setOnAction(e -> {
            try {
                EmployeePayrollReport payrollReport = reportService.getEmployeePayrollHistory(session);
                payrollTable.setItems(FXCollections.observableArrayList(payrollReport.getPayrolls()));
            } catch (Exception ex) {
                DialogUtil.showError("Error generating payroll report: " + ex.getMessage());
            }
        });

        reportBox.getChildren().addAll(reportTitle, generateButton, payrollTable);
        vbox.getChildren().addAll(title, reportBox);

        return new Tab("Payroll", vbox);
    }
}
