package cz.upol.logicgo.handlers.sudoku;


import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.algorithms.sudoku.SudokuUtils;
import cz.upol.logicgo.commands.sudokuCommand.SetSudokuNumberCommand;
import cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions;
import cz.upol.logicgo.controllers.screenControllers.SudokuGameController;
import cz.upol.logicgo.misc.enums.HintType;
import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.Cell;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.*;
import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.redrawCanvas;
import static cz.upol.logicgo.misc.Helper.getSetting;
import static cz.upol.logicgo.misc.enums.settings.SudokuSettings.CELL_NOTES;
import static javafx.scene.input.KeyCode.valueOf;

public class KeyPressedSudokuHandler extends SudokuHandlerBase {

    private static final Map<KeyCode, Integer> keysToNumber = new HashMap<>();

    static {
        IntStream.rangeClosed(0, 9).forEach(i -> {
            keysToNumber.put(valueOf("NUMPAD" + i), i);
            keysToNumber.put(valueOf("DIGIT" + i), i);
        });
        keysToNumber.put(KeyCode.A, 10);
        keysToNumber.put(KeyCode.B, 11);
        keysToNumber.put(KeyCode.C, 12);
        keysToNumber.put(KeyCode.D, 13);
        keysToNumber.put(KeyCode.E, 14);
        keysToNumber.put(KeyCode.F, 15);
        keysToNumber.put(KeyCode.G, 16);

        keysToNumber.put(KeyCode.DELETE, 0);
        keysToNumber.put(KeyCode.BACK_SPACE, 0);
    }

    public KeyPressedSudokuHandler(SudokuGame sudokuGame, SudokuGameController sudokuGameController) {
        super(sudokuGame, sudokuGameController);
    }

    public static Map<KeyCode, Integer> getKeysToNumber() {
        return keysToNumber;
    }


    public void onKeyPressed(KeyEvent keyEvent) {

        if (this.isKeyPressed(keyEvent)) return;
        boolean forceRedraw = false;
        var con = this.getSudokuGameController();
        var sudokuGame = this.getSudokuGame();
        SudokuCell selectedCell = null;

        if (keysToNumber.containsKey(keyEvent.getCode())) {
            var newNumber = keysToNumber.get(keyEvent.getCode());
            if (!sudokuGame.getSudoku().getType().isSupported(newNumber)) return;
            selectedCell = con.getSelectedSudokuCell();
            if (selectedCell == null || !selectedCell.isChangeable()) return;
            int row = selectedCell.getRow();
            int col = selectedCell.getCol();
            int oldNumber = selectedCell.getValue();
            con.getCommandExecutor().execute(new SetSudokuNumberCommand(sudokuGame, row, col, newNumber, oldNumber));
            con.getTabState().setChangePending();
            con.saveGame(false);
            getSudokuGameController().setCandidates();
        }

        if (keyEvent.isControlDown()) {
            var commandExecutor = con.getCommandExecutor();
            switch (keyEvent.getCode()) {
                case Z -> commandExecutor.undo();
                case Y -> commandExecutor.redo();
                case S -> getSudokuGameController().saveGame(false);
            }
        }

        switch (keyEvent.getCode()) {
            case ESCAPE -> {
                boolean isHintBeingChosen = con.isHintChoice();
                if (isHintBeingChosen) {
                    con.setHintChoice(false);
                    con.setActiveHint(null);
                    con.setHoveredSudokuCells(null);
                } else {
                    Map<SettingKey, String> settings = SudokuUtils.getSettingsAsMap(sudokuGame.getSudoku().getSettings());

                    boolean cellNotesOn = getSetting(CELL_NOTES, settings);

                    if (con.getActiveHint() == HintType.CHECK_CANDIDATES && cellNotesOn) {
                        clearCellHints(sudokuGame);
                    } else {
                        clearCellBackgrounds(sudokuGame);
                    }

                }
                forceRedraw = true;
            }
        }

        selectedCell = switch (keyEvent.getCode()) {
            case UP, DOWN, LEFT, RIGHT -> {
                int dRow = 0, dCol = 0;
                switch (keyEvent.getCode()) {
                    case UP -> dRow = -1;
                    case DOWN -> dRow = 1;
                    case LEFT -> dCol = -1;
                    case RIGHT -> dCol = 1;
                }
                var cell = changeSelectedSudokuCell(dRow, dCol);
                changeColorRelatedCells(con.getSudokuGame().getSudoku(), cell);
                yield cell;
            }
            default -> selectedCell;
        };

        if (forceRedraw || selectedCell != null) {
            redrawCanvases(selectedCell, true, true);
        }

        if (SudokuUtils.checkFinishedSudoku(sudokuGame)){
            con.gameFinished();
        }
        keyEvent.consume();
    }



    private SudokuCell changeSelectedSudokuCell(int rowInc, int colInc) {
        SudokuGameController con = getSudokuGameController();
        SudokuCell oldSelectedCell = con.getSelectedSudokuCell();
        SudokuCell newSelectedCell;
        Sudoku sudoku = this.getSudokuGame().getSudoku();
        if (oldSelectedCell == null) {
            newSelectedCell = sudoku.getSudokuCell(0, 0);
        } else {
            int gridSize = sudoku.getType().getGridSize();
            int row = ((oldSelectedCell.getRow() + rowInc + gridSize) % gridSize);
            int col = ((oldSelectedCell.getCol() + colInc + gridSize) % gridSize);
            unselectCellOnCanvas(oldSelectedCell);
            newSelectedCell = sudoku.getSudokuCell(row, col);

            //Cell cell = new Cell(oldSelectedCell.getCenter(), oldSelectedCell.getWidth(), oldSelectedCell.getHeight(), row, col);
            //Canvas secondaryCanvas = getSudokuGameController().getSecondaryCanvas();

            //con.setSelectedCellOutline(cell);
            //animateTo(oldSelectedCell, newSelectedCell, cell, secondaryCanvas);
        }

        if (newSelectedCell == null) return null;

        this.selectCellOnCanvas(newSelectedCell);
        con.setSelectedSudokuCell(newSelectedCell);
        return newSelectedCell;
    }

}