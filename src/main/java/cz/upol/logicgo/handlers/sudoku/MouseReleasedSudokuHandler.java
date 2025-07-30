package cz.upol.logicgo.handlers.sudoku;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.controllers.screenControllers.SudokuGameController;
import javafx.scene.input.MouseEvent;

public class MouseReleasedSudokuHandler extends SudokuHandlerBase {

    public MouseReleasedSudokuHandler(SudokuGame sudokuGame, SudokuGameController sudokuGameController) {
        super(sudokuGame, sudokuGameController);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {

    }
}
