package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.CalculationUtility;
import cs211.project.services.Datasource;
import cs211.project.services.UserListFileDatasource;
import cs211.project.services.comparators.UserLoggedInTimeComparator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import java.io.IOException;

public class UserLogController extends TemplateController {

    @FXML private ListView<User> userlistView;

    @FXML private Circle circle;
    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label dateAndTimeLabel;

    @Override
    public void initialize() throws IOException {
        super.initialize();

        Datasource<UserList> datasource = new UserListFileDatasource("data", "user-list.json");
        UserList userList = datasource.readData();

        clearUserInfo();

        showList(userList);

        userlistView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                clearUserInfo();
            } else {
                showUserInfo(newValue);
            }
        });
    }

    private void clearUserInfo() {
        firstNameLabel.setText("");
        lastNameLabel.setText("");
        usernameLabel.setText("");
        dateAndTimeLabel.setText("");
        Image profilePicture = new Image(getClass().getResourceAsStream("/images/user-default.png"));
        circle.setFill(new ImagePattern(profilePicture));
    }

    private void showList(UserList userlist){
        userlistView.getItems().clear();

        userlist.sort(new UserLoggedInTimeComparator());

        userlistView.getItems().addAll(userlist.getUsers());
        userlistView.getItems().remove(userlist.findUserByID(0));
    }

    private void showUserInfo(User user) {
        firstNameLabel.setText(user.getFirstName());
        lastNameLabel.setText(user.getLastName());
        usernameLabel.setText(user.getUsername());
        dateAndTimeLabel.setText(
                CalculationUtility.formatDateTime(user.getLoggedInTime(), true));
        Image profilePicture = new Image(user.getProfileImagePath(true));
        circle.setFill(new ImagePattern(profilePicture));
    }
}
