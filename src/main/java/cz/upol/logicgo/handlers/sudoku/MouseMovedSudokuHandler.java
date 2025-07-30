package cz.upol.logicgo.handlers.sudoku;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.controllers.screenControllers.SudokuGameController;
import cz.upol.logicgo.model.games.drawable.bounds.IBounds;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import javafx.scene.input.MouseEvent;
import kotlin.text.RegexOption;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class MouseMovedSudokuHandler extends SudokuHandlerBase {

    public MouseMovedSudokuHandler(SudokuGame sudokuGame, SudokuGameController sudokuGameController) {
        super(sudokuGame, sudokuGameController);
    }

    public void onMouseMoved(MouseEvent mouseEvent) {

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        var sgc = this.getSudokuGameController();
        boolean hintChoice = sgc.isHintChoice();
        Sudoku sudoku = sgc.getSudokuGame().getSudoku();
        if (hintChoice) {
            Optional<IBounds> optionalCell = sgc.getQuadTree().search(x, y);

            if (optionalCell.isEmpty() || !(optionalCell.get() instanceof SudokuCell sudokuCell)) {
                sgc.setHoveredSudokuCells(null);
                return;
            }

            SudokuCell[][] board = sudoku.getBoard();

            if (sgc.getActiveHint() == null) return;
            SudokuCell[] result = switch (sgc.getActiveHint()){
                case CHOSEN_CELL, CHECK_CELL, CHECK_CANDIDATES -> new SudokuCell[] {sudokuCell};

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
                    RegionLayout regionLayout = sudoku.getRegionLayout();

                    int row = sudokuCell.getRow();
                    int col = sudokuCell.getCol();

                    int index = regionLayout.getRegions()[row][col];

                    ArrayList<RegionLayout.SubGridCells> sameBoxCells = regionLayout.getCellsBelongToSubgrid(index);

                    SudokuCell[] subgridCells = new SudokuCell[sameBoxCells.size()];

                    index = 0;

                    for (var cell : sameBoxCells) {
                            subgridCells[index++] = board[cell.row()][cell.col()];
                    }

                    yield subgridCells;
                }
                default -> null;
            };

            sgc.setHoveredSudokuCells(result);
        }
    }

}

