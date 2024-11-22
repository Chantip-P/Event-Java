package cs211.project.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class ManualController extends TemplateController {
    @FXML private VBox manualPageLayout;

    @FXML
    public void initialize() throws IOException {
        super.initialize();
        manualPageLayout.getChildren().clear();

        for (int i = 1; i <= 27; i++) {
            ImagePattern manualPage = new ImagePattern(new Image(getClass().getResourceAsStream(
                                                 "/images/manual-page-" + i + ".png")));
            Rectangle manualView = new Rectangle(700, 906, manualPage);
            manualPageLayout.getChildren().add(manualView);
        }

    }

}
