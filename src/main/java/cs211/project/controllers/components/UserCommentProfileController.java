package cs211.project.controllers.components;

import cs211.project.models.Comment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class UserCommentProfileController {
    @FXML Label displayNameLabel;
    @FXML Label commentLabel;
    @FXML Circle profileCircle;

    public void setData(Comment comment){
        displayNameLabel.setText(comment.getName());
        commentLabel.setText(comment.getComment());
        Image profilePicture = new Image(comment.getProfileImage());
        profileCircle.setFill(new ImagePattern(profilePicture));
    }

}
