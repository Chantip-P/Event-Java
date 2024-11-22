package cs211.project.controllers.components;

import cs211.project.services.FXRouter;
import cs211.project.services.SceneUtility;
import javafx.fxml.FXML;

import java.io.IOException;

public class NavbarController {
    @FXML
    public void onSearchEventClick() throws IOException {
        FXRouter.goTo("search-event", "search");
    }

    @FXML
    public void onMyEventClick() throws IOException {
        FXRouter.goTo("my-event");
    }

    @FXML
    public void onCreateEventClick() throws IOException {
        FXRouter.goTo("edit-event", -1);
        SceneUtility.addStyle("edit-event");
    }

    @FXML
    public void onEditEventClick() throws IOException {
        FXRouter.goTo("search-event", "edit");
    }

    @FXML
    public void onAboutUsClick() throws IOException {
        FXRouter.goTo("about-us");
    }

    @FXML
    public void onManualClick() throws IOException {
        FXRouter.goTo("manual");
    }
}
