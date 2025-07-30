package cz.upol.logicgo.commands.sudokuCommand;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.commands.Command;

public abstract class SudokuCommand extends Command {

    final private SudokuGame sudokuGame;


    public SudokuCommand(SudokuGame sudokuGame) {
        super();
        this.sudokuGame = sudokuGame;
    }

    public SudokuGame getSudokuGame() {
        return sudokuGame;
    }
}
