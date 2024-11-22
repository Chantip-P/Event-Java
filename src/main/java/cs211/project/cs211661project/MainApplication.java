package cs211.project.cs211661project;

import cs211.project.services.SceneUtility;
import cs211.project.services.Theme;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import cs211.project.services.FXRouter;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        configRoute();
        configStyleRoute();

        FXRouter.bind(this, stage);
        stage.setTitle("OK Event");
        stage.setResizable(false);

        stage.setOnCloseRequest(windowEvent -> Platform.exit());

        FXRouter.goTo("login");
        SceneUtility.setCurrentTheme(Theme.LIGHT);
    }

    private static void configRoute() {
        String resourcesPath = "cs211/project/views/";

        FXRouter.when("login", resourcesPath + "login.fxml");
        FXRouter.when("sign-up-first", resourcesPath + "sign-up-1.fxml");
        FXRouter.when("sign-up-last", resourcesPath + "sign-up-2.fxml");

        FXRouter.when("search-event", resourcesPath + "search-event.fxml");
        FXRouter.when("event-description", resourcesPath + "event-description.fxml");
        FXRouter.when("join-team", resourcesPath + "join-team.fxml");
        FXRouter.when("join-event-success", resourcesPath + "join-event-success.fxml");
        FXRouter.when("my-event", resourcesPath + "my-event.fxml");
        FXRouter.when("schedule", resourcesPath + "schedule.fxml");

        FXRouter.when("edit-event", resourcesPath + "edit-event.fxml");

        FXRouter.when("about-us", resourcesPath + "about-us.fxml");
        FXRouter.when("manual", resourcesPath + "manual.fxml");

        FXRouter.when("settings", resourcesPath + "settings.fxml");
        FXRouter.when("user-log", resourcesPath + "user-log.fxml");
    }

    private static void configStyleRoute() {
        String resourcesPath = "cs211/project/styles/";
        SceneUtility.bindStyle("default-cursor", resourcesPath + "default-cursor.css");
        SceneUtility.bindStyle("default-style", resourcesPath + "default-style.css");
        SceneUtility.bindStyle("edit-event", resourcesPath + "edit-event-style.css");
        SceneUtility.bindStyle("tableview-style", resourcesPath + "tableview-style.css");

        SceneUtility.bindTheme(Theme.LIGHT, resourcesPath + "light-mode.css");
        SceneUtility.bindTheme(Theme.DARK, resourcesPath + "dark-mode.css");
    }

    public static void main(String[] args) {
        launch();
    }
}