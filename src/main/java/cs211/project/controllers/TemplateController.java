package cs211.project.controllers;

import cs211.project.models.UserType;
import cs211.project.services.ProgramUtility;
import cs211.project.services.UserListFileDatasource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class TemplateController {
    @FXML GridPane gridPane;

    @FXML
    public void initialize() throws IOException {
        String navBarPath = new UserListFileDatasource("data", "user-list.json")
                .readData().findUserByID(ProgramUtility.getLoggedInUserID()).getType() == UserType.ADMIN ?
                "/cs211/project/components/navbar-admin.fxml" :
                "/cs211/project/components/navbar.fxml";

        FXMLLoader navbar = new FXMLLoader(getClass().getResource(navBarPath));
        gridPane.add(navbar.load(), 0, 1);

        FXMLLoader topbar = new FXMLLoader(getClass().getResource("/cs211/project/components/topbar.fxml"));
        gridPane.add(topbar.load(), 1, 0);

        FXMLLoader logoBox = new FXMLLoader(getClass().getResource("/cs211/project/components/logo-box.fxml"));
        gridPane.add(logoBox.load(), 0, 0);
    }
}
