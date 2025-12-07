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

import com.companyz.ems.dao.*;
import com.companyz.ems.model.User;
import com.companyz.ems.model.Role;
import com.companyz.ems.model.employee.BaseEmployee;
import com.companyz.ems.model.Payroll;
import com.companyz.ems.security.PasswordHasher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Main JavaFX GUI for Employee Management System.
 * Uses only features that are currently implemented in the DAO layer.
 */
public class EmployeeManagementSystemUI extends Application {
    private Stage primaryStage;
    private User currentUser;
    
    // DAO instances
    private UserDao userDao;
    private EmployeeDao employeeDao;
    private PayrollDao payrollDao;
    private RoleDao roleDao;
    private DivisionDao divisionDao;
    private JobTitleDao jobTitleDao;
    
    private static final String APP_TITLE = "Employee Management System";
    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        
        // Initialize DAOs
        userDao = new UserDaoImpl();
        employeeDao = new EmployeeDaoImpl();
        payrollDao = new PayrollDaoImpl();
        roleDao = new RoleDaoImpl();
        divisionDao = new DivisionDaoImpl();
        jobTitleDao = new JobTitleDaoImpl();
        
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

            Optional<User> optionalUser = userDao.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                
                // Verify password using PasswordHasher
                PasswordHasher hasher = new PasswordHasher(12);
                if (hasher.verify(password.toCharArray(), user.getPasswordHash(), user.getPasswordSalt())) {
                    currentUser = user;
                    showDashboard();
                } else {
                    errorLabel.setText("Invalid username or password");
                    passwordField.clear();
                }
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
        
        List<Role> userRoles = userDao.getUserRoles(currentUser.getUserId());
        String rolesStr = userRoles.isEmpty() ? "User" : 
                         userRoles.stream().map(Role::getRoleName).reduce((a,b) -> a + ", " + b).orElse("User");
        
        Label statusLabel = new Label("Logged in as: " + currentUser.getUsername() + " | Roles: " + rolesStr);
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
        logoutItem.setOnAction(e -> showLoginScreen());
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

        List<BaseEmployee> allEmployees = employeeDao.findAll();
        Label empCountLabel = new Label("Total Employees: " + allEmployees.size());

        List<Payroll> allPayroll = payrollDao.findAll();
        Label payrollCountLabel = new Label("Total Payroll Records: " + allPayroll.size());

        Label currentUserLabel = new Label("Current User: " + currentUser.getUsername());

        statsBox.getChildren().addAll(statsTitle, empCountLabel, payrollCountLabel, currentUserLabel);
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
        TableView<BaseEmployee> employeeTable = new TableView<>();
        TableColumn<BaseEmployee, Integer> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<BaseEmployee, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<BaseEmployee, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<BaseEmployee, LocalDate> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));

        employeeTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, dobCol);
        employeeTable.setPrefHeight(400);

        // Action buttons
        HBox actionBox = new HBox(10);
        actionBox.setPadding(new Insets(10));

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-padding: 8; -fx-text-fill: white; -fx-background-color: #f44336;");

        deleteButton.setOnAction(e -> {
            BaseEmployee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this employee?");
                if (confirm.showAndWait().get() == ButtonType.OK) {
                    if (employeeDao.deleteEmployee(selected.getEmpId())) {
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
                Optional<BaseEmployee> result = employeeDao.findByName(firstName, lastName);
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
            employeeTable.getItems().addAll(employeeDao.findAll());
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

        // Search payroll by employee
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 3; -fx-background-color: #fafafa;");

        TextField empIdField = new TextField();
        empIdField.setPromptText("Employee ID");
        empIdField.setPrefWidth(120);

        Button searchButton = new Button("Search");
        Button viewAllButton = new Button("View All");

        searchBox.getChildren().addAll(new Label("Search by Employee:"), empIdField, searchButton, viewAllButton);

        // Payroll table
        TableView<Payroll> payrollTable = new TableView<>();
        TableColumn<Payroll, Integer> payIdCol = new TableColumn<>("Pay ID");
        payIdCol.setCellValueFactory(new PropertyValueFactory<>("payId"));

        TableColumn<Payroll, Integer> empIdCol = new TableColumn<>("Employee ID");
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<Payroll, LocalDate> payDateCol = new TableColumn<>("Pay Date");
        payDateCol.setCellValueFactory(new PropertyValueFactory<>("payDate"));

        TableColumn<Payroll, Double> grossPayCol = new TableColumn<>("Gross Pay");
        grossPayCol.setCellValueFactory(new PropertyValueFactory<>("grossPay"));

        TableColumn<Payroll, Double> deductionsCol = new TableColumn<>("Deductions");
        deductionsCol.setCellValueFactory(new PropertyValueFactory<>("deductions"));

        TableColumn<Payroll, Double> netPayCol = new TableColumn<>("Net Pay");
        netPayCol.setCellValueFactory(new PropertyValueFactory<>("netPay"));

        payrollTable.getColumns().addAll(payIdCol, empIdCol, payDateCol, grossPayCol, deductionsCol, netPayCol);
        payrollTable.setPrefHeight(400);

        searchButton.setOnAction(e -> {
            try {
                int empId = Integer.parseInt(empIdField.getText());
                Optional<BaseEmployee> empOpt = employeeDao.findById(empId);
                if (empOpt.isPresent()) {
                    payrollTable.getItems().clear();
                    payrollTable.getItems().addAll(payrollDao.findAll().stream()
                            .filter(p -> p.getEmpId() == empId)
                            .toList());
                } else {
                    showError("Employee not found");
                }
            } catch (NumberFormatException ex) {
                showError("Please enter a valid Employee ID");
            }
        });

        viewAllButton.setOnAction(e -> {
            payrollTable.getItems().clear();
            payrollTable.getItems().addAll(payrollDao.findAll());
        });

        vbox.getChildren().addAll(title, searchBox, payrollTable);

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
                var report = employeeDao.getEmployeeHireByDateRange(startDatePicker.getValue(), endDatePicker.getValue());
                showInfo("Hire Report Generated:\n" + report.toString());
            } else {
                showError("Please select both start and end dates");
            }
        });
        hireReportBox.getChildren().addAll(hireLabel, startDatePicker, endDatePicker, hireReportBtn);

        // Employee Payroll Report
        HBox empPayrollBox = new HBox(10);
        empPayrollBox.setAlignment(Pos.CENTER_LEFT);
        Label empPayrollLabel = new Label("Employee Payroll Report:");
        TextField empPayrollIdField = new TextField();
        empPayrollIdField.setPromptText("Employee ID");
        empPayrollIdField.setPrefWidth(100);
        Button empPayrollBtn = new Button("Generate");
        empPayrollBtn.setStyle("-fx-padding: 8;");
        empPayrollBtn.setOnAction(e -> {
            try {
                int empId = Integer.parseInt(empPayrollIdField.getText());
                var report = payrollDao.getPayrollsByEmployee(empId);
                showInfo("Payroll Report Generated:\n" + report.toString());
            } catch (NumberFormatException ex) {
                showError("Please enter a valid Employee ID");
            }
        });
        empPayrollBox.getChildren().addAll(empPayrollLabel, empPayrollIdField, empPayrollBtn);

        reportOptions.getChildren().addAll(reportTitle, hireReportBox, empPayrollBox);
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

        // View all roles
        VBox rolesBox = new VBox(10);
        rolesBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label rolesTitle = new Label("Available Roles");
        rolesTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        ListView<Role> rolesList = new ListView<>();
        List<Role> allRoles = roleDao.findAll();
        rolesList.getItems().addAll(allRoles);
        rolesList.setPrefHeight(200);

        rolesBox.getChildren().addAll(rolesTitle, rolesList);

        // Current user info
        VBox userInfoBox = new VBox(10);
        userInfoBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label userInfoTitle = new Label("Current User Info");
        userInfoTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        List<Role> currentUserRoles = userDao.getUserRoles(currentUser.getUserId());
        String rolesList_text = currentUserRoles.isEmpty() ? "No roles assigned" : 
                              currentUserRoles.stream().map(Role::getRoleName).reduce((a,b) -> a + ", " + b).orElse("No roles");

        Label userIdLabel = new Label("User ID: " + currentUser.getUserId());
        Label usernameLabel = new Label("Username: " + currentUser.getUsername());
        Label rolesLabel = new Label("Roles: " + rolesList_text);
        Label activeLabel = new Label("Active: " + (currentUser.isActive() ? "Yes" : "No"));

        userInfoBox.getChildren().addAll(userInfoTitle, userIdLabel, usernameLabel, rolesLabel, activeLabel);

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
