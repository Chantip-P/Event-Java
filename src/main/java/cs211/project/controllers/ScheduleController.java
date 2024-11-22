package cs211.project.controllers;

import cs211.project.controllers.components.ActivityBannerController;
import cs211.project.models.Activity;
import cs211.project.models.Event;
import cs211.project.models.Team;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.Schedule;
import cs211.project.services.*;

import cs211.project.services.comparators.ActivityStartDateTimeComparator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;

public class ScheduleController extends TemplateController {
    @FXML private GridPane participantScheduleGrid;
    @FXML private GridPane teamScheduleGrid;
    @FXML private Tab organizerTeamTab;
    @FXML private Label teamNameLabel;
    @FXML private ChoiceBox<Team> selectTeamChoiceBox;
    @FXML private HBox choiceBoxHBox;
    
    @FXML
    public void initialize() throws IOException {
        super.initialize();
        Integer eventID = (Integer) FXRouter.getData();

        Datasource<EventList> eventDatasource = new EventListFileDatasource("data","event-list.json");
        EventList events = eventDatasource.readData();
        Event event = events.findEventByID(eventID);

        Schedule participantSchedule = event.getEventScheduleAsObject();

        String userRole = AuthenticationUtility.userAuthorizeAs(event);

        //Initialized participant schedule despite user's role
        showSchedule(participantSchedule,participantScheduleGrid,"Participant");

        if (userRole.equals("Participant")) {
            organizerTeamTab.setText("");
            organizerTeamTab.setGraphic(null);
            organizerTeamTab.setDisable(true);
        }

        if (userRole.equals("Organizer Team")) {
            ArrayList<Team> allEventTeam = new ArrayList<>(event.getOrganizerTeamsAsObject());
            Team targetTeam = null;

            // find if user is a member of that team
            for (Team team : allEventTeam) {
                if (team.getMemberIDList().contains(ProgramUtility.getLoggedInUserID())) {
                    targetTeam = team;
                }
            }

            if (targetTeam == null) { return; }
            teamNameLabel.setText(targetTeam.getTeamName());
            Schedule teamSchedule = targetTeam.getScheduleAsObject();
            showSchedule(teamSchedule, teamScheduleGrid, userRole);
        }

        if (userRole.equals("Event Organizer") || userRole.equals("Multiple Teams")) {
            choiceBoxHBox.setVisible(true);
            selectTeamChoiceBox.setVisible(true);
            String tabName;
            ArrayList<Team> allEventTeam = new ArrayList<>(event.getOrganizerTeamsAsObject());
            ArrayList<Team> displayTeams = new ArrayList<>();

            if (allEventTeam.isEmpty()){
                choiceBoxHBox.setVisible(false);
                teamNameLabel.setVisible(false);
            return;
            }

            if (userRole.equals("Event Organizer")) {
                tabName = "Event Organizer";
                displayTeams = allEventTeam;}

            else {
                tabName = "Multiple Teams";
                for (Team t : allEventTeam) {
                    if (t.getMemberIDList().contains(ProgramUtility.getLoggedInUserID())){
                        displayTeams.add(t);
                    }
                }
            }

            organizerTeamTab.setText(tabName);


            selectTeamChoiceBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Team team) {
                    return team.getTeamName();
                }

                @Override
                public Team fromString(String s) {
                    return null;
                }
            });

            selectTeamChoiceBox.setItems(FXCollections.observableArrayList(displayTeams));
            selectTeamChoiceBox.getSelectionModel().selectFirst();
            teamNameLabel.setText(selectTeamChoiceBox.getSelectionModel().getSelectedItem().getTeamName());
            showSchedule(selectTeamChoiceBox.getSelectionModel().getSelectedItem().getScheduleAsObject(),teamScheduleGrid,userRole);

            //Changing schedule when choice box is selected
            selectTeamChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue,oldTeam, newTeam) -> {
                teamNameLabel.setText(newTeam.getTeamName());
                showSchedule(newTeam.getScheduleAsObject(),teamScheduleGrid,userRole);
            });
        }

    }

    public void showSchedule(Schedule schedule,GridPane grid,String userRole){
        clearAll(grid);
        schedule.sort(new ActivityStartDateTimeComparator());

        int row = 1;

        for (Activity activity:schedule) {
            FXMLLoader bannerLoader = new FXMLLoader(getClass().getResource("/cs211/project/components/activity-banner.fxml"));
            Parent bannerRoot;
            try {
                bannerRoot = bannerLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ActivityBannerController activityBannerController = bannerLoader.getController();
            activityBannerController.setData(activity,userRole);
            
            grid.add(bannerRoot,0,++row);
        }
    }

    public void clearAll(GridPane grid) {
        grid.getChildren().clear();
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
