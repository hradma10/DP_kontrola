package cz.upol.logicgo.controllers.gameChoiceControllers;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.misc.enums.*;
import cz.upol.logicgo.misc.enums.settings.SudokuSettings;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.helperInit.GameInit;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.util.generate.SeedFactory;
import cz.upol.logicgo.util.generate.SeedFactory.ParsedSeed;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;

import static cz.upol.logicgo.controllers.helpers.ElementsSetterHelpers.*;
import static cz.upol.logicgo.misc.Messages.getFormatted;

public class SudokuSettingsMenu {

    public CheckBox hintsOnCheckBox;
    public ComboBox<Object> hintTypeComboBox;
    public CheckBox hintTypeCheckBox;
    public CheckBox cellNotesCheckBox;
    public CheckBox timerCheckBox;
    public TextField seedTextField;
    public Button okButton;
    public Button cancelButton;
    public Label seedLabel;
    public Label difficultyLabel;
    public ChoiceBox<Difficulty> difficultyChoiceBox;
    public ChoiceBox<RegionLayout> regionLayoutChoiceBox;
    public Label timerLabel;
    public Label cellNotesLabel;
    public Label hintTypeChoiceLabel;
    public Label hintTypeLabel;
    public Label hintsOnLabel;
    public Label regionLabel;
    public Pane pane;
    @FXML
    Canvas canvas;

    User user;

    MainScreenController mainController;

    SudokuType sudokuType;




    private void setUpLabels(){
        difficultyLabel.setText(getFormatted("sudokuSettings.difficulty"));
        timerLabel.setText(getFormatted("sudokuSettings.timer"));
        cellNotesLabel.setText(getFormatted("sudokuSettings.cellNotes"));
        hintsOnLabel.setText(getFormatted("sudokuSettings.hintsOn"));
        hintTypeChoiceLabel.setText(getFormatted("sudokuSettings.hintTypeChoice"));
        hintTypeLabel.setText(getFormatted("sudokuSettings.hintType"));
        okButton.setText(getFormatted("playButton"));
        cancelButton.setText(getFormatted("cancelButton"));
        seedLabel.setText(getFormatted("sudokuSettings.seed"));
        regionLabel.setText(getFormatted("sudokuSettings.region"));

    }

    public void initialize(User user, MainScreenController mainController, GameInit gameInit) {
        this.user = user;
        this.mainController = mainController;
        this.sudokuType = (SudokuType) gameInit.getGameType();

        setUpLabels();

        var defaultSettings = user.getSettingsAsMap();

        hintsOnCheckBox.setSelected(Boolean.parseBoolean(defaultSettings.get(SudokuSettings.HINTS_ON)));
        cellNotesCheckBox.setSelected(Boolean.parseBoolean(defaultSettings.get(SudokuSettings.CELL_NOTES)));
        timerCheckBox.setSelected(Boolean.parseBoolean(defaultSettings.get(SudokuSettings.TIMER)));
        hintTypeCheckBox.setSelected(Boolean.parseBoolean(defaultSettings.get(SudokuSettings.HINT_TYPE)));

        if (sudokuType != null){
            seedLabel.setDisable(true);
            seedLabel.setVisible(false);
            seedTextField.setDisable(true);
            seedTextField.setVisible(false);
        }

        ObservableList<Object> hintTypeList = FXCollections.observableArrayList();

        hintTypeCheckBox.setDisable(!hintsOnCheckBox.isSelected());

        hintTypeComboBox.setDisable(!hintTypeCheckBox.isSelected());

        hintTypeList.add(new GroupLabel("Doplňování"));
        hintTypeList.add(HintType.RANDOM_CELL);
        hintTypeList.add(HintType.CHOSEN_CELL);

        hintTypeList.add(new GroupLabel("Kontrola"));
        hintTypeList.add(HintType.CHECK_CELL);
        hintTypeList.add(HintType.CHECK_ROW);
        hintTypeList.add(HintType.CHECK_COLUMN);
        //hintTypeList.add(HintType.CHECK_CANDIDATES);
        hintTypeList.add(HintType.CHECK_BOX);
        hintTypeList.add(HintType.CHECK_VALIDITY);



        hintTypeComboBox.setItems(hintTypeList);

        hintTypeComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                } else if (item instanceof GroupLabel group) {
                    setText(group.name());
                    setStyle("-fx-font-weight: bold; -fx-background-color: #7b98b5;");
                    setDisable(true);
                } else if (item instanceof HintType hint) {
                    setText("  " + HintType.getTranslation(hint));
                    setStyle("");
                    setDisable(false);
                }
            }
        });

        hintTypeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item instanceof GroupLabel) {
                    setText(null);
                } else if (item instanceof HintType hint) {
                    setText(HintType.getTranslation(hint));
                }
            }
        });

        hintTypeComboBox.getSelectionModel().select(HintType.CHECK_CELL);

        hintsOnCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            hintTypeCheckBox.setDisable(!newValue);
        });

        hintTypeCheckBox.disabledProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                hintTypeComboBox.setDisable(true);
            }else {
                hintTypeComboBox.setDisable(!hintTypeCheckBox.isSelected());

            }
        });

        hintTypeCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            hintTypeComboBox.setDisable(!newValue);
        });

        hintTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Object obj) {
                if (obj instanceof HintType hintType) {
                    return HintType.getTranslation(hintType);
                } else if (obj instanceof GroupLabel(String name)) {
                    return name;
                }
                return "žádné";
            }

            @Override
            public Object fromString(String string) {
                String naming = HintType.getNaming(string);
                return HintType.getInstanceByDescription(naming);
            }
        });
        if (sudokuType != null){
            setItemsDifficultyChoiceBox(difficultyChoiceBox);
            setItemsRegionTypeChoiceBox(regionLayoutChoiceBox, sudokuType, canvas);
            onRegionTypeChange(canvas, sudokuType, regionLayoutChoiceBox);

        } else {
            regionLayoutChoiceBox.setDisable(true);
            regionLayoutChoiceBox.setVisible(false);
            regionLayoutChoiceBox.setManaged(false);
            regionLabel.setDisable(true);
            regionLabel.setVisible(false);
            regionLabel.setManaged(false);
            difficultyChoiceBox.setDisable(true);
            difficultyChoiceBox.setVisible(false);
            difficultyChoiceBox.setManaged(false);
            difficultyLabel.setDisable(true);
            difficultyLabel.setVisible(false);
            difficultyLabel.setManaged(false);
            pane.setVisible(false);
            canvas.setVisible(false);
        }

        if (gameInit.isUse()){
            seedTextField.setText(String.valueOf(gameInit.getSeed()));
            regionLayoutChoiceBox.setDisable(true);
            difficultyChoiceBox.setDisable(true);
        }

    }

    public record GroupLabel(String name) {
        @Override public String toString() {
            return name;
        }
    }


    public void handleCancel() {
        mainController.closeCurrentTab();
    }

    public void handleOk() throws IOException {
        GameInit gameInit = new GameInit(user).setGameType(sudokuType);

        gameInit.addSetting(SudokuSettings.HINTS_ON, String.valueOf(hintsOnCheckBox.isSelected()));
        gameInit.addSetting(SudokuSettings.CELL_NOTES, String.valueOf(cellNotesCheckBox.isSelected()));
        gameInit.addSetting(SudokuSettings.TIMER, String.valueOf(timerCheckBox.isSelected()));
        gameInit.addSetting(SudokuSettings.HINT_TYPE, String.valueOf(hintTypeCheckBox.isSelected()));
        gameInit.addSetting(SudokuSettings.HINT_TYPE_CHOICE, ((HintType)hintTypeComboBox.getSelectionModel().getSelectedItem()).getDescription());

        gameInit.setDifficulty(difficultyChoiceBox.getSelectionModel().getSelectedItem());

        gameInit.setRegionLayout(regionLayoutChoiceBox.getSelectionModel().getSelectedItem());

        if (sudokuType == null || !seedTextField.getText().isEmpty()){
            ParsedSeed parsedSeed = SeedFactory.parseSeed(seedTextField.getText());
            gameInit.setSeed(parsedSeed.seed());
            gameInit.setGameType(parsedSeed.sudokuType());
            gameInit.setDifficulty(parsedSeed.difficulty());
            gameInit.setRegionLayout(parsedSeed.regionLayout());
        }
        ProgressIndicator spinner = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        spinner.setPrefSize(16, 16);
        okButton.setGraphic(spinner);
        okButton.setText(null);

        PauseTransition shortPause = new PauseTransition(Duration.millis(100));
        shortPause.setOnFinished(e -> {
            try {
                mainController.closeCurrentAndReplace(TabType.SUDOKU, gameInit);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        shortPause.play();

    }



}
