package cs211.project.controllers;

import cs211.project.controllers.components.UserCommentProfileController;
import cs211.project.models.Comment;
import cs211.project.models.collections.CommentList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;

public class TeamCommentController {
    @FXML Label activityNameLabel;
    @FXML TextField commentTextField;
    @FXML VBox commentsLayout;
    @FXML GridPane inputGridPane;

    private Datasource<CommentList> commentDatasource;

    private int activityID;

    public void setUp(String activityName, int activityID,boolean hasEnded) {
        this.activityID = activityID;

        commentDatasource = new CommentListFileDatasource("data/comments", "comment-list-" + this.activityID + ".json");

        File commentFile = new File("data/comments", "comment-list-" + this.activityID + ".json");
        if (!commentFile.exists()) {
            CommentList newCommentList = new CommentList();
            commentDatasource.writeData(newCommentList);
        }
        CommentList commentList = commentDatasource.readData();


        activityNameLabel.setText(activityName);
        if (hasEnded) { inputGridPane.setVisible(false); }
        showComment(commentList);
    }

    public void showComment(CommentList comments){
        commentsLayout.getChildren().clear();

        for (Comment comment : comments){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs211/project/components/user-comment-profile.fxml"));
            HBox commentBox;
            try {
                commentBox = loader.load();
                UserCommentProfileController controller = loader.getController();
                controller.setData(comment);
                commentsLayout.getChildren().add(commentBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onSendCommentClick(){
        commentDatasource = new CommentListFileDatasource("data/comments", "comment-list-" + this.activityID + ".json");

        String commentText = commentTextField.getText().trim();
        if(!commentText.isEmpty()){
            CommentList commentList = commentDatasource.readData();
            Comment newComment = new Comment(ProgramUtility.getLoggedInUserID(),commentTextField.getText());
            commentList.addComment(newComment);
            commentDatasource.writeData(commentList);

            commentTextField.clear();
            showComment(commentList);
        }
    }

    @FXML
    public void onSendCommentByPressEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) { onSendCommentClick(); }
    }

}
