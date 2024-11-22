package cs211.project.controllers.components;

import cs211.project.models.User;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class TopbarController {
    @FXML Button profileButton;
    @FXML Circle profileCircle;

    public void initialize() {
        User user = new UserListFileDatasource("data", "user-list.json")
                .readData().findUserByID(ProgramUtility.getLoggedInUserID());

        Image profilePicture = new Image(user.getProfileImagePath(true));
        profileCircle.setFill(new ImagePattern(profilePicture));

        profileButton.setText(user.getFormattedFullName());
    }

    @FXML
    public void onProfileClick() throws IOException {
        FXRouter.goTo("settings",false);
    }

    @FXML
    public void onLogoutClick() throws IOException {
        ProgramUtility.logout();
        FXRouter.goTo("login");
    }
}
