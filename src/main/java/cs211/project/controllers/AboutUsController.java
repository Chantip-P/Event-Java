package cs211.project.controllers;

import cs211.project.controllers.components.AboutUsProfileController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class AboutUsController extends TemplateController {
    @FXML HBox profileLayout;

    @Override
    public void initialize() throws IOException {
        super.initialize();
        profileLayout.getChildren().clear();

        String[] ourNames = {"Chantip\nPiriyabanjurd", "Tanapon\nThaiyanusorn",
                            "Preuk\nRattana-amornkul", "Zharut\nJongsatitpaiboon"};
        String[] ourIDs = {"6510405369", "6510405547", "6510405687", "6510405831"};

        for (int i = 0; i < 4; i++) {
            String profileImageName = "/images/profile-" + ourNames[i].split("\\s")[0].toLowerCase() + ".png";
            Image profileImage = new Image(getClass().getResourceAsStream(profileImageName));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs211/project/components/about-us-profile.fxml"));
            VBox profile = loader.load();
            AboutUsProfileController controller = loader.getController();
            controller.setData(profileImage, ourNames[i], ourIDs[i]);
            profileLayout.getChildren().add(profile);
        }
    }

}
