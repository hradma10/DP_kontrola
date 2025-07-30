package cz.upol.logicgo;

import cz.upol.logicgo.controllers.screenControllers.LoginController;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * spustí aplikaci
 */
public class StartOfApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        CSSFX.start(); // test
        FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource("/cz/upol/logicgo/windows/login_screen.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();

        Scene scene = new Scene(root, 800, 600);
        controller.setStage(stage);
        controller.initialize();

        stage.setTitle("LogicGo");
        stage.setScene(scene);

        stage.centerOnScreen();
        stage.setMinWidth(900);
        stage.setMinHeight(700);

        stage.show();
    }

}