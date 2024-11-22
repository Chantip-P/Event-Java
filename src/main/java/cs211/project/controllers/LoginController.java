package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private VBox signUpVBox;
    @FXML private AnchorPane signUpBackground;
    private UserList users;

    @FXML
    public void initialize() {
        Datasource<UserList> datasource = new UserListFileDatasource("data", "user-list.json");
        users = datasource.readData();

        errorLabel.setText("");

        TransitionUtility.fadeInTransition(signUpBackground, 0.95, 1000);
        TransitionUtility.slideVerticalTransition(signUpVBox, -20, 1200);
    }

    @FXML
    public void onLogInClick() throws IOException {
        String usernameText = usernameTextField.getText();
        String passwordText = passwordField.getText();

        if (usernameText.isBlank()) {
            errorLabel.setText("Please enter username");
        } else if (passwordText.isEmpty()) {
            errorLabel.setText("Please enter password");
        } else if (!ProgramUtility.login(usernameText, passwordText)) {
            errorLabel.setText("Wrong username or password, please try again");
        } else {
            User loggedInUser = users.findUserByID(ProgramUtility.getLoggedInUserID());
            switch (loggedInUser.getType()) {
                case ADMIN -> { FXRouter.goTo("user-log");
                                SceneUtility.setCurrentTheme(loggedInUser.getUserDefaultTheme());}
                case USER -> { FXRouter.goTo("search-event","search");
                                SceneUtility.setCurrentTheme(loggedInUser.getUserDefaultTheme());}
                default -> throw new IllegalStateException("Invalid user type: " + loggedInUser.getType());
            }
        }
    }

    @FXML
    public void onLoginByPressEnter(KeyEvent event) throws IOException {
        if (event.getCode() != KeyCode.ENTER) { return; }

        onLogInClick();
        if (usernameTextField.isFocused() && !usernameTextField.getText().isBlank()) {
            errorLabel.setText("");
            passwordField.requestFocus();
        } else if (passwordField.isFocused() && usernameTextField.getText().isBlank()) {
            usernameTextField.requestFocus();
        }
    }

    @FXML
    public void onSignUpClick() throws IOException {
        FXRouter.goTo("sign-up-first", null);
    }
}
