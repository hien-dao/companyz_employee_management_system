package com.companyz.ems.ui.helper;

import java.time.LocalDate;
import java.util.Optional;

import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.EmployeeService;
import com.companyz.ems.utils.DialogUtil;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Helper dialog for creating a new Employee using a compact GridPane layout.
 */
public class EmployeeCreateDialog {

    private final EmployeeService employeeService;

    public EmployeeCreateDialog(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void show(SessionContext session, TableView<Employee> employeeTable) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("New Employee");
        dialog.setHeaderText("Enter employee details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Compact GridPane
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        // Row 0: First & Last Name
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        grid.add(new Label("First Name:"), 0, 0); grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 2, 0); grid.add(lastNameField, 3, 0);

        // Row 1: Gender & Race
        TextField genderField = new TextField();
        TextField raceField = new TextField();
        grid.add(new Label("Gender:"), 0, 1); grid.add(genderField, 1, 1);
        grid.add(new Label("Race:"), 2, 1); grid.add(raceField, 3, 1);

        // Row 2: DOB & SSN
        DatePicker dobPicker = new DatePicker();
        TextField ssnField = new TextField();
        grid.add(new Label("DOB:"), 0, 2); grid.add(dobPicker, 1, 2);
        grid.add(new Label("SSN:"), 2, 2); grid.add(ssnField, 3, 2);

        // Row 3: Email & Phone
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        grid.add(new Label("Email:"), 0, 3); grid.add(emailField, 1, 3);
        grid.add(new Label("Phone:"), 2, 3); grid.add(phoneField, 3, 3);

        // Row 4: Address Line 1 & 2
        TextField addr1Field = new TextField();
        TextField addr2Field = new TextField();
        grid.add(new Label("Address 1:"), 0, 4); grid.add(addr1Field, 1, 4);
        grid.add(new Label("Address 2:"), 2, 4); grid.add(addr2Field, 3, 4);

        // Row 5: City & State
        TextField cityField = new TextField();
        TextField stateField = new TextField();
        grid.add(new Label("City:"), 0, 5); grid.add(cityField, 1, 5);
        grid.add(new Label("State:"), 2, 5); grid.add(stateField, 3, 5);

        // Row 6: Country & Zip
        TextField countryField = new TextField();
        TextField zipField = new TextField();
        grid.add(new Label("Country:"), 0, 6); grid.add(countryField, 1, 6);
        grid.add(new Label("Zip:"), 2, 6); grid.add(zipField, 3, 6);

        // Row 7: Employment Type & Status
        TextField typeField = new TextField();
        TextField statusField = new TextField();
        grid.add(new Label("Type:"), 0, 7); grid.add(typeField, 1, 7);
        grid.add(new Label("Status:"), 2, 7); grid.add(statusField, 3, 7);

        // Row 8: Job Title & Division
        TextField jobTitleField = new TextField();
        TextField divisionField = new TextField();
        grid.add(new Label("Job Title:"), 0, 8); grid.add(jobTitleField, 1, 8);
        grid.add(new Label("Division:"), 2, 8); grid.add(divisionField, 3, 8);

        // Row 9: Salary & Hire Date
        TextField salaryField = new TextField();
        DatePicker hireDatePicker = new DatePicker();
        grid.add(new Label("Salary:"), 0, 9); grid.add(salaryField, 1, 9);
        grid.add(new Label("Hire Date:"), 2, 9); grid.add(hireDatePicker, 3, 9);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                try {
                    boolean success = employeeService.createEmployee(
                        session,
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        genderField.getText().trim(),
                        raceField.getText().trim(),
                        dobPicker.getValue(),
                        ssnField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        addr1Field.getText().trim(),
                        addr2Field.getText().trim(),
                        cityField.getText().trim(),
                        stateField.getText().trim(),
                        countryField.getText().trim(),
                        zipField.getText().trim(),
                        typeField.getText().trim(),
                        statusField.getText().trim(),
                        jobTitleField.getText().trim(),
                        divisionField.getText().trim(),
                        Double.parseDouble(salaryField.getText().trim()),
                        Optional.ofNullable(hireDatePicker.getValue()).orElse(LocalDate.now())
                    );

                    if (success) {
                        employeeTable.getItems().setAll(employeeService.getAllEmployees(session));
                        DialogUtil.showInfo("Employee created successfully!");
                    } else {
                        DialogUtil.showError("Failed to create employee.");
                    }
                } catch (Exception ex) {
                    DialogUtil.showError("Error creating employee: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
