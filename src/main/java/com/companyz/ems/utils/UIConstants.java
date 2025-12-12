package com.companyz.ems.utils;

/**
 * Centralized UI constants for styling and layout.
 */
public final class UIConstants {
    private UIConstants() {
        // prevent instantiation
    }

    // Common padding and spacing
    public static final int DEFAULT_PADDING = 15;
    public static final int DEFAULT_SPACING = 10;

    // Styles
    public static final String BORDER_STYLE =
        "-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #fafafa;";
    public static final String TITLE_STYLE =
        "-fx-font-size: 18; -fx-font-weight: bold;";
    public static final String SUBTITLE_STYLE =
        "-fx-font-size: 14; -fx-font-weight: bold;";
    public static final String ERROR_STYLE =
        "-fx-text-fill: red;";
    public static final String INFO_STYLE =
        "-fx-font-size: 12; -fx-text-fill: #666;";
    public static final String BUTTON_PRIMARY_STYLE =
        "-fx-padding: 10; -fx-background-color: #2196F3; -fx-text-fill: white;";
    public static final String BUTTON_DANGER_STYLE =
        "-fx-padding: 8; -fx-text-fill: white; -fx-background-color: #f44336;";
    public static final String BUTTON_SUCCESS_STYLE =
        "-fx-padding: 10; -fx-font-size: 14; -fx-min-width: 120; -fx-background-color: #4CAF50; -fx-text-fill: white;";
}
