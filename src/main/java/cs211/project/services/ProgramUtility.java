package cs211.project.services;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ProgramUtility {
    private static Integer loggedInUserID = null;
    private static ArrayList<Stage> allOpenedStages = new ArrayList<>();

    public static boolean login(String username, String password) {
        FileDatasource<UserList> userFileDatasource = new UserListFileDatasource("data", "user-list.json");
        UserList allUser = userFileDatasource.readData();
        User loggedInUser = allUser.login(username, password);

        if (loggedInUser != null) {
            loggedInUserID = loggedInUser.getID();
            userFileDatasource.writeData(allUser);
        }

        return loggedInUser != null;
    }

    public static void logout() {
        loggedInUserID = null;
    }

    public static int getLoggedInUserID() { return loggedInUserID; }

    public static String tempUserCSV(User user) {
        return user.getUsername() + ',' + user.getFirstName() + ',' +
                user.getLastName() + ',' + user.getProfileImagePath(false);
    }
    
    public static User createUserFromTempUserCSV(String tempString) {
        String[] temp = tempString.split(",");
        String tempUsername = temp[0].trim();
        String tempFirstName = temp[1].trim();
        String tempLastName = temp[2].trim();
        String tempImagePath = temp[3].trim();
        return new User(tempUsername, tempFirstName, tempLastName, tempImagePath);
    }

    public static Stage openNewWindow(FXMLLoader loader) throws IOException {
        Parent loaderRoot = loader.load();

        Stage newStage = new Stage();
        newStage.setResizable(false);
        newStage.setScene(new Scene(loaderRoot));
        newStage.show();

        newStage.setOnCloseRequest(windowEvent -> allOpenedStages.remove(newStage));

        allOpenedStages.add(newStage);

        SceneUtility.updateTheme();

        return newStage;
    }

    public static void closeAllWindows() {
        for (Stage s : allOpenedStages) { s.close(); }
        allOpenedStages.clear();
    }
}
