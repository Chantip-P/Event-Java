package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.Optional;

public class EventDescriptionController extends TemplateController {
    @FXML private Label eventNameLabel;
    @FXML private Label eventDescriptionLabel;
    @FXML private Label eventVisibilityLabel;
    @FXML private ImageView imageBanner;
    @FXML private Label startDateLabel;
    @FXML private  Label endDateLabel;
    @FXML private Label participantStartDateLabel;
    @FXML private Label participantEndDateLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label participantStartTimeLabel;
    @FXML private Label participantEndTimeLabel;
    @FXML private Button participateButton;
    @FXML private Button joinTeamButton;
    @FXML private Button scheduleButton;
    @FXML private HBox userStatusHBox;
    @FXML private Label userRoleLabel;
    @FXML private Label teamAvailabilityLabel;
    @FXML private Label  joinedParticipantLabel;
    @FXML private Label  maxParticipantsLabel;
    @FXML private HBox participateHBox;
    @FXML private HBox joinTeamHBox;
    @FXML private HBox scheduleHBox;
    private final EventControllerHelper helper = new EventControllerHelper();

    @FXML
    public void initialize() throws IOException {
        super.initialize();
        int eventID = (int) FXRouter.getData();
        Datasource<EventList> datasource = new EventListFileDatasource("data", "event-list.json");
        EventList events = datasource.readData();
        Event event = events.findEventByID(eventID);
        setData(event);
    }

    public void setData(Event event) {
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
                false
        );
        eventDescriptionLabel.setText(event.getEventDescription());

        String userRole = AuthenticationUtility.userAuthorizeAs(event);

        if (event.getBannedUsersIDList().contains(ProgramUtility.getLoggedInUserID())) {
            userRoleLabel.setText("Banned");
            Tooltip banToolTip = new Tooltip("Restricted due to ban");
            Tooltip.install(participateHBox, banToolTip);
            Tooltip.install(joinTeamHBox, banToolTip);
            Tooltip.install(scheduleHBox, banToolTip);

            participateButton.setDisable(true);
            joinTeamButton.setDisable(true);
            scheduleButton.setDisable(true);

            userStatusHBox.setVisible(true);
            scheduleButton.setVisible(true);
            return;
        }

        if (!userRole.equals("User")) {
            userRoleLabel.setText(userRole);

            Tooltip.install(participateHBox, new Tooltip("You're already in this event"));
            Tooltip.install(joinTeamHBox, new Tooltip("You're already in this event"));

            participateButton.setDisable(true);
            joinTeamButton.setDisable(true);
            userStatusHBox.setVisible(true);
            scheduleButton.setVisible(true);
            return;
        }

        if (!event.seatAvailable()) {
            participateButton.setDisable(true);
            if (!(event.isWithinParticipantAcceptanceTime())) {
                Tooltip.install(participateHBox, new Tooltip("Participant application is currently closed"));
            }
            else if (!(event.isBelowMaxParticipants())){
                Tooltip.install(participateHBox, new Tooltip("The event has reached its maximum participant capacity"));}
        }

        if (event.getOrganizerTeams().isEmpty()) {
            joinTeamButton.setDisable(true);
            Tooltip.install(joinTeamHBox, new Tooltip("There is no team in this event"));
        }

        scheduleButton.setVisible(false);

    }

    @FXML
    public void onParticipateClick() {
            int eventID = (int) FXRouter.getData();
            Datasource<EventList> datasource = new EventListFileDatasource("data", "event-list.json");
            EventList events = datasource.readData();
            Event event = events.findEventByID(eventID);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            Label contentLabel = new Label("Are you sure you want to join " + event.getEventName() + " ?");
            contentLabel.setWrapText(true);
            alert.getDialogPane().setContent(contentLabel);
            alert.getDialogPane().setMaxSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
            alert.getDialogPane().setMinSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);

            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);

            if (button == ButtonType.OK) {
                try {
                    event.joinEvent(ProgramUtility.getLoggedInUserID());
                    datasource.writeData(events);
                    FXRouter.goTo("join-event-success", eventID);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    @FXML
    public void onJoinTeamClick() throws IOException {
        int eventID = (int) FXRouter.getData();
        FXRouter.goTo("join-team",eventID);
        SceneUtility.addStyle("tableview-style");
    }

    @FXML
    public void onScheduleClick() throws IOException {
        int eventID = (int) FXRouter.getData();
        FXRouter.goTo("schedule",eventID);
    }

}
