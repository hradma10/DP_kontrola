package cz.upol.logicgo.handlers.sudoku;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.commands.sudokuCommand.SetSudokuNumberCommand;
import cz.upol.logicgo.controllers.screenControllers.SudokuGameController;
import cz.upol.logicgo.model.games.drawable.bounds.IBounds;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.Cell;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Optional;

import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.changeColorRelatedCells;
import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.clearCellBackgrounds;
import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.animateTo;


public class MouseClickedSudokuHandler extends SudokuHandlerBase {

    public MouseClickedSudokuHandler(SudokuGame sudokuGameState, SudokuGameController sudokuGameController) {
        super(sudokuGameState, sudokuGameController);
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()) {
            case PRIMARY -> {
                if (mouseEvent.getClickCount() == 1) {
                    onSingleClickPrimary(mouseEvent);
                }
            }
            case SECONDARY -> {
                if (mouseEvent.getClickCount() == 1) {
                    onSingleClickSecondary(mouseEvent);
                }
            }
            case null, default -> {
            }
        }

        redrawCanvases(null, true, true);
    }


    private void onSingleClickSecondary(MouseEvent mouseEvent) {
        SudokuGameController con = this.getSudokuGameController();
        boolean activeHint = con.isHintChoice();
        if (activeHint) {
            con.setHintChoice(false);
            con.setActiveHint(null);
            con.setHoveredSudokuCells(null);
        } else {
            clearCellBackgrounds(con.getSudokuGame());
        }
    }

    private void onSingleClickPrimary(MouseEvent mouseEvent) {
        SudokuGameController con = this.getSudokuGameController();
        if (con.isHintChoice()) {
            handleHint(mouseEvent);
            redrawCanvases(null, true, true);
            return;
        }

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        var quadTree = con.getQuadTree();
        var newSelectedCellOptional = quadTree.search(x, y);
        if (newSelectedCellOptional.isPresent()) {
            if (newSelectedCellOptional.get() instanceof SudokuCell newSelectedCell) {
                SudokuCell oldSelectedCell = con.getSelectedSudokuCell();
                if (oldSelectedCell != null) {
                    this.unselectCellOnCanvas(oldSelectedCell);
                    //Cell outlineCell = new Cell(oldSelectedCell.getCenter(), oldSelectedCell.getWidth(), oldSelectedCell.getHeight(), newSelectedCell.getRow(), newSelectedCell.getCol());
                    //con.setSelectedCellOutline(outlineCell);
                    //animateTo(oldSelectedCell, newSelectedCell, outlineCell, getSudokuGameController().getSecondaryCanvas());
                }

                this.selectCellOnCanvas(newSelectedCell);
                con.setSelectedSudokuCell(newSelectedCell);
                changeColorRelatedCells(con.getSudokuGame().getSudoku(), newSelectedCell);
            }
        }
    }

    private void handleHint(MouseEvent mouseEvent) {
        SudokuGameController con = getSudokuGameController();
        ArrayList<SudokuCell> selectedCells = con.getHoveredSudokuCells();

        SudokuGame sudokuGame = con.getSudokuGame();
        Sudoku sudoku = sudokuGame.getSudoku();
        SudokuCell[][] solutionBoard = sudoku.getSolutionBoard();

        clearCellBackgrounds(sudokuGame);
        if (con.getActiveHint() == null) return;
        switch (con.getActiveHint()) {
            case CHOSEN_CELL -> {
                SudokuCell selectedCell = selectedCells.getFirst();
                int row = selectedCell.getRow();
                int col = selectedCell.getCol();
                if (selectedCell.getValue() != 0) {
                    break;
                }
                int value = solutionBoard[row][col].getValue();
                con.getCommandExecutor().execute(new SetSudokuNumberCommand(sudokuGame, row, col, value, 0));
                con.getTabState().setChangePending();
                con.saveGame(false);
            }

            case CHECK_CELL -> {
                SudokuCell selectedCell = selectedCells.getFirst();
                highlightIfIncorrect(solutionBoard, selectedCell);
            }

            case CHECK_COLUMN, CHECK_ROW, CHECK_BOX -> {
                for (SudokuCell sudokuCell : selectedCells) {
                    highlightIfIncorrect(solutionBoard, sudokuCell);
                }
            }
            case CHECK_CANDIDATES -> {
                SudokuCell selectedCell = selectedCells.getFirst();
                showCandidates(selectedCell);
            }
        }

        con.setHintChoice(false);
        con.setActiveHint(null);
    }

    private static void showCandidates(SudokuCell cell){
        cell.setHints(true);
    }


    private static void highlightIfIncorrect(SudokuCell[][] solutionBoard, SudokuCell cell) {
        int row = cell.getRow();
        int col = cell.getCol();

        if (cell.isChangeable() && cell.getValue() != solutionBoard[row][col].getValue()) {
            cell.setBackgroundColor(cell.getValue() == 0 ? Color.CYAN : Color.RED);
        }
    }
}
