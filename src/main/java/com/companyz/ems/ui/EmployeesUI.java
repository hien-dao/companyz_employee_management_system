package com.companyz.ems.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.EmployeeService;
import com.companyz.ems.utils.DialogUtil;
import com.companyz.ems.utils.UIConstants;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Employees tab for HR Admin.
 * Provides search, view, delete, and salary increase functionality.
 */
public class EmployeesUI {
    private final EmployeeService employeeService;

    public EmployeesUI(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Build the Employees tab for HR Admin.
     *
     * @param session current user session
     * @return a Tab with employee management features
     */
    public Tab build(SessionContext session) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(15));

        Label title = new Label("Employee Management (HR Admin)");
        title.setStyle(UIConstants.TITLE_STYLE);

        if (!isAdmin(session)) {
            VBox infoBox = new VBox(10);
            infoBox.setPadding(new javafx.geometry.Insets(10));
            infoBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; "
                    + "-fx-padding: 15; -fx-background-color: #fafafa;");
            infoBox.getChildren().add(new Label("You do not have permission to view this section."));
            vbox.getChildren().addAll(title, infoBox);
            return new Tab("Employees", vbox);
        }

        // Search controls
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new javafx.geometry.Insets(10));
        searchBox.setStyle(UIConstants.BORDER_STYLE);

        TextField firstNameField = new TextField(); firstNameField.setPromptText("First name");
        TextField lastNameField = new TextField(); lastNameField.setPromptText("Last name");
        DatePicker dobPicker = new DatePicker(); dobPicker.setPromptText("DOB");
        TextField ssnField = new TextField(); ssnField.setPromptText("SSN last 4");
        TextField empIdField = new TextField(); empIdField.setPromptText("Emp ID");
        Button searchButton = new Button("Search");
        Button viewAllButton = new Button("View All");

        searchBox.getChildren().addAll(
            new Label("Search:"), firstNameField, lastNameField, dobPicker, ssnField, empIdField, searchButton, viewAllButton
        );

        // Employee table
        TableView<Employee> employeeTable = new TableView<>();
        TableColumn<Employee, Integer> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("empId"));
        TableColumn<Employee, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Employee, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Employee, LocalDate> dobCol = new TableColumn<>("DOB");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        employeeTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, dobCol);
        employeeTable.setPrefHeight(400);

        // Action buttons
        HBox actionBox = new HBox(10);
        actionBox.setPadding(new javafx.geometry.Insets(10));

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle(UIConstants.BUTTON_DANGER_STYLE);

        Button updateSalaryButton = new Button("Increase Salary % (Range)");
        updateSalaryButton.setStyle(UIConstants.BUTTON_PRIMARY_STYLE);

        deleteButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this employee?");
                if (confirm.showAndWait().get() == ButtonType.OK) {
                    if (employeeService.deleteEmployee(session, selected.getEmpId())) {
                        employeeTable.getItems().remove(selected);
                        DialogUtil.showInfo("Employee deleted successfully");
                    } else {
                        DialogUtil.showError("Failed to delete employee");
                    }
                }
            } else {
                DialogUtil.showError("Please select an employee to delete");
            }
        });

        updateSalaryButton.setOnAction(e -> {
            TextInputDialog percentDialog = new TextInputDialog("3.2");
            percentDialog.setHeaderText("Enter percentage increase (e.g., 3.2)");
            Optional<String> pctOpt = percentDialog.showAndWait();

            TextInputDialog minDialog = new TextInputDialog("58000");
            minDialog.setHeaderText("Minimum salary (>=)");
            Optional<String> minOpt = minDialog.showAndWait();

            TextInputDialog maxDialog = new TextInputDialog("105000");
            maxDialog.setHeaderText("Maximum salary (<)");
            Optional<String> maxOpt = maxDialog.showAndWait();

            if (pctOpt.isPresent() && minOpt.isPresent() && maxOpt.isPresent()) {
                try {
                    double pct = Double.parseDouble(pctOpt.get());
                    double min = Double.parseDouble(minOpt.get());
                    double max = Double.parseDouble(maxOpt.get());
                    int updated = employeeService.increaseSalaryByRange(session, pct, min, max, "Admin adjustment");
                    if (updated > 0) DialogUtil.showInfo("Salary updates applied to " + updated + " employees.");
                    else DialogUtil.showError("No salaries updated.");
                } catch (NumberFormatException nfe) {
                    DialogUtil.showError("Invalid numeric input.");
                } catch (Exception ex) {
                    DialogUtil.showError("Error updating salaries: " + ex.getMessage());
                }
            }
        });

        searchButton.setOnAction(e -> {
            try {
                if (!empIdField.getText().trim().isEmpty()) {
                    int id = Integer.parseInt(empIdField.getText().trim());
                    Optional<Employee> res = employeeService.searchByEmpId(session, id);
                    employeeTable.getItems().setAll(res.map(List::of).orElseGet(List::of));
                    return;
                }
                if (!ssnField.getText().trim().isEmpty()) {
                    Optional<Employee> res = employeeService.searchBySsn(session, ssnField.getText().trim());
                    employeeTable.getItems().setAll(res.map(List::of).orElseGet(List::of));
                    return;
                }
                if (dobPicker.getValue() != null) {
                    List<Employee> results = employeeService.searchByDob(session, dobPicker.getValue());
                    employeeTable.getItems().setAll(results);
                    return;
                }
                if (!firstNameField.getText().trim().isEmpty() && !lastNameField.getText().trim().isEmpty()) {
                    Optional<Employee> res = employeeService.getEmployeeByName(session,
                            firstNameField.getText().trim(), lastNameField.getText().trim());
                    employeeTable.getItems().setAll(res.map(List::of).orElseGet(List::of));
                    return;
                }
                DialogUtil.showError("Please enter search criteria.");
            } catch (Exception ex) {
                DialogUtil.showError("Search error: " + ex.getMessage());
            }
        });

        viewAllButton.setOnAction(e -> {
            try {
                employeeTable.getItems().setAll(employeeService.getAllEmployees(session));
            } catch (Exception ex) {
                DialogUtil.showError("Error loading employees: " + ex.getMessage());
            }
        });

        actionBox.getChildren().addAll(deleteButton, updateSalaryButton);
        vbox.getChildren().addAll(title, searchBox, employeeTable, actionBox);

        return new Tab("Employees", vbox);
    }

    private boolean isAdmin(SessionContext session) {
        return session != null && session.getRole() != null
                && "HR_ADMIN".equalsIgnoreCase(session.getRole());
    }
}
