package com.companyz.ems.utils;

import javafx.scene.control.Alert;

/**
 * Utility class for showing common dialogs in the Employee Management System UI.
 */
public final class DialogUtil {

    private static final String APP_TITLE = "Employee Management System";

    private DialogUtil() {
        // prevent instantiation
    }

    /**
     * Show an informational dialog.
     *
     * @param message the message to display
     */
    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(APP_TITLE);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show an error dialog.
     *
     * @param message the error message to display
     */
    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(APP_TITLE);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show the About dialog.
     */
    public static void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(APP_TITLE);
        alert.setContentText("Employee Management System v1.0\n\n"
                + "A comprehensive system for managing employees, payroll, and HR operations.");
        alert.showAndWait();
    }
}
