package com.companyz.ems.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.UserService;
import com.companyz.ems.services.UserServiceImpl;
import com.companyz.ems.services.EmployeeService;
import com.companyz.ems.services.EmployeeServiceImpl;
import com.companyz.ems.services.ReportService;
import com.companyz.ems.services.ReportServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Main JavaFX GUI for Employee Management System.
 * Uses only features that are currently implemented in the DAO layer.
 */
public class EmployeeManagementSystemUI extends Application {
    private Stage primaryStage;
    private SessionContext currentSession;
    private String currentUsername; // Store username separately since SessionContext doesn't have getUsername()
    
    // Service instances
    private UserService userService;
    private EmployeeService employeeService;
    private ReportService reportService;
    
    private static final String APP_TITLE = "Employee Management System";
    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        
        // Initialize services
        userService = new UserServiceImpl();
        employeeService = new EmployeeServiceImpl();
        reportService = new ReportServiceImpl();
        
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        
        showLoginScreen();
        primaryStage.show();
    }

    /**
     * Display login screen
     */
    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label titleLabel = new Label(APP_TITLE);
        titleLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label subtitleLabel = new Label("User Authentication");
        subtitleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 20; -fx-background-color: white;");
        formBox.setMaxWidth(350);
        formBox.setAlignment(Pos.TOP_CENTER);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(280);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(280);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-padding: 10; -fx-font-size: 14; -fx-min-width: 120; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both username and password");
                return;
            }

            // Use UserService to authenticate
            Optional<SessionContext> sessionOpt = userService.authenticateUser(username, password);
            if (sessionOpt.isPresent()) {
                currentSession = sessionOpt.get();
                currentUsername = username;
                showDashboard();
            } else {
                errorLabel.setText("Invalid username or password");
                passwordField.clear();
            }
        });

        formBox.getChildren().addAll(
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                loginButton, errorLabel
        );

        root.getChildren().addAll(titleLabel, subtitleLabel, formBox);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    /**
     * Display main dashboard after successful login
     */
    private void showDashboard() {
        BorderPane root = new BorderPane();

        // Top menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Main content with tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                createDashboardTab(),
                createEmployeesTab(),
                createPayrollTab(),
                createReportsTab(),
                createUsersTab()
        );

        root.setCenter(tabPane);

        // Bottom status bar
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #e0e0e0;");
        
        String userInfo = "Logged in as: " + currentUsername + 
                         " | User ID: " + currentSession.getUserId() +
                         " | Role: " + currentSession.getRole();
        Label statusLabel = new Label(userInfo);
        statusBar.getChildren().add(statusLabel);
        root.setBottom(statusBar);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    /**
     * Create menu bar with File and Help menus
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> {
            currentSession = null;
            showLoginScreen();
        });
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        fileMenu.getItems().addAll(logoutItem, new SeparatorMenuItem(), exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAbout());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    /**
     * Create dashboard tab
     */
    private Tab createDashboardTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label statsTitle = new Label("System Statistics");
        statsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        List<Employee> allEmployees = employeeService.getAllEmployees(currentSession);
        Label empCountLabel = new Label("Total Employees: " + allEmployees.size());

        Label currentUserLabel = new Label("Current User: " + currentUsername);
        Label roleLabel = new Label("Role: " + currentSession.getRole());

        statsBox.getChildren().addAll(statsTitle, empCountLabel, currentUserLabel, roleLabel);
        vbox.getChildren().addAll(title, statsBox);

        Tab tab = new Tab("Dashboard", vbox);
        tab.setClosable(false);
        return tab;
    }

    /**
     * Create employees management tab
     */
    private Tab createEmployeesTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label title = new Label("Employee Management");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Search box
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 3; -fx-background-color: #fafafa;");

        TextField searchFirstName = new TextField();
        searchFirstName.setPromptText("First name");
        searchFirstName.setPrefWidth(120);

        TextField searchLastName = new TextField();
        searchLastName.setPromptText("Last name");
        searchLastName.setPrefWidth(120);

        Button searchButton = new Button("Search");
        Button viewAllButton = new Button("View All");

        searchBox.getChildren().addAll(
                new Label("Search:"), searchFirstName, searchLastName, searchButton, viewAllButton
        );

        // Employee table
        TableView<Employee> employeeTable = new TableView<>();
        TableColumn<Employee, Integer> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<Employee, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, LocalDate> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));

        @SuppressWarnings("unchecked")
        TableColumn<Employee, ?>[] columns = new TableColumn[] {idCol, firstNameCol, lastNameCol, dobCol};
        employeeTable.getColumns().addAll(columns);
        employeeTable.setPrefHeight(400);

        // Action buttons
        HBox actionBox = new HBox(10);
        actionBox.setPadding(new Insets(10));

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-padding: 8; -fx-text-fill: white; -fx-background-color: #f44336;");

        deleteButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this employee?");
                if (confirm.showAndWait().get() == ButtonType.OK) {
                    if (employeeService.deleteEmployee(currentSession, selected.getEmpId())) {
                        employeeTable.getItems().remove(selected);
                        showInfo("Employee deleted successfully");
                    } else {
                        showError("Failed to delete employee");
                    }
                }
            } else {
                showError("Please select an employee to delete");
            }
        });

        searchButton.setOnAction(e -> {
            String firstName = searchFirstName.getText().trim();
            String lastName = searchLastName.getText().trim();
            
            if (!firstName.isEmpty() && !lastName.isEmpty()) {
                Optional<Employee> result = employeeService.getEmployeeByName(currentSession, firstName, lastName);
                if (result.isPresent()) {
                    employeeTable.getItems().clear();
                    employeeTable.getItems().add(result.get());
                } else {
                    showError("Employee not found");
                }
            } else {
                showError("Please enter both first and last name");
            }
        });

        viewAllButton.setOnAction(e -> {
            employeeTable.getItems().clear();
            employeeTable.getItems().addAll(employeeService.getAllEmployees(currentSession));
        });

        actionBox.getChildren().addAll(deleteButton);

        vbox.getChildren().addAll(title, searchBox, employeeTable, actionBox);

        Tab tab = new Tab("Employees", vbox);
        tab.setClosable(false);
        return tab;
    }

    /**
     * Create payroll management tab
     */
    private Tab createPayrollTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label title = new Label("Payroll Management");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        VBox reportBox = new VBox(10);
        reportBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label reportTitle = new Label("Employee Payroll Report");
        reportTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Label reportContent = new Label("Click 'Generate Report' to view your payroll history");
        reportContent.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");

        Button generateButton = new Button("Generate Report");
        generateButton.setStyle("-fx-padding: 10; -fx-background-color: #2196F3; -fx-text-fill: white;");
        
        generateButton.setOnAction(e -> {
            try {
                var payrollReport = reportService.getEmployeePayrollHistory(currentSession);
                showInfo("Payroll Report:\n" + payrollReport.toString());
            } catch (Exception ex) {
                showError("Error generating payroll report: " + ex.getMessage());
            }
        });

        reportBox.getChildren().addAll(reportTitle, reportContent, generateButton);
        vbox.getChildren().addAll(title, reportBox);

        Tab tab = new Tab("Payroll", vbox);
        tab.setClosable(false);
        return tab;
    }

    /**
     * Create reports tab
     */
    private Tab createReportsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label title = new Label("Reports");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        VBox reportOptions = new VBox(10);
        reportOptions.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label reportTitle = new Label("Available Reports");
        reportTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Employee Hire Report
        HBox hireReportBox = new HBox(10);
        hireReportBox.setAlignment(Pos.CENTER_LEFT);
        Label hireLabel = new Label("Employee Hire Date Range Report:");
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        Button hireReportBtn = new Button("Generate");
        hireReportBtn.setStyle("-fx-padding: 8;");
        hireReportBtn.setOnAction(e -> {
            if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                try {
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();
                    var report = reportService.getEmployeesHiredWithinDateRange(currentSession, 
                            startDate.getDayOfMonth(), startDate.getMonthValue(), startDate.getYear(),
                            endDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getYear());
                    showInfo("Hire Report Generated:\n" + report.toString());
                } catch (Exception ex) {
                    showError("Error generating report: " + ex.getMessage());
                }
            } else {
                showError("Please select both start and end dates");
            }
        });
        hireReportBox.getChildren().addAll(hireLabel, startDatePicker, endDatePicker, hireReportBtn);

        // Job Title Report
        HBox jobTitleBox = new HBox(10);
        jobTitleBox.setAlignment(Pos.CENTER_LEFT);
        Label jobLabel = new Label("Job Title Monthly Report:");
        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Job Title");
        jobTitleField.setPrefWidth(100);
        Spinner<Integer> yearSpinner = new Spinner<>(2020, 2050, LocalDate.now().getYear());
        Spinner<Integer> monthSpinner = new Spinner<>(1, 12, LocalDate.now().getMonthValue());
        Button jobReportBtn = new Button("Generate");
        jobReportBtn.setStyle("-fx-padding: 8;");
        jobReportBtn.setOnAction(e -> {
            if (!jobTitleField.getText().isEmpty()) {
                try {
                    var report = reportService.getMonthlyPayByJobTitle(currentSession, 
                            jobTitleField.getText(), yearSpinner.getValue(), monthSpinner.getValue());
                    showInfo("Job Title Report Generated:\n" + report.toString());
                } catch (Exception ex) {
                    showError("Error generating report: " + ex.getMessage());
                }
            } else {
                showError("Please enter a job title");
            }
        });
        jobTitleBox.getChildren().addAll(jobLabel, jobTitleField, yearSpinner, monthSpinner, jobReportBtn);

        reportOptions.getChildren().addAll(reportTitle, hireReportBox, jobTitleBox);
        vbox.getChildren().addAll(title, reportOptions);

        Tab tab = new Tab("Reports", vbox);
        tab.setClosable(false);
        return tab;
    }

    /**
     * Create users management tab
     */
    private Tab createUsersTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label title = new Label("User Management");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Note about role management
        VBox rolesBox = new VBox(10);
        rolesBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label rolesTitle = new Label("Role Information");
        rolesTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Label rolesNote = new Label("Role management is handled through the UserService. Available roles are assigned during user creation.");
        rolesBox.getChildren().addAll(rolesTitle, rolesNote);

        // Current user info
        VBox userInfoBox = new VBox(10);
        userInfoBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label userInfoTitle = new Label("Current User Info");
        userInfoTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        String userIdText = "User ID: " + (currentSession != null ? currentSession.getUserId() : "N/A");
        String usernameText = "Username: " + (currentUsername != null ? currentUsername : "N/A");
        String rolesText = "Role: " + (currentSession != null ? currentSession.getRole() : "N/A");

        Label userIdLabel = new Label(userIdText);
        Label usernameLabel = new Label(usernameText);
        Label rolesLabel = new Label(rolesText);

        userInfoBox.getChildren().addAll(userInfoTitle, userIdLabel, usernameLabel, rolesLabel);

        vbox.getChildren().addAll(title, rolesBox, userInfoBox);

        Tab tab = new Tab("Users", vbox);
        tab.setClosable(false);
        return tab;
    }

    /**
     * Show about dialog
     */
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(APP_TITLE);
        alert.setContentText("Employee Management System v1.0\n\nA comprehensive system for managing employees, payroll, and HR operations.");
        alert.showAndWait();
    }

    /**
     * Show info dialog
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show error dialog
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
