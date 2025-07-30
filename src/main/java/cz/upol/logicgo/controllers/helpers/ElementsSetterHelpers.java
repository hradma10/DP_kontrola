package cz.upol.logicgo.controllers.helpers;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.algorithms.sudoku.layout.SudokuLayoutsLoader;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.PreviewType;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.clearCanvas;
import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.strokeBoard;

public class ElementsSetterHelpers {
    public static void setItemsDifficultyChoiceBox(ChoiceBox<Difficulty> difficultyChoiceBox) {
        ObservableList<Difficulty> difficulties = FXCollections.observableArrayList(Difficulty.values());

        difficultyChoiceBox.setItems(difficulties);

        difficultyChoiceBox.getSelectionModel().select(Difficulty.EASY);

        difficultyChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Difficulty difficulty) {
                if (difficulty != null) {
                    return Difficulty.getTranslation(difficulty);
                }

                return "žádné";
            }

            @Override
            public Difficulty fromString(String string) {
                String naming = Difficulty.getNaming(string);
                return Difficulty.getInstanceByDescription(naming);
            }
        });
    }

    public static void setItemsRegionTypeChoiceBox(ChoiceBox<RegionLayout> regionTypeChoiceBox, SudokuType sudokuType, Canvas canvas) {
        ObservableList<RegionLayout> regionLayouts = FXCollections.observableArrayList(sudokuType.getRegionLayouts());

        regionTypeChoiceBox.setItems(regionLayouts);

        regionTypeChoiceBox.getSelectionModel().select(0);

        regionTypeChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(RegionLayout regionLayout) {
                if (regionLayout != null) {
                    return regionLayout.getTranslatedName();
                }

                return "žádné";
            }

            @Override
            public RegionLayout fromString(String string) {
                String revTransName = SudokuLayoutsLoader.getLayoutsTranslated().get(string);
                return SudokuLayoutsLoader.getLayoutsByName(revTransName);
            }
        });

        regionTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal == newVal) return;
            if (newVal == null) return;

            onRegionTypeChange(canvas, sudokuType, regionTypeChoiceBox);
        });
    }

    public static void onRegionTypeChange(Canvas canvas, SudokuType sudokuType, ChoiceBox<RegionLayout> regionTypeChoiceBox) {
        double width = canvas.getWidth() / sudokuType.getGridSize();
        double height = canvas.getHeight() / sudokuType.getGridSize();

        RegionLayout value = regionTypeChoiceBox.getSelectionModel().getSelectedItem();
        clearCanvas(canvas);
        strokeBoard(value, width, height, canvas.getGraphicsContext2D());
    }

    public static void setItemsPreviewTypeChoiceBox(ChoiceBox<PreviewType> choiceExport) {
        ObservableList<PreviewType> previewTypes = FXCollections.observableArrayList(PreviewType.values());

        choiceExport.setItems(previewTypes);

        choiceExport.getSelectionModel().select(PreviewType.UNSOLVED);

        choiceExport.setConverter(new StringConverter<>() {
            @Override
            public String toString(PreviewType pt) {
                if (pt != null) {
                    return pt.getName();
                }

                return "žádné";
            }

            @Override
            public PreviewType fromString(String string) {
                return PreviewType.getInstanceByDescription(string);
            }
        });
    }
}
