package com.companyz.ems.ui;

import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.EmployeeService;
import com.companyz.ems.services.EmployeeServiceImpl;
import com.companyz.ems.services.ReportService;
import com.companyz.ems.services.ReportServiceImpl;
import com.companyz.ems.services.UserService;
import com.companyz.ems.services.UserServiceImpl;
import com.companyz.ems.utils.DialogUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * Main JavaFX Application class for the Employee Management System.
 * Orchestrates login, dashboard, and logout functionality.
 */
public class EmployeeManagementSystemUI extends Application {
    private Stage primaryStage;
    private SessionContext currentSession;

    private UserService userService;
    private EmployeeService employeeService;
    private ReportService reportService;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        userService = new UserServiceImpl();
        employeeService = new EmployeeServiceImpl();
        reportService = new ReportServiceImpl();

        primaryStage.setTitle("Employee Management System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);

        showLoginScreen();
        primaryStage.show();
    }

    private void showLoginScreen() {
        LoginUI loginUI = new LoginUI(userService);
        Scene loginScene = loginUI.build(primaryStage, session -> {
            currentSession = session;
            showDashboard();
        });
        primaryStage.setScene(loginScene);
    }

    private void showDashboard() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Always show Dashboard (with logout callback)
        tabPane.getTabs().add(new DashboardUI(employeeService).build(currentSession, this::handleLogout));

        // General employee tabs
        if (isEmployee()) {
            tabPane.getTabs().add(new ProfileUI(employeeService).build(currentSession));
            tabPane.getTabs().add(new PayrollUI(reportService).build(currentSession));
        }

        // HR Admin tabs
        if (isAdmin()) {
            tabPane.getTabs().add(new EmployeesUI(employeeService).build(currentSession));
            tabPane.getTabs().add(new ReportsUI(reportService).build(currentSession));
        }

        primaryStage.setScene(new Scene(tabPane, 1200, 800));
    }

    /**
     * Clears the current session and returns to the login screen.
     */
    private void handleLogout() {
        if (currentSession != null) {
            currentSession.inValidated(); // mark session inactive
            currentSession = null;
        }
        DialogUtil.showInfo("You have been logged out.");
        showLoginScreen();
    }

    private boolean isAdmin() {
        return currentSession != null && currentSession.getRole() != null
                && "HR_ADMIN".equalsIgnoreCase(currentSession.getRole());
    }

    private boolean isEmployee() {
        return currentSession != null && currentSession.getRole() != null
                && !"HR_ADMIN".equalsIgnoreCase(currentSession.getRole());
    }

    @Override
    public void stop() {
        // Clean up resources if needed
        DialogUtil.showInfo("Application closed.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
