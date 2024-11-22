package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SettingsController extends TemplateController {
    @FXML private Label errorNameLabel;
    @FXML private Label errorPasswordLabel;
    @FXML private Label themeLabel;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Circle profileCircle;
    @FXML ImageView themeSwitchImage;

    private Datasource<UserList> datasource;

    @Override
    public void initialize() throws IOException {
        super.initialize();

        datasource = new UserListFileDatasource("data","user-list.json");

        UserList userList = datasource.readData();
        User user = userList.findUserByID(ProgramUtility.getLoggedInUserID());

        Image profilePicture = new Image(user.getProfileImagePath(true));
        profileCircle.setFill(new ImagePattern(profilePicture));

        errorNameLabel.setText("");
        errorPasswordLabel.setText("");
        themeLabel.setText("");

        Object data = FXRouter.getData();
        if (data instanceof Boolean && (Boolean) data) {
            errorNameLabel.setTextFill(Color.GREEN);
            errorNameLabel.setText("Change complete");
        }

        firstNameTextField.setText(user.getFirstName());
        lastNameTextField.setText(user.getLastName());

        setThemeSwitchImageDisplay(SceneUtility.getCurrentTheme());

        setThemeSwitchLabel(SceneUtility.getCurrentTheme());
    }

    public void onChooseFileClick(ActionEvent event) {
        UserList userList = datasource.readData();
        User user = userList.findUserByID(ProgramUtility.getLoggedInUserID());

        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("images PNG JPG", "*.png", "*.jpg", "*.jpeg"));
        Node source = (Node) event.getSource();
        File file = chooser.showOpenDialog(source.getScene().getWindow());
        if (file != null){
            try {
                String destDirPath = "data/images/profiles";
                if (CalculationUtility.findFile(destDirPath, user.getUsername()) != null) {
                    Files.delete(CalculationUtility.findFile(destDirPath, user.getUsername()).toPath());
                }
                File target = CalculationUtility.copyExternalFile(file,destDirPath,user.getUsername());

                profileCircle.setFill(new ImagePattern(new Image(target.toPath().toUri().toString())));
                userList.changeProfileImage(user.getUsername(), destDirPath + "/" + target.getName());

                datasource.writeData(userList);
                FXRouter.goTo("settings");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSaveNameClick() {
        errorNameLabel.setTextFill(Color.RED);
        UserList userList = datasource.readData();

        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();

        if (firstName.isBlank() && lastName.isBlank()) {
            errorNameLabel.setText("Please enter first name and last name");
        } else if (firstName.isBlank()) {
            errorNameLabel.setText("Please enter first name");
        } else if (lastName.isBlank()) {
            errorNameLabel.setText("Please enter last name");
        }
        else{
            userList.changeFirstName(ProgramUtility.getLoggedInUserID(), firstName);
            userList.changeLastName(ProgramUtility.getLoggedInUserID(),lastName);

            datasource.writeData(userList);

            try {
                FXRouter.goTo("settings",true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void onSaveNameByPressEnter(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) { return; }

        onSaveNameClick();
        if (firstNameTextField.isFocused() && !firstNameTextField.getText().isBlank()) {
            errorNameLabel.setText("");
            lastNameTextField.requestFocus();
        } else if (lastNameTextField.isFocused() && firstNameTextField.getText().isBlank()) {
            firstNameTextField.requestFocus();
        }
    }

    public void onSavePasswordClick() {
        errorPasswordLabel.setTextFill(Color.RED);

        UserList userList = datasource.readData();
        User user = userList.findUserByID(ProgramUtility.getLoggedInUserID());

        String oldPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        String errorMessage;

        if(oldPassword.isEmpty()){
            errorMessage = "Please enter old password";
        }
        else if(!user.verifyPassword(oldPassword)){
            errorMessage = "Wrong password";
            currentPasswordField.clear();
        }
        else if(newPassword.isEmpty()){
            errorMessage = "Please enter new password";
        }
        else if(newPassword.length() < 8){
            errorMessage = "new password should have at least 8 characters";
            newPasswordField.clear();
        }
        else if(confirmPassword.isEmpty()){
            errorMessage = "Please enter confirm password";
        }
        else if(!newPassword.equals(confirmPassword)){
            errorMessage = "Passwords didn't match";
            confirmPasswordField.clear();
        }
        else if(oldPassword.equals(newPassword)){
            errorMessage = "New password and old password must not be the same";
            newPasswordField.clear();
            confirmPasswordField.clear();
        }
        else {
            errorMessage = "Change password complete";
            userList.changePassword(ProgramUtility.getLoggedInUserID(), oldPassword, newPassword);
            datasource.writeData(userList);

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            errorPasswordLabel.setTextFill(Color.GREEN);
        }
        errorPasswordLabel.setText(errorMessage);
    }

    @FXML
    public void onSavePasswordByPressEnter(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) { return; }

        onSavePasswordClick();
        if (currentPasswordField.isFocused() && !currentPasswordField.getText().isEmpty()
            && newPasswordField.getText().isEmpty()) {
            errorPasswordLabel.setText("");
            newPasswordField.requestFocus();
        } else if (newPasswordField.isFocused() && !newPasswordField.getText().isEmpty()
                && confirmPasswordField.getText().isEmpty()) {
            errorPasswordLabel.setText("");
            confirmPasswordField.requestFocus();
        } else if (currentPasswordField.getText().isEmpty()) {
            currentPasswordField.requestFocus();
        } else if (newPasswordField.getText().isEmpty()) {
            newPasswordField.requestFocus();
        } else if (confirmPasswordField.getText().isEmpty()) {
            confirmPasswordField.requestFocus();
        }
    }

    //theme switch
    @FXML
    public void onThemeSwitchClick() {
        UserList userList = datasource.readData();

        Theme currentTheme = SceneUtility.getCurrentTheme();
        Theme targetTheme;

        switch (currentTheme) {
            case DARK -> targetTheme = Theme.LIGHT;
            case LIGHT -> targetTheme = Theme.DARK;
            default -> throw new IllegalStateException("Unexpected value: " + currentTheme);
        }

        userList.changeUserDefaultTheme(ProgramUtility.getLoggedInUserID(), targetTheme);
        SceneUtility.setCurrentTheme(targetTheme);

        setThemeSwitchImageDisplay(targetTheme);
        setThemeSwitchLabel(SceneUtility.getCurrentTheme());

        datasource.writeData(userList);
    }

    private void setThemeSwitchImageDisplay(Theme theme) {
        String imagePath;
        switch (theme) {
            case DARK -> imagePath = "/images/icon-dark-mode.png";
            case LIGHT -> imagePath = "/images/icon-light-mode.png";
            default -> imagePath = "";
        }

        if (!imagePath.isEmpty()) {
            themeSwitchImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        }
    }

    private void setThemeSwitchLabel(Theme theme) {
        String type;
        switch (theme) {
            case DARK -> type = "Dark mode";
            case LIGHT -> type = "Light mode";
            default -> type = "";
        }
        themeLabel.setText(type);
    }
}
