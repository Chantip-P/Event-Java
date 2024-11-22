package cs211.project.services;

import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

public class SceneUtility {
    private static AbstractMap<String, String> allStylePath = new HashMap<>();
    private static AbstractMap<Theme, String> allThemePath = new HashMap<>();

    private static Theme currentTheme = Theme.DARK;

    /**
     * Apply styling to all opened scene.
     * @param styleLabel Style label set in {@link #bindStyle(String, String)}
     */
    public static void addStyle(String styleLabel) {
        String stylePath = new Object() { }.getClass().getResource("/" + allStylePath.get(styleLabel)).toExternalForm();
        for (Window w : Stage.getWindows()) {
            w.getScene().getStylesheets().add(stylePath);
        }
    }

    /**
     * Apply an array of styling to all opened scene.
     * @param styleLabels Style labels set in {@link #bindStyle(String, String)}
     */
    public static void addAllStyle(String... styleLabels) {
        for (String s : styleLabels) {
            addStyle(s);
        }
    }

    /**
     * Clear all styling from all opened scene.
     */
    public static void clearStyle() {
        for (Window w : Stage.getWindows()) {
            w.getScene().getStylesheets().clear();
        }
    }

    /**
     * Add a style label to be accessed by SceneUtility.
     * @param styleLabel Style label used to retrieve style file.
     * @param path Path to style (.css) file.
     */
    public static void bindStyle(String styleLabel, String path) {
        allStylePath.put(styleLabel, path);
    }

    public static void bindTheme(Theme theme, String path) {
        allThemePath.put(theme, path);
    }

    public static void updateTheme() {
        ArrayList<String> nonThemeStyle = new ArrayList<>();

        for (Window w : Stage.getWindows()) {
            for (String style : w.getScene().getStylesheets()) {
                if (!isThemeStyle(style) && !nonThemeStyle.contains(style)) {
                    nonThemeStyle.add(style);
                }
            }
        }

        String themePath = new Object() { }.getClass().getResource("/" + allThemePath.get(currentTheme)).toExternalForm();
        nonThemeStyle.add(themePath);

        clearStyle();
        for (Window w : Stage.getWindows()) {
            for (String style : nonThemeStyle) {
                w.getScene().getStylesheets().add(style);
            }
        }
    }

    public static void setCurrentTheme(Theme theme) {
        currentTheme = theme;
        updateTheme();
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    private static boolean isThemeStyle(String externalPath) {
        for (String path : allThemePath.values()) {
            if (externalPath.contains(path)) { return true; }
        }
        return false;
    }
}
