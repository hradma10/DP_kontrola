package cz.upol.logicgo.controllers.screenControllers;

import cz.upol.logicgo.StartOfApp;
import cz.upol.logicgo.exceptions.database.UserErrorException;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.model.games.service.UserService;
import cz.upol.logicgo.util.JpaUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    public PasswordField passwordFieldUserLogin;
    @FXML
    public TextField textFieldUsernameLogin;
    @FXML
    public Button loginUserButton;
    @FXML
    public Button closeAppButton;
    @FXML
    public Button registerUserButton;
    public Button anonButton;
    Stage stage;

    UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initialize() {
        JpaUtil.getEntityManager();
    }

    public void loginAnon() {
        User anon = userService.getAnon();
        try {
            openMainScreen(anon);
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException | ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }


    public void loginUser() {
        String username = textFieldUsernameLogin.getText();
        String password = passwordFieldUserLogin.getText();
        try {
            var optionalUser = userService.login(username, password);
            if (optionalUser.isPresent()) {
                openMainScreen(optionalUser.get());
            }
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException | ParserConfigurationException | TransformerException | UserErrorException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    public void openMainScreen(User loggedInUser) throws IOException, SQLException, InterruptedException, ParserConfigurationException, TransformerException {
        FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource("/cz/upol/logicgo/windows/main_screen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.getController();
        Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight(), false);
        stage.setMinWidth(900);
        stage.setMinHeight(700);
        stage.setTitle("LogicGo");
        controller.setStage(stage);
        controller.initialize(loggedInUser);
        stage.setTitle("");
        stage.setScene(scene);
    }

    @FXML
    public void openRegisterWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource("/cz/upol/logicgo/windows/register_screen.fxml"));
        Parent root = loader.load();
        RegisterController controller = loader.getController();
        Scene scene = new Scene(root, 800, 500, false);
        var registerStage = new Stage();
        controller.setStage(registerStage);
        stage.setTitle("Registrace");
        controller.initialize();
        registerStage.setTitle("Vytvořit účet");
        registerStage.setScene(scene);
        registerStage.showAndWait();
    }

    public void closeApp() {
        stage.close();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
