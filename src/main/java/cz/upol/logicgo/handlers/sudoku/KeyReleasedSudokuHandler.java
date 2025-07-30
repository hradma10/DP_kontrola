package cz.upol.logicgo.handlers.sudoku;


import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.controllers.screenControllers.SudokuGameController;
import javafx.scene.input.KeyEvent;

import java.util.Arrays;

public class KeyReleasedSudokuHandler extends SudokuHandlerBase {


    public KeyReleasedSudokuHandler(SudokuGame sudokuGameState, SudokuGameController sudokuGameController) {
        super(sudokuGameState, sudokuGameController);
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        if (Arrays.stream(getSupportedKeys()).anyMatch(key -> key.equals(keyEvent.getCode()))) {
            this.setKeyPressed(keyEvent, false);
        }
    }

}
