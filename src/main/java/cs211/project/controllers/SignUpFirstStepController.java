package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;

public class SignUpFirstStepController {
    @FXML private TextField usernameTextField;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private Label errorUsernameLabel;
    @FXML private Label errorFirstNameLabel;
    @FXML private Label errorLastNameLabel;
    @FXML private AnchorPane signUpBackground;
    @FXML private VBox signUpVBox;
    private User tempUser;
    private UserList users;
    private boolean alreadySignUpFirstStep = false;

    @FXML
    public void initialize() {
        Datasource<UserList> datasource = new UserListFileDatasource("data", "user-list.json");
        users = datasource.readData();

        String tempData = (String) FXRouter.getData();
        if (tempData != null) {
            tempUser = ProgramUtility.createUserFromTempUserCSV(tempData);
            showUserDetails(tempUser);
            alreadySignUpFirstStep = true;
        }

        errorUsernameLabel.setText("");
        errorFirstNameLabel.setText("");
        errorLastNameLabel.setText("");

        TransitionUtility.slideHorizontalTransition(signUpBackground,-1000,800);
        TransitionUtility.fadeInTransition(signUpVBox, 0.95, 1000);
    }

    private void showUserDetails(User user) {
        usernameTextField.setText(user.getUsername());
        firstNameTextField.setText(user.getFirstName());
        lastNameTextField.setText(user.getLastName());
    }

    @FXML
    private void onBackToLogInClick() throws IOException {
        String destDirPath = "data/images/profiles";
        if (tempUser != null &&
                CalculationUtility.findFile(destDirPath, tempUser.getUsername()) != null) {
            Files.delete(CalculationUtility.findFile(destDirPath, tempUser.getUsername()).toPath());
        }
        FXRouter.goTo("login");
    }

    @FXML
    private void onNextClick() throws IOException {
        String usernameText = usernameTextField.getText();
        String firstNameText = firstNameTextField.getText();
        String lastNameText = lastNameTextField.getText();

        errorUsernameLabel.setText("");
        errorFirstNameLabel.setText("");
        errorLastNameLabel.setText("");

        boolean checkDuplicateUsername = users.findUserByUsername(usernameText) != null;

        if (usernameText.isBlank()) {
            errorUsernameLabel.setText("Please enter username");
            return;
        } else if (checkDuplicateUsername) {
            errorUsernameLabel.setText("This username has been taken, please try again");
            return;
        } else {
            errorUsernameLabel.setText("");
        }

        if (firstNameText.isBlank()) {
            errorFirstNameLabel.setText("Please enter your first name");
            return;
        } else {
            errorFirstNameLabel.setText("");
        }

        if (lastNameText.isBlank()) {
            errorLastNameLabel.setText("Please enter your last name");
            return;
        } else {
            errorLastNameLabel.setText("");
        }

        if (alreadySignUpFirstStep) {
            tempUser.setUsername(usernameText);
            tempUser.setFirstName(firstNameText);
            tempUser.setLastName(lastNameText);
        } else {
            tempUser = new User(usernameText, firstNameText, lastNameText);
        }

        FXRouter.goTo("sign-up-last", ProgramUtility.tempUserCSV(tempUser));
    }

    @FXML
    public void onNextByPressEnter(KeyEvent event) throws IOException {
        if (event.getCode() != KeyCode.ENTER) { return; }

        onNextClick();
        if (usernameTextField.isFocused() && errorUsernameLabel.getText().isBlank()) {
            errorFirstNameLabel.setText("");
            errorLastNameLabel.setText("");
            firstNameTextField.requestFocus();
        } else if (firstNameTextField.isFocused() && errorUsernameLabel.getText().isBlank() &&
                errorFirstNameLabel.getText().isBlank()) {
            errorLastNameLabel.setText("");
            lastNameTextField.requestFocus();
        } else if (!errorUsernameLabel.getText().isBlank()) {
            usernameTextField.requestFocus();
        }
    }

}
