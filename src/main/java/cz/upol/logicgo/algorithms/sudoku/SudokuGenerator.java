package cz.upol.logicgo.algorithms.sudoku;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.algorithms.sudoku.solvers.SudokuParallelSolver;
import cz.upol.logicgo.algorithms.sudoku.solvers.SudokuSolveContext;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;

import java.util.*;

import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.getBoardCopy;

public class SudokuGenerator {

    final public static int MAX_ELEMENTS_TO_REMOVE = 65;

    private static boolean removeElements(SudokuGame sudokuGame, int numbersElementsToRemove, Stack<RemovedElement> removedElements, int order, Random random) {
        Sudoku sudoku = sudokuGame.getSudoku();
        // pokud byly odstraněny všechny prvky co se mají odstranit, vracím true
        if (numbersElementsToRemove == 0) return true;
        var r = random;
        // list řádků a sloupců, které už byly vybrané
        var chosenCandidates = new HashSet<ChosenCandidates>();
        while (true) {
            int colIndex, rowIndex;
            // tento loop slouží k nalezení řádku a sloupce, ze kterého bude smazán prvek
            // je ukončen, pokud byla nalezen nenulový prvek a tato skombinace sloupce a řádku nebyla ještě zkoušena
            // je vráceno false, pokud už jsem vyskoušel všechny možnosti
            if (chosenCandidates.size() == sudoku.getType().getCellCount() - removedElements.size()) {
                return false;
            }
            do {
                rowIndex = r.nextInt(sudoku.getType().getGridSize());
                colIndex = r.nextInt(sudoku.getType().getGridSize());
            } while (sudoku.isZero(rowIndex, colIndex) || chosenCandidates.contains(new ChosenCandidates(rowIndex, colIndex)));

            chosenCandidates.add(new ChosenCandidates(rowIndex, colIndex));

            // odstraním prvek a vložím informaci o odstranění do pole
            int element = sudokuGame.removeNumber(rowIndex, colIndex);
            var removedElement = new RemovedElement(rowIndex, colIndex, element, order);
            removedElements.add(removedElement);

            // získám kandidáty, kteří by mohli být vloženi na místo smazaného prvku
            //var candidates = sudokuGame.getSudokuValidator().getCellCandidates(rowIndex, colIndex);

            //final int finalRowIndex = rowIndex;
            //final int finalColIndex = colIndex;

            boolean anotherSolution = false;
            boolean noSolution = false;
            SudokuGame sudokuGameCopy = new SudokuGame(sudokuGame);

            SudokuSolveContext context = new SudokuSolveContext();
            SudokuParallelSolver.solvingParallelStart(sudokuGameCopy, context);
            if (context.getSolutionCount() > 1) {
                anotherSolution = true;
            }else if (context.getSolutionCount() == 0){
                noSolution = true;
            }

            if (anotherSolution || noSolution) { // pokud bylo nalezeno další řešení, naposledy odstraněný prvek byl nevhodný
                var lastRemovedElement = removedElements.pop();
                sudokuGame.setNumber(lastRemovedElement.rowIndex(), lastRemovedElement.columnIndex(), lastRemovedElement.element());
            } else {  // jinak pokračuji dál
                numbersElementsToRemove--;
                order++;
                if (removeElements(sudokuGame, numbersElementsToRemove, removedElements, order, random)) { // pokud je ukončeno mazání hodnotou true, vše prošlo a mohu skončit
                    return true;
                } else { // jinak je nutnost se vrátit
                    numbersElementsToRemove++;
                    order--;
                    var lastRemovedElement = removedElements.pop();
                    sudoku.setNumber(lastRemovedElement.rowIndex(), lastRemovedElement.columnIndex(), lastRemovedElement.element());
                }
            }
        }
    }

    private static SudokuGame fillSudokuBoardRecursive(Sudoku sudoku) {
        SudokuGame sudokuGame = new SudokuGame(sudoku, true);
        ArrayList<Integer> numbersToInsert = new ArrayList<>(List.of(sudoku.getType().getPossibleNumbers()));
        fillSudokuBoard(numbersToInsert, 0, 0, sudokuGame);
        sudoku.setSolutionBoard(getBoardCopy(sudoku.getStartingBoard()));
        return sudokuGame;
    }

    private static boolean fillSudokuBoard(ArrayList<Integer> rowRemainingNumbers, int rowIndex, int columnIndex, SudokuGame sudokuGame) {
        // záloha čisel, která mohou být vložena
        var sudoku = sudokuGame.getSudoku();
        var validator = sudokuGame.getSudokuValidator();
        if (rowIndex == sudoku.getType().getGridSize()) return true;

        ArrayList<Integer> cellCandidates = new ArrayList<>(rowRemainingNumbers);
        while (!cellCandidates.isEmpty()) { // postupně zkouším vložit všechna čísla

            int index = sudoku.getRandomInstance().nextInt(cellCandidates.size()); // náhodně vybírám čísla podle indexu, která budou vložena
            int number = cellCandidates.get(index);

            if (validator.isValidMove(rowIndex, columnIndex, number)) { // pokusím se vložit číslo
                sudokuGame.setNumber(rowIndex, columnIndex, number);
                rowRemainingNumbers.remove((Integer) number); // odstraním z pole čísel, která ještě nejsou na řádku
                cellCandidates.remove(index);

                int newColumnIndex = (columnIndex + 1) % sudoku.getType().getGridSize();
                int newRowIndex = columnIndex + 1 == sudoku.getType().getGridSize() ? rowIndex + 1 : rowIndex;

                var newNumbersToInsert = newRowIndex == rowIndex ? new ArrayList<>(rowRemainingNumbers) : new ArrayList<>(List.of(sudoku.getType().getPossibleNumbers()));

                // pokud jsem na stejném řádku, vložím zbytek čísel možná na řádku
                // nebo pokud pokračuji na novém řádku

                if (fillSudokuBoard(newNumbersToInsert, newRowIndex, newColumnIndex, sudokuGame)) { // cyklovalo mezi 10´9 a 10 16 seed 7655755813462
                    return true;
                }

                sudokuGame.removeNumber(rowIndex, columnIndex); // pokud se nevydařilo, tak smažu naposledy vložené číslo
                rowRemainingNumbers.add(number); // vložím zpět do čísel možných na řádku číslo, kde byl neúspěšný pokus vložení
            } else {
                cellCandidates.remove(index);
            }

        }

        return false;
    }
    public static Sudoku createSudoku(SudokuType sudokuType, RegionLayout regionLayout, Difficulty difficulty, User player){
        Sudoku sudoku = new Sudoku(sudokuType, regionLayout, difficulty, player);
        int removeCount = Difficulty.getSudokuCellsToRemove(sudokuType, difficulty, sudoku.getSeed());
        createSudoku(sudoku, removeCount);
        return sudoku;
    }

    public static Sudoku createSudoku(SudokuType sudokuType, RegionLayout regionLayout, Difficulty difficulty, User player, long seed){
        Sudoku sudoku = new Sudoku(sudokuType, regionLayout, difficulty, seed,  player);
        int removeCount = Difficulty.getSudokuCellsToRemove(sudokuType, difficulty, sudoku.getSeed());
        createSudoku(sudoku, removeCount);
        return sudoku;
    }

    public static void createSudoku(Sudoku sudoku, int numbersElementsToRemove) {
        // vytvořím vyplněné sudoku
        SudokuGame sudokuGame = fillSudokuBoardRecursive(sudoku);
        sudoku.setSolutionBoard(getBoardCopy(sudoku.getBoard()));
        // inicializuji pole s recordy popisující odstraněné prvky
        Stack<RemovedElement> removedElements = new Stack<>();
        // pokud počet odstraněných prvků překročí maximální počet, je vyhozena výjimka
        /*if (numbersElementsToRemove >= MAX_ELEMENTS_TO_REMOVE) {
            throw new IllegalArgumentException("There must be less than 65 cells being removed");
        }*/
        var random = sudoku.getRandomInstance();
        // začátek mazání prvků
        try {
            removeElements(sudokuGame, numbersElementsToRemove, removedElements, 0, random);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        sudoku.setStartingBoard(getBoardCopy(sudoku.getBoard()));
    }

    public record RemovedElement(int rowIndex, int columnIndex, int element, int order) {
        @Override
        public String toString() {
            return "RemovedElement{" +
                    "rowIndex=" + rowIndex +
                    ", columnIndex=" + columnIndex +
                    ", element=" + element +
                    ", order=" + order +
                    '}';
        }
    }

    private record ChosenCandidates(int rowIndex, int colIndex) {
    }


}