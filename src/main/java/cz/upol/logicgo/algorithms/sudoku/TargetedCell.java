package cz.upol.logicgo.algorithms.sudoku;

public final class TargetedCell {
    private final int row;
    private final int col;
    private int val;

    public TargetedCell(int row, int col, int val) {
        this.row = row;
        this.col = col;
        this.val = val;
    }

    public TargetedCell(int row, int col) {
        this.row = row;
        this.col = col;
        this.val = 0;
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "TargetedCell[" +
                "row=" + row + ", " +
                "col=" + col + ", " +
                "val=" + val + ']';
    }

}