package com.companyz.ems.ui;

import java.util.Optional;

import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.EmployeeService;
import com.companyz.ems.utils.UIConstants;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

/**
 * Profile tab for general employees.
 * Displays the logged-in employee's personal information in view-only mode.
 * Uses EmployeeService.getSelfEmployeeInfo to enforce security.
 */
public class ProfileUI {
    private final EmployeeService employeeService;

    public ProfileUI(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Build the profile tab for the current session.
     *
     * @param session current user session
     * @return a Tab showing employee profile info
     */
    public Tab build(SessionContext session) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(15));

        Label title = new Label("My Profile (View Only)");
        title.setStyle(UIConstants.TITLE_STYLE);

        VBox infoBox = new VBox(8);
        infoBox.setStyle(UIConstants.BORDER_STYLE);

        try {
            Optional<Employee> empOpt = employeeService.getSelfEmployeeInfo(session);
            if (empOpt.isPresent()) {
                Employee emp = empOpt.get();
                infoBox.getChildren().addAll(
                    new Label("Employee ID: " + emp.getEmpId()),
                    new Label("Name: " + emp.getFirstName() + " " + emp.getLastName()),
                    new Label("Gender: " + emp.getGender()),
                    new Label("Race: " + emp.getRace()),
                    new Label("Date of Birth: " + emp.getDob()),
                    new Label("SSN (Last 4): " + emp.getSsnLast4()),
                    new Label("Email: " + emp.getPrimaryEmail()),
                    new Label("Phone: " + emp.getPrimaryPhoneNumber()),
                    new Label("Address: " + emp.getAddressLine1() + 
                              (emp.getAddressLine2() != null ? ", " + emp.getAddressLine2() : "")),
                    new Label("City: " + emp.getCity()),
                    new Label("State: " + emp.getState()),
                    new Label("Country: " + emp.getCountry()),
                    new Label("Zip Code: " + emp.getZipCode()),
                    new Label("Employment Type: " + emp.getEmploymentTypeString()),
                    new Label("Employment Status: " + emp.getEmploymentStatusString()),
                    new Label("Job Title: " + emp.getJobTitleString()),
                    new Label("Division: " + emp.getDivisionString()),
                    new Label("Salary: $" + emp.getSalary()),
                    new Label("Hire Date: " + emp.getHireDate()),
                    new Label("Created At: " + emp.getCreatedAt()),
                    new Label("Updated At: " + emp.getUpdatedAt())
                );
            } else {
                infoBox.getChildren().add(new Label("Employee record not found."));
            }
        } catch (Exception ex) {
            Label error = new Label("Error loading profile: " + ex.getMessage());
            error.setStyle(UIConstants.ERROR_STYLE);
            infoBox.getChildren().add(error);
        }

        vbox.getChildren().addAll(title, infoBox);
        return new Tab("Profile", vbox);
    }
}
