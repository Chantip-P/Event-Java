package cs211.project.services;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class TransitionUtility {
   public static void slideVerticalTransition(Node fxmlObject, double fromY, double duration) {
       TranslateTransition translate = new TranslateTransition();
       translate.setNode(fxmlObject);
       translate.setDuration(Duration.millis(duration));
       translate.setFromY(fromY);
       translate.setToY(0);
       translate.play();
   }

    public static void slideHorizontalTransition(Node fxmlObject, double fromX, double duration) {
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(fxmlObject);
        translate.setDuration(Duration.millis(duration));
        translate.setFromX(fromX);
        translate.setToX(0);
        translate.play();
    }

   public static void fadeInTransition(Node fxmlObject, double toOpacity, double duration) {
       FadeTransition fade = new FadeTransition();
       fade.setNode(fxmlObject);
       fade.setDuration(Duration.millis(duration));
       fade.setFromValue(0);
       fade.setToValue(toOpacity);
       fade.play();
   }
}
