package com.companyz.ems.ui;

import java.util.Optional;
import java.util.function.Consumer;

import com.companyz.ems.security.SessionContext;
import com.companyz.ems.services.UserService;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginUI {
    private final UserService userService;

    public LoginUI(UserService userService) {
        this.userService = userService;
    }

    public Scene build(Stage stage, Consumer<SessionContext> onLoginSuccess) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Employee Management System Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(150);   // keep compact

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(150);   // keep compact

        Label errorLabel = new Label();
        Button loginButton = new Button("Login");

        loginButton.setOnAction(e -> {
            Optional<SessionContext> sessionOpt = userService.authenticateUser(
                usernameField.getText(), passwordField.getText()
            );
            if (sessionOpt.isPresent()) {
                onLoginSuccess.accept(sessionOpt.get());
            } else {
                errorLabel.setText("Invalid username or password");
            }
        });

        VBox formBox = new VBox(10);
        formBox.setAlignment(Pos.CENTER);
        formBox.getChildren().addAll(usernameField, passwordField, loginButton, errorLabel);

        root.getChildren().addAll(title, formBox);

        return new Scene(root, 400, 300);
    }
}
