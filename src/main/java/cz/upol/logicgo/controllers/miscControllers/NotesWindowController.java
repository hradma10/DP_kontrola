package cz.upol.logicgo.controllers.miscControllers;

import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.windows.AlertBox;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.dao.GameDAO;
import cz.upol.logicgo.model.games.dao.SettingDAO;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.setting.UserSetting;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;
import org.jetbrains.annotations.TestOnly;

import java.net.URL;
import java.util.*;

import static cz.upol.logicgo.misc.enums.settings.UserSettings.NOTES_FONT_NAME;
import static cz.upol.logicgo.misc.enums.settings.UserSettings.NOTES_FONT_SIZE;

public class NotesWindowController implements Initializable {
    private final static KeyCode[] supportedKeys;
    private static final Map<KeyCode, Boolean> supportedKeyMap = new HashMap<>();

    static {
        supportedKeys = new KeyCode[]{KeyCode.S, KeyCode.F};
    }

    final private GameDAO gameDAO;
    final private SettingDAO settingDAO;
    @FXML
    public TextArea textArea;
    @FXML
    public Button buttonCancel;
    @FXML
    public Button buttonSave;
    Stage stage;
    boolean change;
    private Game game;

    public NotesWindowController() {
        this.gameDAO = new GameDAO();
        this.settingDAO = new SettingDAO();
    }

    @TestOnly
    public NotesWindowController(GameDAO gameDAO, SettingDAO settingDAO) {
        this.gameDAO = gameDAO;
        this.settingDAO = settingDAO;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initialize(Game game) {
        setRequests();
        setWindowSizes();
        this.game = game;
        String notes = game.getNotes();
        change = false;
        textArea.setText(Objects.requireNonNullElse(notes, ""));
        textArea.textProperty().addListener((_, _, _) -> {
            change = true;
        });
    }

    private void setWindowSizes(){
        stage.setMinWidth(400);
        stage.setMaxWidth(800);

        stage.setMinHeight(300);
        stage.setMaxHeight(600);
    }

    @FXML
    public void save() {
        if (change) {
            String notes = textArea.getText();
            game.setNotes(notes);
            gameDAO.updateGameNotes(game.getId(), notes);
            change = false;
        }
    }

    private void setRequests() {
        this.getStage().setOnCloseRequest(event -> {
            if (!onCloseRequest()) {
                event.consume();
            }
        });
    }

    public boolean onCloseRequest() {
        if (!change) return true;

        Optional<Boolean> value = AlertBox.saveAlertNotes();
        if (value.isPresent()) {
            if (value.get()) {
                String text = textArea.getText();
                game.setNotes(text);
                gameDAO.updateGameNotes(game.getId(), text);
            }
            return true;
        }

        return false;
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if (!supportedKeyMap.containsKey(keyEvent.getCode())) return;
        if (isKeyPressed(keyEvent)) return;
        setKeyPressed(keyEvent, true);

        switch (keyEvent.getCode()) {
            case S -> {
                if (keyEvent.isControlDown()) {
                    save();
                }
            }
            case F -> {
                if (keyEvent.isControlDown() && keyEvent.isShiftDown()) {
                    changeFont();
                }
            }
        }

    }

    public void setKeyPressed(KeyEvent keyEvent, boolean state) {
        supportedKeyMap.put(keyEvent.getCode(), state);
    }

    public boolean isKeyPressed(KeyEvent keyEvent) {
        return supportedKeyMap.get(keyEvent.getCode());
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        if (Arrays.stream(supportedKeys).anyMatch(key -> key.equals(keyEvent.getCode()))) {
            setKeyPressed(keyEvent, false);
        }
    }

    @FXML
    public void changeFont() {
        HashMap<SettingKey, UserSetting> settings = settingDAO.getUserSettings(game.getPlayer(), NOTES_FONT_NAME, NOTES_FONT_SIZE);
        Setting fontNameSetting = settings.get(NOTES_FONT_NAME);
        Setting fontSizeSetting = settings.get(NOTES_FONT_SIZE);
        Font font = Font.font(fontNameSetting.getValue(), Double.parseDouble(fontSizeSetting.getValue()));
        Optional<Font> newFontOptional = openFontSelectorDialog(font);
        if (newFontOptional.isPresent()) {
            Font newFont = newFontOptional.get();
            if (!font.equals(newFont)) {
                textArea.setFont(newFont);
                fontNameSetting.setValue(newFont.getName());
                fontSizeSetting.setValue(String.valueOf(newFont.getSize()));
                settingDAO.saveAll(fontNameSetting, fontSizeSetting);
            }
        }

    }

    public Optional<Font> openFontSelectorDialog(Font font) {
        return new FontSelectorDialog(font).showAndWait();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
