package com.companyz.ems.ui;

import java.util.List;

import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.EmployeeService;
import com.companyz.ems.utils.UIConstants;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

/**
 * Dashboard tab for Employee Management System.
 * - HR Admin: shows system statistics (total employees).
 * - General Employee: shows welcome message and role info.
 * - Provides a Logout button (delegates to parent callback).
 */
public class DashboardUI {
    private final EmployeeService employeeService;

    public DashboardUI(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Build the dashboard tab based on the current session role.
     *
     * @param session current user session
     * @param logoutCallback callback to invoke when logout is requested
     * @return a Tab for the dashboard
     */
    public Tab build(SessionContext session, Runnable logoutCallback) {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));

        Label title = new Label("Dashboard");
        title.setStyle(UIConstants.TITLE_STYLE);

        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label currentUserLabel = new Label("Current User: " + session.getUserId());
        Label roleLabel = new Label("Role: " + session.getRole());

        if (isAdmin(session)) {
            Label statsTitle = new Label("System Statistics");
            statsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
            try {
                List<Employee> allEmployees = employeeService.getAllEmployees(session);
                Label empCountLabel = new Label("Total Employees: " + allEmployees.size());
                box.getChildren().addAll(statsTitle, empCountLabel, currentUserLabel, roleLabel);
            } catch (Exception ex) {
                Label error = new Label("Error loading admin stats: " + ex.getMessage());
                error.setStyle("-fx-text-fill: red;");
                box.getChildren().addAll(statsTitle, error, currentUserLabel, roleLabel);
            }
        } else {
            Label welcome = new Label("Welcome! Use 'Profile' to view your data and 'Payroll' to see your pay history.");
            welcome.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
            box.getChildren().addAll(welcome, currentUserLabel, roleLabel);
        }

        // Add logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> {
            if (logoutCallback != null) {
                logoutCallback.run();
            }
        });

        vbox.getChildren().addAll(title, box, logoutButton);
        return new Tab("Dashboard", vbox);
    }

    private boolean isAdmin(SessionContext session) {
        return session != null && session.getRole() != null
                && "HR_ADMIN".equalsIgnoreCase(session.getRole());
    }
}
