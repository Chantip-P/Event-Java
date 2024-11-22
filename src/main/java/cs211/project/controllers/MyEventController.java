package cs211.project.controllers;

import cs211.project.controllers.components.EventBannerController;
import cs211.project.models.Event;
import cs211.project.models.EventVisibility;
import cs211.project.models.collections.EventList;
import cs211.project.services.AuthenticationUtility;
import cs211.project.services.Datasource;
import cs211.project.services.EventListFileDatasource;
import cs211.project.services.comparators.EventStartDateTimeComparator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class MyEventController extends TemplateController{
    @FXML private TabPane eventTabPane;
    @FXML private Tab tabCurrent;
    @FXML private Tab tabEnded;
    @FXML private GridPane currentGrid;
    @FXML private GridPane endedGrid;
    @Override
    public void initialize() throws IOException {
        super.initialize();
        showEvents(currentGrid,getEventList("current"));

        eventTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tabCurrent) { showEvents(currentGrid,getEventList("current")); }
            else if (newValue == tabEnded) { showEvents(endedGrid,getEventList("ended")); }
        });
    }

    public EventList getEventList(String type){
        Datasource<EventList> datasource = new EventListFileDatasource("data","event-list.json");
        EventList events = datasource.readData();
        EventList userEvents = new EventList();

        for (Event event : events) {
            String userRole = AuthenticationUtility.userAuthorizeAs(event);
            if (userRole.equals("User")) continue;
            if (type.equals("current") && event.checkEventStatus() != 1 && event.getEventVisibility() == EventVisibility.PUBLIC) { userEvents.addEvent(event); }
            if (type.equals("ended") && event.checkEventStatus() == 1 && event.getEventVisibility() == EventVisibility.PUBLIC){ userEvents.addEvent(event); }
        }
        return userEvents;
    }
    public void showEvents(GridPane grid, EventList events)  {
        clearAll(grid);
        events.sort(new EventStartDateTimeComparator());

        int column = 0;
        int row = 1;

        for (Event event : events) {
            FXMLLoader bannerLoader = new FXMLLoader(getClass().getResource("/cs211/project/components/event-banner.fxml"));
            Parent bannerRoot;
            try {
                bannerRoot = bannerLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            EventBannerController eventBannerController = bannerLoader.getController();
            eventBannerController.isSearchBanner(true);
            eventBannerController.setData(event);

            if (column == 2) {
                column = 0;
                row++;}

            grid.add(bannerRoot, column++, row);
        }
    }

    public void clearAll(GridPane grid) {
        grid.getChildren().clear();
    }

}
