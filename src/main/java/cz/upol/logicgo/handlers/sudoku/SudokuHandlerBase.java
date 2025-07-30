package cz.upol.logicgo.handlers.sudoku;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions;
import cz.upol.logicgo.controllers.screenControllers.SudokuGameController;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static javafx.scene.input.KeyCode.*;

public class SudokuHandlerBase {

    private final static KeyCode[] supportedKeys;
    private static final Map<KeyCode, Boolean> supportedKeyMap = new HashMap<>();

    static {
        supportedKeys = new KeyCode[]{
                // movement - keys
                W, S, A, D,

                // movement - arrows
                KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT,

                KeyCode.Z,           // undo
                KeyCode.Y,           // redo
                KeyCode.H,           // hint
                KeyCode.E,           // export
                KeyCode.S,           // save
                KeyCode.DELETE,      // delete number

                // numbers - numpad
                NUMPAD0, NUMPAD1, NUMPAD2, NUMPAD3, NUMPAD4,
                NUMPAD5, NUMPAD6, NUMPAD7, NUMPAD8, NUMPAD9,

                // number keys
                DIGIT0, DIGIT1, DIGIT2, DIGIT3, DIGIT4,
                DIGIT5, DIGIT6, DIGIT7, DIGIT8, DIGIT9
        };
    }

   final private SudokuGame sudokuGame;
    final private SudokuGameController sudokuGameController;

    public SudokuHandlerBase(SudokuGame sudokuGame, SudokuGameController sudokuGameController) {
        this.sudokuGame = sudokuGame;
        this.sudokuGameController = sudokuGameController;
        Arrays.stream(supportedKeys).forEach(keyCode -> supportedKeyMap.put(keyCode, false));
    }

    public static KeyCode[] getSupportedKeys() {
        return supportedKeys;
    }

    static Map<KeyCode, Boolean> getSupportedKeyMap() {
        return supportedKeyMap;
    }

    public boolean isKeyPressed(KeyEvent keyEvent) {
        return supportedKeyMap.getOrDefault(keyEvent.getCode(), false);
    }

    public void setKeyPressed(KeyEvent keyEvent, boolean state) {
        supportedKeyMap.put(keyEvent.getCode(), state);
    }

    public SudokuGame getSudokuGame() {
        return sudokuGame;
    }

    public SudokuGameController getSudokuGameController() {
        return sudokuGameController;
    }

    public void selectCellOnCanvas(SudokuCell cell) {
        cell.setDrawToPrimary(false);
        cell.setSelected(true);
    }


    public void unselectCellOnCanvas(SudokuCell cell) {
        cell.setDrawToPrimary(true);
        cell.setSelected(false);
    }

    public void redrawCanvases(SudokuCell selectedCell, boolean drawPrimary, boolean drawSecondary){
        SudokuGameController con = getSudokuGameController();

        if (selectedCell == null) selectedCell = con.getSelectedSudokuCell();

        sudokuGameController.setCandidates();

        if (drawPrimary) {
            Canvas mainCanvas = con.getPrimaryCanvas();
            RedrawCanvasFunctions.redrawCanvas(mainCanvas, sudokuGame.getSudoku());
        }

        if (drawSecondary) {
            Canvas secondaryCanvas = con.getSecondaryCanvas();
            if (selectedCell != null){
                //System.out.println(selectedCell.getCol() + "," + selectedCell.getRow());
                RedrawCanvasFunctions.redrawCanvas(secondaryCanvas, false, selectedCell);
            }else {
                RedrawCanvasFunctions.redrawCanvas(secondaryCanvas, false, selectedCell);
            }

        }
    }


}
