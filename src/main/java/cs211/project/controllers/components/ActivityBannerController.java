package cs211.project.controllers.components;

import cs211.project.controllers.TeamCommentController;
import cs211.project.models.Activity;
import cs211.project.services.CalculationUtility;
import cs211.project.services.ProgramUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

import java.io.IOException;


public class ActivityBannerController {
    @FXML private Label activityNameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label activityDateLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private GridPane activityStatus;
    @FXML private Label contextDescriptionLabel;
    @FXML private Label contextNameLabel;
    @FXML private Button commentButton;

    private String activityName;
    private int activityID;

    boolean hasEnded;

    public void setData(Activity activity,String userRole) {
        activityNameLabel.setText(activity.getActivityName());
        contextNameLabel.setText(activity.getActivityName());
        descriptionLabel.setText(activity.getDescription());
        contextDescriptionLabel.setText(activity.getDescription());
        activityDateLabel.setText(CalculationUtility.formatDate(activity.getStartDateTime()));
        startTimeLabel.setText(CalculationUtility.formatTime(activity.getStartDateTime(), false));
        endTimeLabel.setText(CalculationUtility.formatTime(activity.getEndDateTime(), false));

        this.activityName = activity.getActivityName();
        this.activityID = activity.getID();

        // Set visibility based on the current status ONGOING & END
        this.hasEnded = !activity.checkActivityOnGoing() || activity.getForceEnd();
        if (hasEnded) {
            activityStatus.setVisible(true);
            Tooltip.install(activityStatus, new Tooltip("This activity has ended"));
        }
        else {
            activityStatus.setVisible(false);
        }

        if(userRole.equals("Participant")) {
            commentButton.setVisible(false);
        }

    }

    @FXML
    public void onCommentClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs211/project/views/team-comment.fxml"));

        ProgramUtility.openNewWindow(loader);

        TeamCommentController controller = loader.getController();
        controller.setUp(this.activityName,this.activityID,this.hasEnded);
    }

}
