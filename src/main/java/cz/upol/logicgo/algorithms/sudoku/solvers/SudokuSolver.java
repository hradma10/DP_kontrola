package cz.upol.logicgo.algorithms.sudoku.solvers;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.algorithms.sudoku.SudokuValidator;
import cz.upol.logicgo.algorithms.sudoku.TargetedCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;

import java.util.ArrayList;
import java.util.concurrent.*;


public class SudokuSolver {


    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final int MAX_SOLUTIONS = 1;
    private static volatile boolean stopFlag = false;
    private static SudokuGame solvedGame;
    public static int MINIMAL_FILLED_CELLS = 17;

    /**
     * solver
     *
     * @return {@code true} if solvable, {@code false} if not
     */
    public static boolean solveSudoku(SudokuGame sudokuGame) {
        var emptyCells = sudokuGame.getSudokuValidator().getEmptyCells();
        if (emptyCells.size() > 81 - MINIMAL_FILLED_CELLS) throw new IllegalArgumentException("");

        return solve(sudokuGame);
    }


    public static boolean solveSudoku(Sudoku sudoku) {
        SudokuGame sudokuGame = new SudokuGame(sudoku, true);
        return solveSudoku(sudokuGame);
    }

    public static ArrayList<TargetedCell> insertToEasyCells(SudokuGame sudokuGame, ArrayList<TargetedCell> emptyCells) {
        SudokuValidator validator = sudokuGame.getSudokuValidator();
        ArrayList<TargetedCell> filled = new ArrayList<>();  // list vyplněných prvků
        boolean changed = false;
        do { // TODO chyba v této metodě nějak špatně se vyplňuje tady
            changed = false;
            for (var emptyCell : emptyCells) {
                var row = emptyCell.getRow();
                var col = emptyCell.getCol();
                var cellCandidates = validator.getCellCandidates(row, col);  // získám čísla, která lze vložit do políčka
                if (cellCandidates.isEmpty())
                    return null;  // pokud žádná nejsou, tuto verzi sudoku nelze vyřešit
                if (cellCandidates.size() == 1) {
                    int candidate = cellCandidates.getFirst();
                    sudokuGame.setNumber(row, col, candidate);  // pokud je jenom jeden, vložíme
                    emptyCell.setVal(candidate);
                    filled.add(emptyCell);
                    changed = true;
                }
            }
        } while (changed);

        return filled;
    }

    public static boolean solving(SudokuGame sudokuGame) {
        var sudoku = sudokuGame.getSudoku();
        var validator = sudokuGame.getSudokuValidator();
        var emptyCells = validator.getEmptyCells(); // získám prázdná políčka
        if (emptyCells.isEmpty()) return true; // pokud jsem vložil všechny co zbývaly, mohu skončit
        TargetedCell emptyCell = emptyCells.getFirst();
        System.out.println();
        int row = emptyCell.getRow();
        int col = emptyCell.getCol();
        var cellCandidates = validator.getCellCandidates(row, col);
        if (cellCandidates.isEmpty()) return false;
        for (var candidate : cellCandidates) {
            sudokuGame.setNumber(row, col, candidate);
            if (solving(sudokuGame)) {
                return true;
            }else {
                sudokuGame.removeNumber(row, col);
            }
        }
        return false;
    }

    public static boolean solve(SudokuGame sudokuGame) {
        var sudoku = sudokuGame.getSudoku();
        var validator = sudokuGame.getSudokuValidator();
        var emptyCells = validator.getEmptyCells(); // získám prázdná políčka
        //ArrayList<TargetedCell> insertedToCells = insertToEasyCells(sudokuGame, emptyCells); // vložím do jednoduchých políček
        //if (insertedToCells == null) return false;    // pokud nešlo vložit do políčka, nelze vyřešit
        //emptyCells.removeAll(insertedToCells);
        if (emptyCells.isEmpty()) return true; // pokud jsem vložil všechny co zbývaly, mohu skončit
        TargetedCell emptyCell = emptyCells.getFirst();
        int row = emptyCell.getRow();
        int col = emptyCell.getCol();
        var cellCandidates = validator.getCellCandidates(row, col);
        if (cellCandidates.isEmpty()) return false;
        for (var candidate : cellCandidates) {
            sudokuGame.setNumber(row, col, candidate);
            if (solving(sudokuGame)) {
                return true;
            }else {
                sudokuGame.removeNumber(row, col);
            }
        }
        /*for (var candidate : insertedToCells) {
            sudokuGame.removeNumber(candidate.getRow(), candidate.getCol());
        }*/
        return false;
    }
}
