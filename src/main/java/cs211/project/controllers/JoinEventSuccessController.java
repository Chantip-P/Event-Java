package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.services.Datasource;
import cs211.project.services.EventListFileDatasource;
import cs211.project.services.FXRouter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.IOException;

public class JoinEventSuccessController extends TemplateController{
    @FXML private Label eventNameLabel;

    @Override
    public void initialize() throws IOException {
        super.initialize();
        int eventID = (int) FXRouter.getData();
        Datasource<EventList> datasource = new EventListFileDatasource("data", "event-list.json");
        EventList events = datasource.readData();
        Event event = events.findEventByID(eventID);
        eventNameLabel.setText(event.getEventName());
    }

    @FXML
    public void onBackToEventClick(){
        try {
            int eventID = (int) FXRouter.getData();
            FXRouter.goTo("event-description",eventID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
