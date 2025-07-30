package cz.upol.logicgo.algorithms.sudoku;

import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;

public class SudokuGame {
    final private SudokuValidator validator;
    final private Sudoku sudoku;

    public SudokuGame(Sudoku sudoku, boolean createValidator) {
        SudokuValidator sudokuValidator = null;
        if (createValidator) {
            sudokuValidator = new SudokuValidator(sudoku);
        }
        this(sudokuValidator, sudoku);
    }

    public SudokuGame(SudokuValidator sudokuValidator, Sudoku sudoku) {
        this.validator = sudokuValidator;
        this.sudoku = sudoku;
    }

    public SudokuGame(SudokuGame sudokuGame) {
        Sudoku sudokuCopy = new Sudoku(sudokuGame.getSudoku());
        this.sudoku = sudokuCopy;
        this.validator = sudokuGame.getSudokuValidator().copy(sudokuCopy);
    }

    public SudokuValidator getSudokuValidator() {
        return validator;
    }

    public Sudoku getSudoku() {
        return sudoku;
    }


    public void setNumber(int rowIndex, int columnIndex, int number) {
        int oldNum = sudoku.setNumber(rowIndex, columnIndex, number);
        validator.unset(rowIndex, columnIndex, oldNum);
        validator.update(rowIndex, columnIndex, number);
    }

    public int removeNumber(int rowIndex, int columnIndex) {
        int removedNum = sudoku.setNumberToZero(rowIndex, columnIndex); // pokud se nevydařilo, tak smažu naposledy vložené číslo
        validator.remove(rowIndex, columnIndex, removedNum);
        return removedNum;
    }

    public SudokuGame makeCopy(){
        return new SudokuGame(this);
    }

    // temp metody , obcházejí validátor pro možnost hraní, možná oddělat

    public void setNumberPlay(int rowIndex, int columnIndex, int number) {
        int oldNum = sudoku.setNumber(rowIndex, columnIndex, number);
    }

    public int removeNumberPlay(int rowIndex, int columnIndex) {
        return sudoku.setNumberToZero(rowIndex, columnIndex);
    }

}
