package cz.upol.logicgo.controllers.screenControllers;

import cz.upol.logicgo.exceptions.LogicGoException;
import cz.upol.logicgo.exceptions.database.UserExistsException;
import cz.upol.logicgo.misc.Messages;
import cz.upol.logicgo.misc.Validators;
import cz.upol.logicgo.misc.windows.AlertBox;
import cz.upol.logicgo.model.games.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    public PasswordField passwordFieldUser;
    @FXML
    public TextField textFieldUsername;
    @FXML
    public Button createUserButton;
    @FXML
    public Button cancelCreateUser;
    @FXML
    public Label passwordRuleLabel;

    public Stage stage;

    public UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initialize() {
        initializeTooltips();
    }

    public void initializeTooltips() {
        Tooltip tooltip = new Tooltip(Messages.getFormatted("register.password.tooltip"));
        Tooltip.install(passwordRuleLabel, tooltip);
    }

    @FXML
    public void onCreateUserButtonClicked() {
        var username = textFieldUsername.getText();
        var password = passwordFieldUser.getText();
        try {
            userService.register(username, password);
            stage.close();
        } catch (UserExistsException e) {
            AlertBox.initExistingUser(username);
        } catch (LogicGoException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onCancelCreateUserClicked() {
        stage.close();
    }


    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
