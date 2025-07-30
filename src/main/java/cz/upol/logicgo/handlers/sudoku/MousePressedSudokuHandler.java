package cz.upol.logicgo.handlers.sudoku;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.controllers.screenControllers.SudokuGameController;
import cz.upol.logicgo.model.games.drawable.bounds.IBounds;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.Optional;

public class MousePressedSudokuHandler extends SudokuHandlerBase {

    public MousePressedSudokuHandler(SudokuGame sudokuGame, SudokuGameController sudokuGameController) {
        super(sudokuGame, sudokuGameController);
    }

    public void onMousePressed(MouseEvent mouseEvent) {

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        var sgc = this.getSudokuGameController();
        boolean hintChoice = sgc.isHintChoice();
        Sudoku sudoku = sgc.getSudokuGame().getSudoku();
        if (hintChoice) {
            Optional<IBounds> optionalCell = sgc.getQuadTree().search(x, y);

            if (optionalCell.isEmpty() || !(optionalCell.get() instanceof SudokuCell)) {
                sgc.setHoveredSudokuCells(null);
                return;
            }

            SudokuCell sudokuCell = (SudokuCell) optionalCell.get();
            SudokuCell[][] board = sudoku.getBoard();

            SudokuCell[] result = switch (sgc.getActiveHint()){
                case CHOSEN_CELL, CHECK_CELL -> new SudokuCell[] {sudokuCell};

                case CHECK_ROW -> {
                    int col = sudokuCell.getCol();
                    SudokuCell[] column = new SudokuCell[sudoku.getHeight()];
                    for (int r = 0; r < sudoku.getHeight(); r++) {
                        column[r] = board[r][col];
                    }
                    yield column;
                }

                case CHECK_COLUMN -> {
                    int row = sudokuCell.getRow();
                    yield Arrays.copyOf(board[row], board[row].length);

                }
                case CHECK_BOX -> {
                    int row = sudokuCell.getRow();
                    int col = sudokuCell.getCol();

                    int subgridSize = (int) Math.sqrt(sudoku.getWidth());
                    int startRow = (row / subgridSize) * subgridSize;
                    int startCol = (col / subgridSize) * subgridSize;

                    SudokuCell[] subgridCells = new SudokuCell[subgridSize * subgridSize];
                    int index = 0;

                    for (int r = startRow; r < startRow + subgridSize; r++) {
                        for (int c = startCol; c < startCol + subgridSize; c++) {
                            subgridCells[index++] = board[r][c];
                        }
                    }

                    yield subgridCells;
                }
                default -> null;
            };

            sgc.setHoveredSudokuCells(result);
        }
    }

}

