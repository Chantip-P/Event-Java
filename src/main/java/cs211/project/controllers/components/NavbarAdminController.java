package cs211.project.controllers.components;

import cs211.project.services.FXRouter;
import javafx.fxml.FXML;

import java.io.IOException;

public class NavbarAdminController {
    @FXML
    public void onUserLogClick() throws IOException {
        FXRouter.goTo("user-log");
    }
}
