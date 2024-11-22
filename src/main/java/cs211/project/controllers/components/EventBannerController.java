package cs211.project.controllers.components;

import cs211.project.services.EventControllerHelper;
import cs211.project.models.Event;
import cs211.project.services.FXRouter;
import cs211.project.services.SceneUtility;
import cs211.project.services.TransitionUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class EventBannerController {
    @FXML private Label eventNameLabel;
    @FXML private Label maxParticipantsLabel;
    @FXML private Label eventVisibilityLabel;
    @FXML private ImageView imageBanner;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label participantStartDateLabel;
    @FXML private Label participantEndDateLabel;
    @FXML private Label joinedParticipantLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label participantStartTimeLabel;
    @FXML private Label participantEndTimeLabel;
    @FXML private Label teamAvailabilityLabel;
    @FXML private AnchorPane bannerAnchorPane;

    private Event event;
    private boolean isSearch;
    private final EventControllerHelper helper = new EventControllerHelper();

    public void isSearchBanner(boolean isSearch) { this.isSearch = isSearch; }

    public void setData(Event event) {
        this.event = event;
        helper.setBasicData(
                event,
                eventNameLabel,
                startDateLabel,
                endDateLabel,
                participantStartDateLabel,
                participantEndDateLabel,
                startTimeLabel,
                endTimeLabel,
                participantStartTimeLabel,
                participantEndTimeLabel,
                eventVisibilityLabel,
                imageBanner,
                teamAvailabilityLabel,
                joinedParticipantLabel,
                maxParticipantsLabel,
                !isSearch
        );
    }

    @FXML
    public void initialize(){
        TransitionUtility.slideVerticalTransition(bannerAnchorPane,30,500);
    }

    @FXML
    public void onBannerClick() {
        try {
            if (isSearch) {
                FXRouter.goTo("event-description", event.getID());
            }
            else{
                FXRouter.goTo("edit-event",event.getID());
                SceneUtility.addStyle("edit-event");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
