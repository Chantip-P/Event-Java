package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SignUpLastStepController {
    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label usernameLabel;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorPasswordLabel;
    @FXML private Label errorConfirmPasswordLabel;
    @FXML private Circle profileCircle;
    @FXML private Circle iconCameraCircle;
    @FXML private AnchorPane signUpBackground;
    @FXML private VBox signUpVBox;
    private User newUser;
    private UserList users;
    private Datasource<UserList> datasource;

    @FXML
    public void initialize() {
        datasource = new UserListFileDatasource("data", "user-list.json");
        users = datasource.readData();

        String tempData = (String) FXRouter.getData();
        newUser = ProgramUtility.createUserFromTempUserCSV(tempData);
        showUserDetails(newUser);

        Image profilePicture = new Image(newUser.getProfileImagePath(true));
        profileCircle.setFill(new ImagePattern(profilePicture));

        Image cameraIcon = new Image(getClass().getResourceAsStream("/images/icon-camera.png"));
        iconCameraCircle.setFill(new ImagePattern(cameraIcon));

        errorPasswordLabel.setText("");
        errorConfirmPasswordLabel.setText("");

        TransitionUtility.slideHorizontalTransition(signUpBackground, -1000, 800);
        TransitionUtility.fadeInTransition(signUpVBox, 0.95, 1000);
    }

    private void showUserDetails(User user) {
        firstNameLabel.setText(user.getFirstName());
        lastNameLabel.setText(user.getLastName());
        usernameLabel.setText(user.getUsername());
    }

    @FXML
    public void OnBrowsePictureClick(ActionEvent event) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("images PNG JPG", "*.png", "*.jpg", "*.jpeg"));
        Node source = (Node) event.getSource();
        File file = chooser.showOpenDialog(source.getScene().getWindow());
        if (file != null) {
            String destDirPath = "data/images/profiles";
            if (CalculationUtility.findFile(destDirPath, newUser.getUsername()) != null) {
                Files.delete(CalculationUtility.findFile(destDirPath, newUser.getUsername()).toPath());
            }
            File target = CalculationUtility.copyExternalFile(file, destDirPath, newUser.getUsername());

            profileCircle.setFill(new ImagePattern(new Image(target.toPath().toUri().toString())));
            newUser.setProfileImagePath(destDirPath + "/" + target.getName());
        }
    }

    @FXML
    public void onBackClick() throws IOException {
        FXRouter.goTo("sign-up-first", ProgramUtility.tempUserCSV(newUser));
    }

    @FXML
    public void onConfirmClick() throws IOException {
        String passwordText = passwordField.getText();
        String confirmPasswordText = confirmPasswordField.getText();

        errorPasswordLabel.setText("");
        errorConfirmPasswordLabel.setText("");

        if (passwordText.isEmpty()) {
            errorPasswordLabel.setText("Please enter password");
            return;
        } else if (passwordText.length() < 8) {
            errorPasswordLabel.setText("Password should have at least 8 characters");
            return;
        } else {
            errorPasswordLabel.setText("");
        }

        if (confirmPasswordText.isBlank()) {
            errorConfirmPasswordLabel.setText("Please confirm your password");
            return;
        } else if (!confirmPasswordText.equals(passwordText)) {
            errorConfirmPasswordLabel.setText("Passwords aren't matched, please try again");
            return;
        } else {
            errorConfirmPasswordLabel.setText("");
        }

        newUser.setPassword(confirmPasswordText);
        users.addUser(newUser);
        datasource.writeData(users);
        FXRouter.goTo("login");
    }

    @FXML
    public void onConfirmByPressEnter(KeyEvent event) throws IOException {
        if (event.getCode() != KeyCode.ENTER) { return; }

        onConfirmClick();
        if (passwordField.isFocused() && errorPasswordLabel.getText().isBlank()) {
            errorConfirmPasswordLabel.setText("");
            confirmPasswordField.requestFocus();
        } else if (!errorPasswordLabel.getText().isBlank()) {
            passwordField.requestFocus();
        }
    }

}
