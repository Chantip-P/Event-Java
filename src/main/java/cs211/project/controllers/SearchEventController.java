package cs211.project.controllers;

import cs211.project.controllers.components.EventBannerController;
import cs211.project.models.Event;
import cs211.project.models.EventVisibility;
import cs211.project.models.collections.EventList;
import cs211.project.services.Datasource;
import cs211.project.services.EventListFileDatasource;
import cs211.project.services.FXRouter;
import cs211.project.services.ProgramUtility;
import cs211.project.services.comparators.EventNameComparator;
import cs211.project.services.comparators.EventParticipantComparator;
import cs211.project.services.comparators.EventStartDateTimeComparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Comparator;

public class SearchEventController extends TemplateController{

    @FXML private Label editEventLabel;
    @FXML private TextField searchTextField;
    @FXML private GridPane grid;
    @FXML private ChoiceBox<Comparator<Event>> sortChoiceBox;
    String currentPage;

    @FXML
    public void initialize() throws IOException {
        super.initialize();
        currentPage = (String)FXRouter.getData();
        if(currentPage.equals("search")){ editEventLabel.setVisible(false); }
        setUpChoiceBox();
        showEvents(currentPage,getSearchEditEventList(currentPage),sortChoiceBox.getSelectionModel().getSelectedItem());

        Tooltip.install(sortChoiceBox, new Tooltip("Sort Event by"));
        sortChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue,oldChoice, newChoice) -> onSearchClick());
    }

    public EventList getSearchEditEventList(String searchType) {
        Datasource<EventList> datasource = new EventListFileDatasource("data", "event-list.json");
        EventList events = datasource.readData();
        EventList searchEvents = new EventList();
        for (Event event : events) {
            if(event.checkEventStatus() == 1){ continue; }
            if (searchType.equals("search") && event.getEventVisibility().equals(EventVisibility.PUBLIC)) {
                searchEvents.addEvent(event);
            } else if (searchType.equals("edit") && event.getEventCreatorUserID() == ProgramUtility.getLoggedInUserID()) {
                searchEvents.addEvent(event);
            }
        }

        return searchEvents;
    }

    public void setUpChoiceBox(){
        ObservableList<Comparator<Event>> choices = FXCollections.observableArrayList(
               new EventStartDateTimeComparator(),
                 new EventParticipantComparator(),
               new EventNameComparator()
        );
        sortChoiceBox.getItems().addAll(choices);
        sortChoiceBox.getSelectionModel().selectFirst();
    }

    public void showEvents(String currentPage, EventList events, Comparator<Event> comparator) {
        clearAll(grid);
        events.sort(comparator);

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
            if(currentPage.equals("search")){
                eventBannerController.isSearchBanner(true);}
            if(currentPage.equals("edit")) {
                eventBannerController.isSearchBanner(false);}
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

    public void onSearchClick() {
        clearAll(grid);
        EventList targetEvents = new EventList();
        String inputText = searchTextField.getText().trim();
        EventList events = getSearchEditEventList(currentPage);
        if (events == null) { return; }
        if (inputText.isEmpty()) { showEvents(currentPage,events,sortChoiceBox.getSelectionModel().getSelectedItem()); }
        for (Event event : events) {
            if (event.getEventName().toLowerCase().contains(inputText.toLowerCase())) {
                targetEvents.addEvent(event);
            }
        }
        showEvents(currentPage,targetEvents,sortChoiceBox.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void onSearchByPressEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) { onSearchClick(); }
    }

}
