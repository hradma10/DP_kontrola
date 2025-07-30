package cz.upol.logicgo.algorithms.sudoku.solvers;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.algorithms.sudoku.SudokuValidator;
import cz.upol.logicgo.algorithms.sudoku.TargetedCell;
import cz.upol.logicgo.exceptions.parallel.ThreadTerminationException;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;

import java.util.*;
import java.util.concurrent.*;


public class SudokuParallelSolver {

    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final int MAX_SOLUTIONS = 1;
    public static int MINIMAL_FILLED_CELLS = 17;
    private static final ConcurrentHashMap<UUID, SudokuSolveContext> contexts = new ConcurrentHashMap<>();


    public static SudokuGame solveSudokuParallel(SudokuGame sudokuGame) {
        UUID uuid = UUID.randomUUID();
        SudokuSolveContext context = new SudokuSolveContext();
        contexts.put(uuid, context);
        var emptyCells = sudokuGame.getSudokuValidator().getEmptyCells();
        //if (emptyCells.size() > 81 - MINIMAL_FILLED_CELLS) throw new IllegalArgumentException("");
        solvingParallelStart(sudokuGame, context);
        contexts.remove(uuid);
        return context.getSolutionCount() == 1 ? context.getSolvedGame() : null;
    }

    public static SudokuGame solveSudokuParallel(Sudoku sudoku) {
        SudokuGame sudokuGame = new SudokuGame(sudoku, true);
        /*if (sudokuGame.getSudokuValidator().getEmptyCells().size() < 25) {
            boolean res = SudokuSolver.solveSudoku(sudokuGame);
            return res ? sudokuGame : null;
        }*/
        return solveSudokuParallel(sudokuGame);
    }

    public static TargetedCell getOneWithLeastCandidates(ArrayList<TargetedCell> cells, SudokuValidator sudokuValidator, Random rng) {
        int min = cells.stream()
                .mapToInt(cell -> sudokuValidator.getCellCandidates(cell.getRow(), cell.getCol()).size())
                .min()
                .orElse(Integer.MAX_VALUE);

        List<TargetedCell> filtered = cells.stream()
                .filter(cell -> sudokuValidator.getCellCandidates(cell.getRow(), cell.getCol()).size() == min)
                .toList();

        if (filtered.isEmpty()) return null;
        return filtered.get(rng.nextInt(filtered.size()));
    }

    public static TargetedCell getOneWithMostCandidates(ArrayList<TargetedCell> cells, SudokuValidator sudokuValidator, Random rng) {
        int max = cells.stream()
                .mapToInt(cell -> sudokuValidator.getCellCandidates(cell.getRow(), cell.getCol()).size())
                .max()
                .orElse(Integer.MIN_VALUE);

        List<TargetedCell> filtered = cells.stream()
                .filter(cell -> sudokuValidator.getCellCandidates(cell.getRow(), cell.getCol()).size() == max)
                .toList();

        if (filtered.isEmpty()) return null;
        return filtered.get(rng.nextInt(filtered.size()));
    }

    public static void solvingParallelStart(SudokuGame sudokuGame, SudokuSolveContext context) {
        List<Future<?>> futures = new ArrayList<>();
        var validator = sudokuGame.getSudokuValidator();
        ArrayList<TargetedCell> emptyCells = validator.getEmptyCells(); // získám prázdná políčka
        if (emptyCells.isEmpty()) {
            context.incrementSolutionCountAndGet();
            context.setSolvedGame(sudokuGame);// pokud mám vyřešeno před paralelizací, nastavím v kontextu a končím
            return;
        }
        Random randomInstance = sudokuGame.getSudoku().getRandomInstance();

        TargetedCell leastCandidateCell = getOneWithMostCandidates(emptyCells, validator, randomInstance);
        int row = leastCandidateCell.getRow();
        int col = leastCandidateCell.getCol();
        ArrayList<Integer> candidates = validator.getCellCandidates(row, col);
        for (var candidate : candidates) {
            SudokuGame sudokuGameCopy = new SudokuGame(sudokuGame);
            sudokuGameCopy.setNumber(row, col, candidate);
            futures.add(executor.submit(() -> {
                try {
                    solvingParallel(sudokuGameCopy, context);
                } catch (ThreadTerminationException ignored) {}
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
    }

    public static void solvingParallel(SudokuGame sudokuGame, SudokuSolveContext context) throws ThreadTerminationException {
        if (context.shouldStop() || context.getSolutionCount() > MAX_SOLUTIONS || Thread.currentThread().isInterrupted()) throw new ThreadTerminationException();
        var validator = sudokuGame.getSudokuValidator();
        ArrayList<TargetedCell> emptyCells = validator.getEmptyCells(); // získám prázdná políčka
        if (emptyCells.isEmpty()) {
            allCellsFilledHandler(sudokuGame, context);
            return; // pokud jsem vložil všechny co zbývaly, mohu skončit
        }
        Random randomInstance = sudokuGame.getSudoku().getRandomInstance();

        // vyplním všechny políčka s jedním možným číslem
        /*var filledCells = fillOneCandidateCells(emptyCells, sudokuGame);
        emptyCells.removeAll(filledCells);*/

        if (emptyCells.isEmpty()) { // pokud jsem vložil všechny co zbývaly, mohu skončit
            allCellsFilledHandler(sudokuGame, context);
            return;
        }
        TargetedCell emptyCell = getOneWithLeastCandidates(emptyCells, validator, randomInstance);
        int row = emptyCell.getRow();
        int col = emptyCell.getCol();
        var cellCandidates = validator.getCellCandidates(row, col);
        if (cellCandidates.isEmpty()) {
            //refillOneCandidateCells(filledCells, sudokuGame); // pokud nejsou kandidáti pro políčka -> chyba
            return;                                           // musím vrátit vyplnění všech políčka s jedním možným číslem
         }
        for (var candidate : cellCandidates) {
            sudokuGame.setNumber(row, col, candidate);
            solvingParallel(sudokuGame, context);
            if (context.shouldStop() || context.getSolutionCount() > MAX_SOLUTIONS || Thread.currentThread().isInterrupted()) throw new ThreadTerminationException();
            sudokuGame.removeNumber(row, col);
        }
        //refillOneCandidateCells(filledCells, sudokuGame);
    }

    private static void allCellsFilledHandler(SudokuGame sudokuGame, SudokuSolveContext context) throws ThreadTerminationException {
        if (context.incrementSolutionCountAndGet() > MAX_SOLUTIONS) {
            context.setStopFlag(true);
            throw new ThreadTerminationException();
        }else {
            context.setSolvedGame(sudokuGame);
        }
    }

    public static ArrayList<TargetedCell> fillOneCandidateCells(ArrayList<TargetedCell> emptyCells, SudokuGame sudokuGame) {
        ArrayList<TargetedCell> filledCells = new ArrayList<>();
        for (var cell : emptyCells) {
            ArrayList<Integer> candidates = sudokuGame.getSudokuValidator().getCellCandidates(cell.getRow(), cell.getCol());
            if (candidates.size() == 1) {
                int value =  candidates.getFirst();
                sudokuGame.setNumber(cell.getRow(), cell.getCol(), value);
                filledCells.add(cell);
                cell.setVal(value);
            }
        }
        return filledCells;
    }

    public static void refillOneCandidateCells(ArrayList<TargetedCell> filledCells, SudokuGame sudokuGame) {
        for (TargetedCell filled : filledCells) {
            sudokuGame.removeNumber(filled.getRow(), filled.getCol());
        }
    }


}
