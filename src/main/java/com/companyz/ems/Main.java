package com.companyz.ems;

import com.companyz.ems.ui.EmployeeManagementSystemUI;

import javafx.application.Application;

/**
 * Entry point for the Employee Management System.
 * Delegates to the JavaFX Application subclass EmployeeManagementSystemUI.
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(EmployeeManagementSystemUI.class, args);
    }
}

