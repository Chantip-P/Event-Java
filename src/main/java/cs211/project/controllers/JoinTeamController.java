package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.TeamAvailability;
import cs211.project.models.collections.EventList;
import cs211.project.services.*;

import cs211.project.services.comparators.TeamComparator;
import javafx.fxml.FXML;
import cs211.project.models.Team;
import cs211.project.models.collections.TeamList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;


public class JoinTeamController extends TemplateController{

    @FXML private TableView<Team> teamsTableView;
    @FXML private TableColumn<Team,Calendar> startDateTimeColumn;
    @FXML private TableColumn<Team,Calendar> endDateTimeColumn;
    @FXML private TableColumn<Team,String> teamNameColumn;
    @FXML private TableColumn<Team, TeamAvailability> AvailabilityColumn;


    @FXML
    public void initialize() throws IOException {
        super.initialize();
        int eventID = (int) FXRouter.getData();
        Datasource<EventList> datasource = new EventListFileDatasource("data", "event-list.json");
        EventList events = datasource.readData();
        Event event = events.findEventByID(eventID);

        Datasource<TeamList> teamDatasource = new TeamListFileDatasource("data", "team-list.json");
        TeamList teams = teamDatasource.readData();
        ArrayList<Integer> eventTeamListId = event.getOrganizerTeams();
        TeamList eventTeamList = new TeamList();

        for (Integer teamId : eventTeamListId) {
            eventTeamList.addTeam(teams.findTeamByID(teamId));
        }

        eventTeamList.sort(new TeamComparator());
        showTable(eventTeamList);

        teamsTableView.setRowFactory(row -> new TableRow<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    Tooltip tooltip = new Tooltip("Double click to join");
                    tooltip.setShowDelay(Duration.millis(500));
                    setTooltip(tooltip);
                    if (item.getAvailability() == TeamAvailability.UNAVAILABLE){
                        setStyle("-fx-background-color: #bbbbbb;");
                    }
                }
                else {
                    setStyle("");
                }
            }
        });
    }

    private TableCell<Team, Calendar> createDateTimeCellFactory() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Calendar item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(CalculationUtility.formatDate(item) + " " + CalculationUtility.formatTime(item, false));
                }
            }
        };
    }

    private void showTable(TeamList teamList){
        startDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        endDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        startDateTimeColumn.setCellFactory(column -> createDateTimeCellFactory());
        endDateTimeColumn.setCellFactory(column -> createDateTimeCellFactory());

        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        AvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        for(Team team: teamList.getTeams()){
            teamsTableView.getItems().add(team);
        }
    }
    @FXML
    private void handleTableViewMouseClicked(MouseEvent event) {
        int eventID = (int) FXRouter.getData();
        if (teamsTableView.getSelectionModel().getSelectedItem() == null){ return; }
        if (event.getClickCount() == 2) {
            Team selectedTeam = teamsTableView.getSelectionModel().getSelectedItem();
                int teamID = selectedTeam.getID();
            displayAlert(teamID, eventID, selectedTeam.getAvailability() == TeamAvailability.UNAVAILABLE);
            }
        }


    public void displayAlert(int teamID,int eventID,boolean isErrorAlert){


        if(isErrorAlert){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Occurred");
            alert.setContentText("The team is UNAVAILABLE, Please select AVAILABLE team");
            alert.showAndWait();
        }
        else {
            Datasource<TeamList> teamDatasource = new TeamListFileDatasource("data", "team-list.json");
            TeamList teams = teamDatasource.readData();

            Team selectedTeam = teams.findTeamByID(teamID);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Team Selection Confirmation");
            Label contentLabel = new Label("Are you sure you want to join " + selectedTeam.getTeamName() + " ?");
            contentLabel.setWrapText(true);
            alert.getDialogPane().setContent(contentLabel);
            alert.getDialogPane().setMaxSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
            alert.getDialogPane().setMinSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);

            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);
            if (button == ButtonType.OK) {
                try {
                    selectedTeam.addMemberIDList(ProgramUtility.getLoggedInUserID());
                    teamDatasource.writeData(teams);

                    FXRouter.goTo("join-event-success",eventID);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

    @FXML
    public void onBackToEventClick(){
        try {
            Integer eventID = (Integer) FXRouter.getData();
            FXRouter.goTo("event-description", eventID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
