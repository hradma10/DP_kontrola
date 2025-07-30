package cz.upol.logicgo.controllers.popoverControllers;

import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.misc.enums.HintType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

import static cz.upol.logicgo.misc.Messages.getFormatted;

public class HintPopOverController {

    @FXML
    private HBox hintButtonBox;

    @FXML
    private HBox helpButtonBox;

    private Consumer<HintType> onSelect;

    private MainScreenController mainScreenController;

    public void initialize(MainScreenController mainScreenController, boolean candidateShowActive) {
        this.mainScreenController = mainScreenController;

        Button rowHint = createButton(HintType.CHECK_ROW, "tooltip.hint_type.check_row");
        Button colHint = createButton(HintType.CHECK_COLUMN, "tooltip.hint_type.check_column");
        Button checkCellHint = createButton(HintType.CHECK_CELL, "tooltip.hint_type.check_cell");
        Button checkBoxHint = createButton(HintType.CHECK_BOX, "tooltip.hint_type.check_box");
        Button checkValid = createButton(HintType.CHECK_VALIDITY, "tooltip.hint_type.check_validity");

       /* if (!candidateShowActive){
            Button checkCandidates = createButton(HintType.CHECK_CANDIDATES, "tooltip.hint_type.check_candidates");
            hintButtonBox.getChildren().add(checkCandidates);
        }*/

        hintButtonBox.getChildren().addAll(rowHint, colHint, checkBoxHint, checkCellHint, checkValid);


        Button randomCellHelp = createButton(HintType.RANDOM_CELL, "tooltip.hint_type.random_cell");
        Button chosenCellHelp = createButton(HintType.CHOSEN_CELL, "tooltip.hint_type.chosen_cell");

        helpButtonBox.getChildren().addAll(randomCellHelp, chosenCellHelp);
    }

    private Button createButton(HintType type, String key) {
        Button button = new Button(getFormatted(type.getDescription()));
        button.setOnAction(e -> {
            if (onSelect != null) onSelect.accept(type);
        });
        addTooltip(button, key);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-padding: 6 12; -fx-background-radius: 8;");
        return button;
    }

    private void addTooltip(Node node, String key) {
        node.setOnMouseEntered(_ -> mainScreenController.setTextToLabel(getFormatted(key)));
        node.setOnMouseExited(_ -> mainScreenController.unsetTextToLabel());
    }

    public void setOnSelect(Consumer<HintType> onSelect) {
        this.onSelect = onSelect;
    }
}
