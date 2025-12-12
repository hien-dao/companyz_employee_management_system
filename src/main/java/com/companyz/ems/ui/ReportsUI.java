package com.companyz.ems.ui;

import java.time.LocalDate;

import com.companyz.ems.model.report.DivisionMonthlyPayReport;
import com.companyz.ems.model.report.EmployeeHireReport;
import com.companyz.ems.model.report.JobTitleMonthlyPayReport;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.ReportService;
import com.companyz.ems.utils.DialogUtil;
import com.companyz.ems.utils.UIConstants;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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

        // --- Hire Report Table ---
        TableView<EmployeeHireReport.HireEntry> hireTable = new TableView<>();
        hireTable.setPrefHeight(300);

        TableColumn<EmployeeHireReport.HireEntry, Integer> empIdCol = new TableColumn<>("Emp ID");
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<EmployeeHireReport.HireEntry, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<EmployeeHireReport.HireEntry, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<EmployeeHireReport.HireEntry, String> divisionCol = new TableColumn<>("Division");
        divisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

        TableColumn<EmployeeHireReport.HireEntry, String> jobTitleCol = new TableColumn<>("Job Title");
        jobTitleCol.setCellValueFactory(new PropertyValueFactory<>("jobTitleName"));

        TableColumn<EmployeeHireReport.HireEntry, LocalDate> hireDateCol = new TableColumn<>("Hire Date");
        hireDateCol.setCellValueFactory(new PropertyValueFactory<>("hireDate"));

        hireTable.getColumns().addAll(empIdCol, firstNameCol, lastNameCol, divisionCol, jobTitleCol, hireDateCol);

        HBox hireReportBox = new HBox(UIConstants.DEFAULT_SPACING);
        hireReportBox.setAlignment(Pos.CENTER_LEFT);
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
                    EmployeeHireReport report = reportService.getEmployeesHiredWithinDateRange(
                            session,
                            startDate.getDayOfMonth(), startDate.getMonthValue(), startDate.getYear(),
                            endDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getYear());

                    hireTable.setItems(FXCollections.observableArrayList(report.getHires()));
                } catch (Exception ex) {
                    DialogUtil.showError("Error generating hire report: " + ex.getMessage());
                }
            } else {
                DialogUtil.showError("Please select both start and end dates");
            }
        });
        hireReportBox.getChildren().addAll(hireLabel, startDatePicker, endDatePicker, hireReportBtn);

        // --- Job Title Monthly Pay Table ---
        TableView<JobTitleMonthlyPayReport> jobTitleTable = new TableView<>();
        jobTitleTable.setPrefHeight(200);

        TableColumn<JobTitleMonthlyPayReport, Integer> jtIdCol = new TableColumn<>("Job Title ID");
        jtIdCol.setCellValueFactory(new PropertyValueFactory<>("jobTitleId"));

        TableColumn<JobTitleMonthlyPayReport, String> jtNameCol = new TableColumn<>("Job Title Name");
        jtNameCol.setCellValueFactory(new PropertyValueFactory<>("jobTitleName"));

        TableColumn<JobTitleMonthlyPayReport, Integer> jtMonthCol = new TableColumn<>("Month");
        jtMonthCol.setCellValueFactory(new PropertyValueFactory<>("month"));

        TableColumn<JobTitleMonthlyPayReport, Integer> jtYearCol = new TableColumn<>("Year");
        jtYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<JobTitleMonthlyPayReport, Double> jtPayCol = new TableColumn<>("Total Pay");
        jtPayCol.setCellValueFactory(new PropertyValueFactory<>("totalPay"));

        jobTitleTable.getColumns().addAll(jtIdCol, jtNameCol, jtMonthCol, jtYearCol, jtPayCol);

        HBox jobTitleBox = new HBox(UIConstants.DEFAULT_SPACING);
        jobTitleBox.setAlignment(Pos.CENTER_LEFT);
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
                    JobTitleMonthlyPayReport report = reportService.getMonthlyPayByJobTitle(
                            session, jobTitleField.getText(), jtYearSpinner.getValue(), jtMonthSpinner.getValue());
                    jobTitleTable.setItems(FXCollections.observableArrayList(report));
                } catch (Exception ex) {
                    DialogUtil.showError("Error generating job title report: " + ex.getMessage());
                }
            } else {
                DialogUtil.showError("Please enter a job title");
            }
        });
        jobTitleBox.getChildren().addAll(jobLabel, jobTitleField, jtYearSpinner, jtMonthSpinner, jobReportBtn);

        // --- Division Monthly Pay Table ---
        TableView<DivisionMonthlyPayReport> divisionTable = new TableView<>();
        divisionTable.setPrefHeight(200);

        TableColumn<DivisionMonthlyPayReport, Integer> divIdCol = new TableColumn<>("Division ID");
        divIdCol.setCellValueFactory(new PropertyValueFactory<>("divisionId"));

        TableColumn<DivisionMonthlyPayReport, String> divNameCol = new TableColumn<>("Division Name");
        divNameCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

        TableColumn<DivisionMonthlyPayReport, Integer> divMonthCol = new TableColumn<>("Month");
        divMonthCol.setCellValueFactory(new PropertyValueFactory<>("month"));

        TableColumn<DivisionMonthlyPayReport, Integer> divYearCol = new TableColumn<>("Year");
        divYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<DivisionMonthlyPayReport, Double> divPayCol = new TableColumn<>("Total Pay");
        divPayCol.setCellValueFactory(new PropertyValueFactory<>("totalPay"));

        divisionTable.getColumns().addAll(divIdCol, divNameCol, divMonthCol, divYearCol, divPayCol);

        HBox divisionBox = new HBox(UIConstants.DEFAULT_SPACING);
        divisionBox.setAlignment(Pos.CENTER_LEFT);
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
                    DivisionMonthlyPayReport report = reportService.getMonthlyPayByDivision(
                            session, divisionField.getText(), divYearSpinner.getValue(), divMonthSpinner.getValue());

                    divisionTable.setItems(FXCollections.observableArrayList(report));
                } catch (Exception ex) {
                    DialogUtil.showError("Error generating division report: " + ex.getMessage());
                }
            } else {
                DialogUtil.showError("Please enter a division");
            }
        });
        divisionBox.getChildren().addAll(divLabel, divisionField, divYearSpinner, divMonthSpinner, divisionReportBtn);

        // Add all report sections to the options container
        reportOptions.getChildren().addAll(reportTitle, hireReportBox, hireTable,
                                           jobTitleBox, jobTitleTable,
                                           divisionBox, divisionTable);

        vbox.getChildren().addAll(title, reportOptions);

        return new Tab("Reports", vbox);
    }

    private boolean isAdmin(SessionContext session) {
        return session != null && session.getRole() != null
                && "HR_ADMIN".equalsIgnoreCase(session.getRole());
    }
}
