package cz.upol.logicgo.algorithms.sudoku;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.*;

import static cz.upol.logicgo.algorithms.sudoku.SudokuGenerator.createSudoku;
import static javafx.scene.paint.Color.CORNFLOWERBLUE;

public class SudokuUtils {
    public static int calcSubGridIndex(int row, int col, RegionLayout regionLayout) {
        return regionLayout.getRegions()[row][col];
    }

    public static void setCellsProperties(Sudoku sudoku, boolean changeable, boolean hints) {
        SudokuCell[][] startingBoard = sudoku.getStartingBoard();
        SudokuCell[][] board = sudoku.getBoard();
        for (int i = 0; i < sudoku.getBoard().length; i++) {
            for (int j = 0; j < sudoku.getBoard()[0].length; j++) {
                SudokuCell el = board[i][j];
                el.setSudokuType(sudoku.getType());
                if (startingBoard[i][j].getValue() != 0) {
                    el.setChangeable(changeable);
                }else {
                    el.setHints(hints);
                }
            }
        }
    }


    public static  <T extends Setting> Map<SettingKey, String> getSettingsAsMap(List<T> settings) {
        Map<SettingKey, String> settingsMap = new HashMap<>();

        if (settings != null) {
            for (Setting setting : settings) {
                settingsMap.put(setting.getKey(), setting.getValue());
            }
        }

        return settingsMap;
    }

    public static void setCellsProperties(SudokuCell[][] cells) {
        for (SudokuCell[] row : cells) {
            for (SudokuCell item : row) {
                item.setChangeable(false);
            }
        }
    }

    public static SudokuCell[][] getBoardCopy(SudokuCell[][] original) {
        if (original == null) return null;

        SudokuCell[][] copy = new SudokuCell[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new SudokuCell[original[i].length];
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = original[i][j] != null ? new SudokuCell(original[i][j]) : null;
            }
        }
        return copy;
    }

    public static SudokuCell[][] createEmptyCells(SudokuType sudokuType) {
        int gridSize = sudokuType.getGridSize();
        var sudokuCells = new SudokuCell[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                sudokuCells[i][j] = new SudokuCell(i, j, 0);
            }
        }
        return sudokuCells;
    }

    public static SudokuCell[][] createCellsFromInts(int[][] board) {
        var sudokuCells = new SudokuCell[board.length][board.length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                sudokuCells[i][j] = new SudokuCell(i, j, board[i][j]);
            }
        }
        return sudokuCells;
    }

    public static SudokuCell[] flattenBoard(SudokuCell[][] board) {
        return Arrays.stream(board)
                .flatMap(Arrays::stream)
                .toArray(SudokuCell[]::new);
    }


    public static boolean checkFinishedSudoku(SudokuGame sudokuGame) {
        Sudoku sudoku = sudokuGame.getSudoku();
        SudokuCell[] flattenedBoard = SudokuUtils.flattenBoard(sudoku.getBoard());
        SudokuCell[] flattenedSolutionBoard = SudokuUtils.flattenBoard(sudoku.getSolutionBoard());

        for (int i = 0; i < flattenedBoard.length; i++) {
            if (flattenedBoard[i].getValue() != flattenedSolutionBoard[i].getValue()) {
                return false;
            }
        }
        return true;
    }

    public static void changeCellSize(SudokuCell[][] board, double width, double height) {
        for (int row = 0; row < board.length; row++) {
            SudokuCell[] sudokuCells = board[row];
            int bound = board.length;
            for (int col = 0; col < bound; col++) {
                SudokuCell cell = sudokuCells[col];
                cell.changeCellSize(row, col, width, height);
            }
        }
    }

    public static boolean areBoardsEqual(SudokuCell[][] self, SudokuCell[][] other) {
        if (self.length != other.length || self[0].length != other[0].length) return false;
        for (int i = 0; i < self.length; i++) {
            for (int j = 0; j < self[i].length; j++) {
                if (self[i][j].getValue() != other[i][j].getValue()) {
                    return false;
                }
            }
        }
        return true;
    }


    public static void drawPulsatingCell(Canvas canvas, double timeSeconds, SudokuCell... sudokuCells) {
        if (sudokuCells == null || sudokuCells.length == 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double opacity = 0.3 + 0.3 * (0.5 + 0.5 * Math.sin(timeSeconds * 3));
        Color fill = Color.rgb(255, 100, 100, opacity);
        gc.setFill(fill);

        if (sudokuCells.length == 1) {
            SudokuCell c = sudokuCells[0];
            gc.fillRect(c.getTopLeft().getX(),
                    c.getTopLeft().getY(),
                    c.getWidth(),
                    c.getHeight());
        } else {
            for (SudokuCell c : sudokuCells) {
                gc.fillRect(c.getTopLeft().getX(),
                        c.getTopLeft().getY(),
                        c.getWidth(),
                        c.getHeight());
            }
        }
    }

    public static void clearCellBackgrounds(SudokuGame sudokuGame) {
        SudokuCell[] board = flattenBoard(sudokuGame.getSudoku().getBoard());
        Arrays.stream(board).forEach(cell -> cell.setBackgroundColor(Color.WHITE));
    }

    public static void clearCellBackgrounds(Sudoku sudoku) {
        SudokuCell[] board = flattenBoard(sudoku.getBoard());
        Arrays.stream(board).forEach(cell -> cell.setBackgroundColor(Color.WHITE));
    }

    public static void clearCellHints(SudokuGame sudokuGame) {
        SudokuCell[] board = flattenBoard(sudokuGame.getSudoku().getBoard());
        Arrays.stream(board).forEach(cell -> cell.setHints(false));
    }

    public static Sudoku generateSudokuTimed(long seed, User user, SudokuType gameType, RegionLayout regionLayout, Difficulty diff, long seconds) {
        Sudoku sudoku = null;
        int attempt = 0;
        Random random = new Random(seed);

        try (ExecutorService executor = Executors.newSingleThreadExecutor()){
            while (attempt < 10) {
                long currentSeed = seed;

                Future<Sudoku> future = executor.submit(() ->
                        createSudoku(gameType, regionLayout, diff, user, currentSeed));

                try {
                    sudoku = future.get(seconds, TimeUnit.SECONDS);
                    break;
                } catch (TimeoutException e) {
                    seed = random.nextLong(seed);
                    System.out.println("Generation too slow, retrying with seed " + (seed + 1));
                    future.cancel(true);
                    attempt++;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        return sudoku;
    }

    public static ArrayList<Integer> getCellCandidatesPlay(Sudoku sudoku, int setRow, int setCol) {
        ArrayList<Integer> candidates = new ArrayList<>();

        Set<Integer> used = new HashSet<>();

        SudokuCell[][] board = sudoku.getBoard();

        for (int col = 0; col < sudoku.getType().getGridSize(); col++) {
            SudokuCell cell = board[setRow][col];
            if (cell.getValue() != 0) used.add(cell.getValue());
        }

        for (int row = 0; row < sudoku.getType().getGridSize(); row++) {
            SudokuCell cell = board[row][setCol];
            if (cell.getValue() != 0) used.add(cell.getValue());
        }

        RegionLayout regionLayout = sudoku.getRegionLayout();

        int index = regionLayout.getRegions()[setRow][setCol];

        ArrayList<RegionLayout.SubGridCells> sameBoxCells = regionLayout.getCellsBelongToSubgrid(index);

        for (var sameBoxCell : sameBoxCells) {
            SudokuCell cell = board[sameBoxCell.row()][sameBoxCell.col()];
            if (cell.getValue() != 0) used.add(cell.getValue());

        }

        for (int num : sudoku.getType().getPossibleNumbers()) {
            if (!used.contains(num)) {
                candidates.add(num);
            }
        }

        return candidates;
    }


    public static void changeColorRelatedCells(Sudoku sudoku, SudokuCell selectedCell) {
        if (selectedCell == null) return;
        int setRow = selectedCell.getRow();
        int setCol = selectedCell.getCol();

        SudokuCell[][] board = sudoku.getBoard();
        RegionLayout regionLayout = sudoku.getRegionLayout();

        clearCellBackgrounds(sudoku);

        Color color = CORNFLOWERBLUE;

        // řádek
        for (int col = 0; col < sudoku.getType().getGridSize(); col++) {
            board[setRow][col].setBackgroundColor(color);
        }

        // sloupec
        for (int row = 0; row < sudoku.getType().getGridSize(); row++) {
            board[row][setCol].setBackgroundColor(color);
        }

        // region
        int index = regionLayout.getRegions()[setRow][setCol];
        ArrayList<RegionLayout.SubGridCells> sameBoxCells = regionLayout.getCellsBelongToSubgrid(index);

        for (RegionLayout.SubGridCells cellCoords : sameBoxCells) {
            board[cellCoords.row()][cellCoords.col()].setBackgroundColor(color);
        }

    }

}
