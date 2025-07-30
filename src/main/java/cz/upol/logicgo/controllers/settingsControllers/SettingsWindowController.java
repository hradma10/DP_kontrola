package cz.upol.logicgo.controllers.settingsControllers;

import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.model.games.dao.SettingDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SettingsWindowController implements Initializable {

    private SettingDAO settingDAO;

    private MainScreenController mainScreenController;
    Tab settingsTab;
    HashMap<SettingKey, Object> settings = new HashMap<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initialize(MainScreenController mainScreenController) {
        initialize(mainScreenController, new SettingDAO());
    }

    public void initialize(MainScreenController mainScreenController, SettingDAO settingDAO) {
        this.mainScreenController = mainScreenController;
        this.settingDAO = settingDAO;
    }

    @FXML
    public void onOKClick() {
        applySettings();
        //settingsTab.close();
    }


    public void applySettings() {

    }


    @FXML
    public void onCancelClick() {
        //stage.close();
    }
}
