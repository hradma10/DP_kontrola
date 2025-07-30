package cz.upol.logicgo.model.games.cqUpdate;

import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.user.User;

public class SudokuUpdate extends GameUpdate {

    private Boolean respectRules;

    private SudokuCell[][] board;

    private byte[] undoStack;

    private byte[] redoStack;

    public SudokuUpdate(long gameId, User user) {
        super(gameId, user);
    }

    public SudokuUpdate setRespectRules(boolean respectRules) {
        this.respectRules = respectRules;
        return this;
    }

    public boolean isRespectRules() {
        return respectRules;
    }

    public boolean hasRespectRules() {
        return respectRules != null;
    }

    public SudokuUpdate setBoard(SudokuCell[][] board) {
        this.board = board;
        return this;
    }

    public SudokuCell[][] getBoard() {
        return board;
    }

    public boolean hasBoard() {
        return board != null;
    }
    public SudokuUpdate setUndoStack(byte[] undoStack) {
        this.undoStack = undoStack;
        return this;
    }

    public byte[] getUndoStack() {
        return undoStack;
    }

    public boolean hasUndoStack() {
        return undoStack != null;
    }

    public SudokuUpdate setRedoStack(byte[] redoStack) {
        this.redoStack = redoStack;
        return this;
    }

    public byte[] getRedoStack() {
        return redoStack;
    }

    public boolean hasRedoStack() {
        return redoStack != null;
    }
}
