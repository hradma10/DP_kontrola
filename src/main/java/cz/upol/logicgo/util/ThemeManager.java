package cz.upol.logicgo.util;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.scene.Scene;

public class ThemeManager {

    private static boolean darkTheme = false;

    public static void applyInitialTheme(Scene scene) {
        scene.getStylesheets().add(new PrimerLight().getUserAgentStylesheet());
    }

    public static void toggleTheme(Scene scene) {
        scene.getStylesheets().removeIf(s -> s.contains("/atlantafx/"));

        if (darkTheme) {
            scene.getStylesheets().add(new PrimerLight().getUserAgentStylesheet());
        } else {
            scene.getStylesheets().add(new PrimerDark().getUserAgentStylesheet());
        }

        darkTheme = !darkTheme;
    }

    public static boolean isDarkThemeActive() {
        return darkTheme;
    }
}
