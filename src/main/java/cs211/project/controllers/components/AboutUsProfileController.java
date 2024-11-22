package cs211.project.controllers.components;

import cs211.project.services.TransitionUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class AboutUsProfileController {
    @FXML Circle profileCircle;
    @FXML Label nameLabel;
    @FXML Label studentIDLabel;
    @FXML VBox profileBackground;

    @FXML
    public void initialize(){
        TransitionUtility.fadeInTransition(profileBackground, 1, 500);
        TransitionUtility.slideVerticalTransition(profileBackground,15,625);
    }

    public void setData(Image profileImage, String name, String studentID) {
        profileCircle.setFill(new ImagePattern(profileImage));
        nameLabel.setText(name);
        studentIDLabel.setText(studentID);
    }

}
