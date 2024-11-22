package cs211.project.controllers;

import cs211.project.models.*;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.Schedule;
import cs211.project.models.collections.ScheduleList;
import cs211.project.models.collections.TeamList;
import cs211.project.services.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

public class EditEventController extends TemplateController {
    private Datasource<EventList> eventDatasource = new EventListFileDatasource("data", "event-list.json");
    private User selectedUser;
    private Team selectedTeam;
    private Team selectedAddingTeam;
    private Schedule selectedSchedule;
    private Activity selectedActivity;

    private boolean forcePublish = false;

    // Metadata
    @FXML private Label pageTitleLabel;
    @FXML private Button publishButton;
    @FXML private TabPane tabPane;
    @FXML private Tab participantsTab, organizerTeamTab, schedulesTab;

    // General
    @FXML private ImageView eventBannerImageView;
    @FXML private TextField eventNameTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private Spinner<Integer>
            startDateHourSpinner, startDateMinuteSpinner,
            endDateHourSpinner, endDateMinuteSpinner;

    // Participants
    @FXML private ListView<User> parListView;
    @FXML private Button parRemoveButton;
    @FXML private DatePicker parStartDatePicker, parEndDatePicker;
    @FXML private Spinner<Integer>
            parStartDateHourSpinner, parStartDateMinuteSpinner,
            parEndDateHourSpinner, parEndDateMinuteSpinner;
    @FXML private Label currParLabel, orgManageUserLabel;
    @FXML private TextField maxParTextField;
    @FXML private Pane orgManageUserPane;
    private int currentPar = 0;

    // Organizer Team
    @FXML private ChoiceBox<Team> orgTeamChoiceBox, orgManageUserTeamChoiceBox;
    @FXML private ListView<User> orgTeamMemberListView;
    @FXML private Button orgTeamMemberRemoveButton, addTeamButton, orgManageUserAddButton;
    @FXML private DatePicker orgStartDatePicker, orgEndDatePicker;
    @FXML private Spinner<Integer>
            orgStartDateHourSpinner, orgStartDateMinuteSpinner,
            orgEndDateHourSpinner, orgEndDateMinuteSpinner;
    @FXML private Label addTeamLabel, currOrgTeamMemberLabel;
    @FXML private TextField teamNameTextField, maxTeamMembersTextField;
    private int currentOrgTeamMember = 0;

    // Schedules
    @FXML private ChoiceBox<Schedule> scheduleChoiceBox;
    @FXML private TableView<Activity> scheduleTableView;
    @FXML private Button deselectActivityButton, endActivityButton, addActivityButton;
    @FXML private HBox editActivityButtonGroup;
    @FXML private DatePicker activityDatePicker;
    @FXML private Spinner<Integer>
            activityStartHourSpinner, activityStartMinuteSpinner,
            activityEndHourSpinner, activityEndMinuteSpinner;
    @FXML private Label addActivityLabel;
    @FXML private TextField activityNameTextField;
    @FXML private TextArea activityDescriptionTextArea;

    @FXML
    public void initialize() throws IOException {
        super.initialize();

        File file = CalculationUtility.findFile("data/images/event-banners", "temp");
        if (file != null) { Files.deleteIfExists(file.toPath()); }

        tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            selectedUser = null;
            selectedActivity = null;
            parListView.getSelectionModel().select(-1);
            orgTeamMemberListView.getSelectionModel().select(-1);
            scheduleTableView.getSelectionModel().select(null);
        });

        // Config List & Table Views
        {
            parListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldUser, newUser) -> selectedUser = newUser);

            parListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                public void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) { setText(null); }
                    else { setText(item.getFormattedFullName()); }
                }
            });

            orgTeamMemberListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldUser, newUser) -> {
                selectedUser = newUser;
                showManageUser(selectedUser, selectedAddingTeam);
            });

            orgTeamMemberListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                public void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) { setText(null); }
                    else { setText(item.getFormattedFullName()); }
                }
            });

            scheduleTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldActivity, newActivity) -> {
                selectedActivity = newActivity;

                showActivity(selectedActivity);
            });

            scheduleTableView.setRowFactory(row -> new TableRow<>() {
                @Override
                protected void updateItem(Activity item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty && item != null && !item.checkActivityOnGoing()) { setStyle("-fx-background-color: #bbbbbb;"); }
                    else { setStyle(""); }
                }
            });
        }

        // Config Choice Boxes
        {
            orgTeamChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTeam, newTeam) -> {
                selectedTeam = newTeam;
                selectedUser = null;

                showTeam(selectedTeam);
                showManageUser(selectedUser, selectedAddingTeam);
            });

            orgTeamChoiceBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Team team) {
                    return (team != null) ? team.getTeamName() : "(Add new team)";
                }

                @Override
                public Team fromString(String s) {
                    return null;
                }
            });

            orgManageUserTeamChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTeam, newTeam) ->
            {
                selectedAddingTeam = newTeam;
                showManageUser(selectedUser, selectedAddingTeam);
            });

            orgManageUserTeamChoiceBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Team team) {
                    return (team != null) ? team.getTeamName() : "NULL";
                }

                @Override
                public Team fromString(String s) {
                    return null;
                }
            });

            scheduleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSchedule, newSchedule) -> {
                selectedSchedule = newSchedule;
                selectedActivity = null;

                showSchedule(selectedSchedule);
            });
        }

        // Setup Table View Columns
        {
            TableColumn<Activity, Calendar> dateColumn = new TableColumn<>("Date");
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
            dateColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Calendar item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setText(null); }
                    else { setText(CalculationUtility.formatDate(item)); }
                }
            });
            dateColumn.setPrefWidth(70);
            dateColumn.setSortable(false);
            dateColumn.setResizable(false);

            TableColumn<Activity, Calendar> startTimeColumn = new TableColumn<>("Start");
            startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
            startTimeColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Calendar item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setText(null); }
                    else { setText(CalculationUtility.formatTime(item, false)); }
                }
            });
            startTimeColumn.setPrefWidth(35);
            startTimeColumn.setSortable(false);
            startTimeColumn.setResizable(false);

            TableColumn<Activity, Calendar> endTimeColumn = new TableColumn<>("End");
            endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
            endTimeColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Calendar item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setText(null); }
                    else { setText(CalculationUtility.formatTime(item, false)); }
                }
            });
            endTimeColumn.setPrefWidth(35);
            endTimeColumn.setSortable(false);
            endTimeColumn.setResizable(false);

            TableColumn<Activity, String> activityNameColumn = new TableColumn<>("Name");
            activityNameColumn.setCellValueFactory(new PropertyValueFactory<>("activityName"));
            activityNameColumn.setPrefWidth(90);
            activityNameColumn.setSortable(false);
            activityNameColumn.setResizable(false);

            scheduleTableView.getColumns().clear();
            scheduleTableView.getColumns().add(dateColumn);
            scheduleTableView.getColumns().add(startTimeColumn);
            scheduleTableView.getColumns().add(endTimeColumn);
            scheduleTableView.getColumns().add(activityNameColumn);
        }

        int eventId = (int)FXRouter.getData();
        if (eventId == -1) {
            showBlank();
        }
        else {
            Event event = eventDatasource.readData().findEventByID(eventId);
            showEvent(event);
        }
    }

    @FXML
    public void onSaveEventClick() {
        writeEventData(forcePublish);
    }

    @FXML
    public void onPublishEventClick() {
        writeEventData(true);
    }

    @FXML
    public void onUploadImageClick(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("images PNG JPG", "*.png", "*.jpg", "*.jpeg"));
        Node source = (Node) actionEvent.getSource();
        File file = chooser.showOpenDialog(source.getScene().getWindow());
        if (file != null) {
            File copy = CalculationUtility.copyExternalFile(file, "data/images/event-banners", "temp");
            eventBannerImageView.setImage(new Image(copy.toPath().toUri().toString()));
        }
    }

    @FXML
    public void onRemoveClick() {
        int eventId = (int)FXRouter.getData();
        if (selectedUser == null || eventId == -1) { return; }

        if (displayWarningAlert("Removing User",
                String.format("Are you sure you want to remove %s from this event? This user will be unable to join this event again.", selectedUser.getFormattedFullName()))) {
            return;
        }

        EventList eventList = eventDatasource.readData();
        Event event = eventList.findEventByID(eventId);
        event.ban(selectedUser.getID());

        eventDatasource.writeData(eventList);

        setupParticipantsTab(event);
        setupOrganizerTeamTab(event, orgTeamChoiceBox.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void onAddTeamClick() {
        int eventId = (int)FXRouter.getData();
        if (eventId == -1) { return; }

        ArrayList<String> errorList = getSaveTeamErrorList();
        if (displayErrorAlert("Cannot create team", errorList)) { return; }

        ArrayList<String> warningList = getSaveTeamWarningList();
        if (displayWarningAlert("Warning", warningList)) { return; }

        String teamName = teamNameTextField.getText();
        Calendar startDateTime = extractCalendar(orgStartDatePicker, orgStartDateHourSpinner, orgStartDateMinuteSpinner);
        Calendar endDateTime = extractCalendar(orgEndDatePicker, orgEndDateHourSpinner, orgEndDateMinuteSpinner);
        int maxMember = Integer.parseInt(maxTeamMembersTextField.getText());

        Datasource<TeamList> teamDatasource = new TeamListFileDatasource("data", "team-list.json");
        TeamList teamList = teamDatasource.readData();

        EventList eventList = eventDatasource.readData();
        Event event = eventList.findEventByID(eventId);

        int finalTeamIndex;

        if (selectedTeam == null) {
            Team adding = new Team(teamName, maxMember, startDateTime, endDateTime);

            teamList.addTeam(adding);

            event.addTeamByID(adding.getID());
            eventDatasource.writeData(eventList);

            finalTeamIndex = event.getOrganizerTeams().indexOf(adding.getID());
        }
        else {
            Team editing = teamList.findTeamByID(selectedTeam.getID());

            editing.setTeamName(teamName);
            editing.setMaximumMember(maxMember);
            editing.setStartDateTime(startDateTime);
            editing.setEndDateTime(endDateTime);

            finalTeamIndex = event.getOrganizerTeams().indexOf(editing.getID());
        }

        teamDatasource.writeData(teamList);

        // Finalize
        displayAlert(Alert.AlertType.INFORMATION, "Save successful",
                "The team has been saved.");

        setupOrganizerTeamTab(event, finalTeamIndex);
        setupSchedulesTab(event, scheduleChoiceBox.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void onManageUserAddClick() {
        int eventId = (int)FXRouter.getData();
        if (selectedUser == null || eventId == -1) { return; }

        EventList eventList = eventDatasource.readData();
        Event event = eventList.findEventByID(eventId);

        Datasource<TeamList> teamDatasource = new TeamListFileDatasource("data", "team-list.json");
        TeamList teams = teamDatasource.readData();
        Team team = teams.findTeamByID(selectedAddingTeam.getID());

        if (team.isFull()) {
            displayAlert(Alert.AlertType.ERROR, "Cannot add member to team",
                    String.format("%s is full.", selectedAddingTeam.getTeamName()));
            return;
        }

        team.addMemberIDList(selectedUser.getID());

        teamDatasource.writeData(teams);

        displayAlert(Alert.AlertType.INFORMATION, "Save Successful",
                String.format("%s has been added to %s.", selectedUser.getFormattedFullName(), team.getTeamName()));

        setupOrganizerTeamTab(event, orgTeamChoiceBox.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void onDeselectActivityClick() {
        selectedActivity = null;
        scheduleTableView.getSelectionModel().select(null);
    }

    @FXML
    public void onAddActivityClick() {
        int eventId = (int)FXRouter.getData();
        if (eventId == -1) { return; }

        ArrayList<String> errorList = getSaveActivityErrorList();

        if (displayErrorAlert("Cannot create activity", errorList)) { return; }

        String activityName = activityNameTextField.getText();
        Calendar startDateTime = extractCalendar(activityDatePicker, activityStartHourSpinner, activityStartMinuteSpinner);
        Calendar endDateTime = extractCalendar(activityDatePicker, activityEndHourSpinner, activityEndMinuteSpinner);
        String activityDesc = activityDescriptionTextArea.getText();

        Event event = eventDatasource.readData().findEventByID(eventId);

        Datasource<ScheduleList> scheduleDatasource = new ScheduleListFileDatasource("data", "schedule-list.json");
        ScheduleList scheduleList = scheduleDatasource.readData();
        Schedule editingSchedule = scheduleList.findScheduleByID(selectedSchedule.getID());

        Activity changing;

        if (selectedActivity != null) {
            editingSchedule.removeActivity(selectedActivity, false);
            changing = new Activity(activityName, activityDesc, startDateTime, endDateTime, selectedActivity.getID());
        }
        else {
            changing = new Activity(activityName, activityDesc, startDateTime, endDateTime);
        }

        if (!editingSchedule.addActivity(changing)) {
            displayAlert(Alert.AlertType.ERROR, "Cannot create activity",
                    "Please check if the activity time overlaps with other activities.");

            return;
        }
        else if (editingSchedule.getID() == event.getEventScheduleID() &&
                (changing.getStartDateTime().before(event.getStartDateTime()) || changing.getEndDateTime().after(event.getEndDateTime()))) {
            displayAlert(Alert.AlertType.ERROR, "Cannot create activity",
                    "Participant's activity period must be within event period.");

            return;
        }

        scheduleDatasource.writeData(scheduleList);

        // Finalize
        displayAlert(Alert.AlertType.INFORMATION, "Save successful",
                "The activity has been saved.");

        setupSchedulesTab(event, scheduleChoiceBox.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void onRemoveActivityClick() {
        int eventId = (int)FXRouter.getData();
        if (eventId == -1 || selectedActivity == null) { return; }

        if (displayWarningAlert("Removing Activity",
                String.format("Are you sure you want to delete %s?", selectedActivity.getActivityName()))) {
            return;
        }

        Datasource<ScheduleList> scheduleDatasource = new ScheduleListFileDatasource("data", "schedule-list.json");
        ScheduleList scheduleList = scheduleDatasource.readData();
        Schedule editingSchedule = scheduleList.findScheduleByID(selectedSchedule.getID());

        editingSchedule.removeActivity(selectedActivity, true);

        scheduleDatasource.writeData(scheduleList);

        // Finalize
        Event event = eventDatasource.readData().findEventByID(eventId);
        setupSchedulesTab(event, scheduleChoiceBox.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void onEndActivityClick() {
        int eventId = (int)FXRouter.getData();
        if (eventId == -1 || selectedActivity == null) { return; }

        Datasource<ScheduleList> scheduleDatasource = new ScheduleListFileDatasource("data", "schedule-list.json");
        ScheduleList scheduleList = scheduleDatasource.readData();
        Schedule editingSchedule = scheduleList.findScheduleByID(selectedSchedule.getID());

        editingSchedule.getSchedule().get(scheduleTableView.getSelectionModel().getSelectedIndex()).setForceEnd();

        scheduleDatasource.writeData(scheduleList);

        // Finalize
        Event event = eventDatasource.readData().findEventByID(eventId);
        setupSchedulesTab(event, scheduleChoiceBox.getSelectionModel().getSelectedIndex());
    }

    private void showBlank() {
        pageTitleLabel.setText("Create Event");
        publishButton.setVisible(false);

        hideTab(participantsTab);
        hideTab(organizerTeamTab);
        hideTab(schedulesTab);
    }

    private void showEvent(Event event) {
        pageTitleLabel.setText("Edit Event: " + event.getEventName());

        if (event.getEventVisibility() == EventVisibility.PUBLIC) {
            publishButton.setText("Published");
            publishButton.setDisable(true);

            forcePublish = true;
        }

        setupGeneralTab(event);
        setupParticipantsTab(event);
        setupOrganizerTeamTab(event);
        setupSchedulesTab(event);
    }

    private void setupGeneralTab(Event event) {
        eventBannerImageView.setImage(new Image(event.getEventBannerPath()));

        eventNameTextField.setText(event.getEventName());
        descriptionTextArea.setText(event.getEventDescription());

        setCalendarToPicker(event.getStartDateTime(), startDatePicker, startDateHourSpinner, startDateMinuteSpinner);
        setCalendarToPicker(event.getEndDateTime(), endDatePicker, endDateHourSpinner, endDateMinuteSpinner);
    }

    private void setupParticipantsTab(Event event) {
        if (event.getEventVisibility() == EventVisibility.DRAFT) {
            parListView.setDisable(true);
            parRemoveButton.setDisable(true);
        }
        else {
            parListView.getItems().clear();
            parListView.getItems().addAll(event.getParticipantsAsUser());
        }

        setCalendarToPicker(event.getParticipantAcceptStartDateTime(), parStartDatePicker, parStartDateHourSpinner, parStartDateMinuteSpinner);
        setCalendarToPicker(event.getParticipantAcceptEndDateTime(), parEndDatePicker, parEndDateHourSpinner, parEndDateMinuteSpinner);

        currentPar = event.getJoinedParticipants();
        currParLabel.setText(String.format("%d/%d", currentPar, event.getMaxParticipants()));

        maxParTextField.setText(String.valueOf(event.getMaxParticipants()));
    }

    private void setupOrganizerTeamTab(Event event) {
        setupOrganizerTeamTab(event, 0);
    }

    private void setupOrganizerTeamTab (Event event, int teamSelectIndex) {
        ArrayList<Team> allEventTeam = new ArrayList<>(event.getOrganizerTeamsAsObject());
        orgManageUserTeamChoiceBox.setItems(FXCollections.observableArrayList(allEventTeam));
        orgManageUserTeamChoiceBox.getSelectionModel().select(0);

        allEventTeam.add(null);
        orgTeamChoiceBox.setItems(FXCollections.observableArrayList(allEventTeam));
        orgTeamChoiceBox.getSelectionModel().select(teamSelectIndex);

        orgManageUserPane.setVisible(false);

        if (event.getOrganizerTeams().isEmpty()) { showTeam(null); }
    }

    private void setupSchedulesTab(Event event) {
        setupSchedulesTab(event, 0);
    }

    private void setupSchedulesTab(Event event, int scheduleSelectIndex) {
        ArrayList<Schedule> allEventSchedule = new ArrayList<>();
        allEventSchedule.add(event.getEventScheduleAsObject());
        for (Team t : event.getOrganizerTeamsAsObject()) {
            allEventSchedule.add(t.getScheduleAsObject());
        }

        scheduleChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Schedule schedule) {
                if (schedule == null) { return "NULL"; }

                if (schedule.isScheduleID(event.getEventScheduleID())) { return "Participants"; }

                for (Team t : event.getOrganizerTeamsAsObject()) {
                    if (schedule.isScheduleID(t.getScheduleID())) { return t.getTeamName(); }
                }

                return "ERROR";
            }

            @Override
            public Schedule fromString(String s) {
                return null;
            }
        });
        scheduleChoiceBox.setItems(FXCollections.observableArrayList(allEventSchedule));
        scheduleChoiceBox.getSelectionModel().select(scheduleSelectIndex);

        if (scheduleChoiceBox.getSelectionModel().getSelectedItem() == null) { showSchedule(null); }
        showActivity(null);
    }

    private void showTeam(Team team) {
        orgTeamMemberListView.getItems().clear();

        if (team != null) {
            orgTeamMemberListView.getItems().addAll(team.getMemberAsUser());
            orgTeamMemberListView.setDisable(false);
            orgTeamMemberRemoveButton.setDisable(false);

            addTeamLabel.setText("Edit Team: " + team.getTeamName());

            setCalendarToPicker(team.getStartDateTime(), orgStartDatePicker, orgStartDateHourSpinner, orgStartDateMinuteSpinner);
            setCalendarToPicker(team.getEndDateTime(), orgEndDatePicker, orgEndDateHourSpinner, orgEndDateMinuteSpinner);

            teamNameTextField.setText(team.getTeamName());
            maxTeamMembersTextField.setText(String.valueOf(team.getMaximumMember()));

            currentOrgTeamMember = team.getCurrentMember();
            currOrgTeamMemberLabel.setText(String.format("%d/%d", currentOrgTeamMember, team.getMaximumMember()));
            addTeamButton.setText("Save Changes");
        }
        else {
            orgTeamMemberListView.setDisable(true);
            orgTeamMemberRemoveButton.setDisable(true);

            addTeamLabel.setText("Add Team");

            setCalendarToPicker(null, orgStartDatePicker, orgStartDateHourSpinner, orgStartDateMinuteSpinner);
            setCalendarToPicker(null, orgEndDatePicker, orgEndDateHourSpinner, orgEndDateMinuteSpinner);

            teamNameTextField.setText("");
            maxTeamMembersTextField.setText("");

            currentOrgTeamMember = 0;
            currOrgTeamMemberLabel.setText("---");
            addTeamButton.setText("Add Team");
        }
    }

    private void showManageUser(User user, Team team) {
        orgManageUserPane.setVisible(user != null && team != null);

        if (user == null || team == null) { return; }
        orgManageUserLabel.setText(String.format("Add %s to ", user.getFormattedFullName()));

        if (team.getMemberIDList().contains(user.getID())) {
            orgManageUserAddButton.setDisable(true);
            orgManageUserAddButton.setText("User already in team");
        }
        else {
            orgManageUserAddButton.setDisable(false);
            orgManageUserAddButton.setText("Add");
        }
    }

    private void showSchedule(Schedule schedule) {
        scheduleTableView.getItems().clear();

        if (schedule == null || schedule.getSchedule().isEmpty()) {
            scheduleTableView.setDisable(true);
            return;
        }
        else { scheduleTableView.setDisable(false); }

        for (Activity activity : schedule) {
            scheduleTableView.getItems().add(activity);
        }
    }

    private void showActivity(Activity activity) {
        if (activity != null) {
            addActivityLabel.setText("Edit Activity: " + activity.getActivityName());

            activityNameTextField.setText(activity.getActivityName());
            setCalendarToPicker(activity.getStartDateTime(), activityDatePicker, activityStartHourSpinner, activityStartMinuteSpinner);
            setCalendarToPicker(activity.getEndDateTime(), activityDatePicker, activityEndHourSpinner, activityEndMinuteSpinner);
            activityDescriptionTextArea.setText(activity.getDescription());

            editActivityButtonGroup.setDisable(false);
            deselectActivityButton.setVisible(true);
            addActivityButton.setText("Save Changes");

            if (activity.checkActivityOnGoing()) {
                endActivityButton.setDisable(false);
                endActivityButton.setText("End Activity");
            }
            else {
                endActivityButton.setDisable(true);
                endActivityButton.setText("Activity Ended");
            }
        }
        else {
            addActivityLabel.setText("Add Activity");

            activityNameTextField.setText("");
            setCalendarToPicker(null, activityDatePicker, activityStartHourSpinner, activityStartMinuteSpinner);
            setCalendarToPicker(null, activityDatePicker, activityEndHourSpinner, activityEndMinuteSpinner);
            activityDescriptionTextArea.setText("");

            editActivityButtonGroup.setDisable(true);
            deselectActivityButton.setVisible(false);
            addActivityButton.setText("Add Activity");

            endActivityButton.setText("End Activity");
        }
    }

    private void writeEventData(boolean publishing) {
        ArrayList<String> errorList = getSaveErrorList(publishing);
        if (displayErrorAlert("Cannot save event", errorList)) { return; }

        ArrayList<String> warningList = getSaveWarningList();
        if (displayWarningAlert("Warning", warningList)) { return; }

        // Read Inputs
        String eventName = eventNameTextField.getText();
        String eventDesc = descriptionTextArea.getText();
        Calendar startDateTime = extractCalendar(startDatePicker, startDateHourSpinner, startDateMinuteSpinner);
        Calendar endDateTime = extractCalendar(endDatePicker, endDateHourSpinner, endDateMinuteSpinner);
        Calendar parStartDateTime = extractCalendar(parStartDatePicker, parStartDateHourSpinner, parStartDateMinuteSpinner);
        Calendar parEndDateTime = extractCalendar(parEndDatePicker, parEndDateHourSpinner, parEndDateMinuteSpinner);
        int maxPar = maxParTextField.getText().isBlank() ? 1 : Integer.parseInt(maxParTextField.getText());

        // Find event reference
        int eventId = (int)FXRouter.getData();
        EventList eventList = eventDatasource.readData();
        Event event;

        if (eventId == -1) {
            event = new Event(eventName, eventDesc, startDateTime, endDateTime);
            eventList.addEvent(event);
        }
        else {
            event = eventList.findEventByID(eventId);
        }

        // Set event data...
        event.setEventName(eventName);
        event.setEventDescription(eventDesc);
        event.setStartDateTime(startDateTime);
        event.setEndDateTime(endDateTime);
        event.setParticipantAcceptStartDateTime(parStartDateTime);
        event.setParticipantAcceptEndDateTime(parEndDateTime);
        event.setMaxParticipants(maxPar);

        CalculationUtility.renameFile("data/images/event-banners", "temp", String.valueOf(event.getID()));

        if (publishing) { event.publishEvent(); }

        // Finalize
        eventDatasource.writeData(eventList);

        if (publishing) {
            displayAlert(Alert.AlertType.INFORMATION, "Publish successful",
                    "Your event has been published.");
        }
        else {
            displayAlert(Alert.AlertType.INFORMATION, "Save successful",
                    "Your event has been saved.");
        }

        // Refresh page...
        try {
            FXRouter.goTo("edit-event", event.getID());
            SceneUtility.addStyle("edit-event");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void hideTab(Tab tab) {
        tab.setDisable(true);
        tab.setTooltip(new Tooltip("Available after creating event. (Save once)"));
    }

    private Calendar extractCalendar(DatePicker datePicker, Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner) {
        int hour = (hourSpinner.getValue() == null) ? 0 : hourSpinner.getValue();
        int minute = (minuteSpinner.getValue() == null) ? 0 : minuteSpinner.getValue();
        return CalculationUtility.localDateToCalendar(datePicker.getValue(), hour, minute);
    }

    private void setCalendarToPicker(Calendar calendar, DatePicker datePicker, Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner) {
        if (calendar != null) {
            datePicker.setValue(CalculationUtility.calendarToLocalDate(calendar));
            hourSpinner.getValueFactory().setValue(calendar.get(Calendar.HOUR_OF_DAY));
            minuteSpinner.getValueFactory().setValue(calendar.get(Calendar.MINUTE));
        } else {
            datePicker.setValue(null);
            hourSpinner.getValueFactory().setValue(0);
            minuteSpinner.getValueFactory().setValue(0);
        }
    }

    private ArrayList<String> getSaveErrorList(boolean publishing) {
        String eventName = eventNameTextField.getText();
        String eventDesc = descriptionTextArea.getText();
        Calendar startDateTime = extractCalendar(startDatePicker, startDateHourSpinner, startDateMinuteSpinner);
        Calendar endDateTime = extractCalendar(endDatePicker, endDateHourSpinner, endDateMinuteSpinner);
        Calendar parStartDateTime = extractCalendar(parStartDatePicker, parStartDateHourSpinner, parStartDateMinuteSpinner);
        Calendar parEndDateTime = extractCalendar(parEndDatePicker, parEndDateHourSpinner, parEndDateMinuteSpinner);

        ArrayList<String> saveErrorList = new ArrayList<>();
        ArrayList<String> publishErrorList = new ArrayList<>();

        // Mandatory
        if (eventName.isBlank()) { saveErrorList.add("- Event name cannot be blank."); }

        if (eventDesc.isBlank()) { saveErrorList.add("- Description cannot be blank."); }

        if (startDateTime == null) { saveErrorList.add("- Start date cannot be blank."); }

        if (endDateTime == null) { saveErrorList.add("- End date cannot be blank."); }
        else if (endDateTime.before(Calendar.getInstance())) { saveErrorList.add("- End date must be after current time."); }
        else if (startDateTime != null && (endDateTime.before(startDateTime) || endDateTime.equals(startDateTime))) { saveErrorList.add("- End date must be after start date."); }

        if (parStartDateTime == null) { publishErrorList.add("- Participant application period: Start date cannot be blank."); }
        else if (parStartDateTime.after(startDateTime)) { saveErrorList.add("- Participant application period: Start date cannot be after event start time."); }

        if (parEndDateTime == null) { publishErrorList.add("- Participant application period: End date cannot be blank."); }
        else if (parStartDateTime != null && (parEndDateTime.before(parStartDateTime) || parEndDateTime.equals(parStartDateTime))) { saveErrorList.add("- Participant application period: End date must be after start date."); }
        else if (parEndDateTime.after(startDateTime)) { saveErrorList.add("- Participant application period: End date cannot be after event start time."); }

        if (!maxParTextField.getText().isBlank()) {
            try {
                int maxPar = Integer.parseInt(maxParTextField.getText());
                if (maxPar < 1) { saveErrorList.add("- Max participants must be at least 1."); }
                else if (maxPar < currentPar) { publishErrorList.add("- Max participants must not be lower than current participants."); }
            }
            catch (NumberFormatException e) { saveErrorList.add("- Max participants must be an integer."); }
        }
        else { publishErrorList.add("- Max participant must not be blank."); }

        if (saveErrorList.isEmpty() && !publishErrorList.isEmpty() && publishing) {
            publishErrorList.add("\nConsider saving the event first.");
        }

        if (publishing) { saveErrorList.addAll(publishErrorList); }

        return saveErrorList;
    }

    private ArrayList<String> getSaveWarningList() {
        Calendar startDateTime = extractCalendar(startDatePicker, startDateHourSpinner, startDateMinuteSpinner);
        Calendar parEndDateTime = extractCalendar(parEndDatePicker, parEndDateHourSpinner, parEndDateMinuteSpinner);

        ArrayList<String> saveWarningList = new ArrayList<>();

        if (startDateTime.before(Calendar.getInstance())) { saveWarningList.add("- You are saving an event that has already started."); }
        if (parEndDateTime != null && parEndDateTime.before(Calendar.getInstance())) { saveWarningList.add("- Participants won't be able to join this event because the application period has ended."); }

        return saveWarningList;
    }

    private ArrayList<String> getSaveTeamErrorList() {
        int eventId = (int)FXRouter.getData();
        Event event = eventDatasource.readData().findEventByID(eventId);

        ArrayList<Team> existingTeams = event.getOrganizerTeamsAsObject();
        ArrayList<String> existingTeamsName = new ArrayList<>();
        for (Team t : existingTeams) { existingTeamsName.add(t.getTeamName()); }

        String teamName = teamNameTextField.getText();
        Calendar startDateTime = extractCalendar(orgStartDatePicker, orgStartDateHourSpinner, orgStartDateMinuteSpinner);
        Calendar endDateTime = extractCalendar(orgEndDatePicker, orgEndDateHourSpinner, orgEndDateMinuteSpinner);

        ArrayList<String> errorList = new ArrayList<>();

        if (teamName.isBlank()) { errorList.add("- Team name cannot be blank."); }
        else if (existingTeamsName.contains(teamName) && (selectedTeam == null || !selectedTeam.getTeamName().equals(teamName))) {
            errorList.add("- Team already exists.");
        }

        if (startDateTime == null) { errorList.add("- Application start date cannot be blank."); }

        if (endDateTime == null) { errorList.add("- Application end date cannot be blank."); }
        else if (startDateTime != null && (endDateTime.before(startDateTime) || endDateTime.equals(startDateTime))) { errorList.add("- Application end date must be after application start date."); }
        else if (endDateTime.after(event.getStartDateTime())) { errorList.add("- Application end date cannot be after event start time."); }

        if (!maxTeamMembersTextField.getText().isBlank()) {
            try {
                int maxMember = Integer.parseInt(maxTeamMembersTextField.getText());
                if (maxMember < 1) { errorList.add("- Max team members must be at least 1."); }
                else if (maxMember < currentOrgTeamMember) { errorList.add("- Max team members must not be lower than current team members."); }
            }
            catch (NumberFormatException e) { errorList.add("- Max team members must be an integer."); }
        }
        else { errorList.add("- Max team members must not be blank."); }

        return errorList;
    }


    private ArrayList<String> getSaveTeamWarningList() {
        Calendar startDateTime = extractCalendar(orgStartDatePicker, orgStartDateHourSpinner, orgStartDateMinuteSpinner);
        Calendar endDateTime = extractCalendar(orgEndDatePicker, orgEndDateHourSpinner, orgEndDateMinuteSpinner);

        ArrayList<String> saveWarningList = new ArrayList<>();

        if (endDateTime.before(Calendar.getInstance())) { saveWarningList.add("- Users won't be able to join this team because the application period has ended."); }
        else if (startDateTime.before(Calendar.getInstance())) { saveWarningList.add("- You are saving a team that has already started accepting users."); }

        return saveWarningList;
    }

    private ArrayList<String> getSaveActivityErrorList() {
        ArrayList<String> errorList = new ArrayList<>();

        String activityName = activityNameTextField.getText();
        Calendar startDateTime = extractCalendar(activityDatePicker, activityStartHourSpinner, activityStartMinuteSpinner);
        Calendar endDateTime = extractCalendar(activityDatePicker, activityEndHourSpinner, activityEndMinuteSpinner);
        String activityDesc = activityDescriptionTextArea.getText();

        if (activityName.isBlank()) { errorList.add("- Activity name cannot be blank."); }

        if (startDateTime == null || endDateTime == null) { errorList.add("- Activity date cannot be blank."); }
        else {
            if (startDateTime.before(Calendar.getInstance())) { errorList.add("- Activity start date must be after current time."); }

            if (endDateTime.before(Calendar.getInstance())) { errorList.add("- Activity end date must be after current time."); }
            else if (endDateTime.before(startDateTime) || endDateTime.equals(startDateTime)) { errorList.add("- Activity end date must be after activity start date."); }
        }

        if (activityDesc.isBlank()) { errorList.add("- Activity description cannot be blank."); }

        return errorList;
    }

    private boolean displayErrorAlert(String title, ArrayList<String> errorList) {
        if (errorList.isEmpty()) { return false; }

        Alert alert = getNewAlertBox(Alert.AlertType.ERROR, title,
                "Please fix the following problems:\n" + String.join("\n", errorList));

        alert.show();

        return true;
    }

    private boolean displayWarningAlert(String title, ArrayList<String> warningList) {
        if (warningList.isEmpty()) { return false; }

        Alert alert = getNewAlertBox(Alert.AlertType.WARNING, title,
                "You have the following warnings:\n" + String.join("\n", warningList) + "\n\nAre you sure you want to save?");

        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);

        return button != ButtonType.OK;
    }

    private boolean displayWarningAlert(String title, String content) {
        Alert alert = getNewAlertBox(Alert.AlertType.WARNING, title, content);

        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);

        return button != ButtonType.OK;
    }

    private void displayAlert(Alert.AlertType type, String title, String content) {
        Alert alert = getNewAlertBox(type, title, content);
        alert.show();
    }

    private Alert getNewAlertBox(Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.getDialogPane().setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        alert.getDialogPane().setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        return alert;
    }

    private Alert getNewAlertBox(Alert.AlertType type, String title, String content) {
        Alert alert = getNewAlertBox(type);
        alert.setTitle(title);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);

        alert.getDialogPane().setContent(contentLabel);

        return alert;
    }
}
