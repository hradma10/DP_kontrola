package cz.upol.logicgo.commands.sudokuCommand;


import cz.upol.logicgo.algorithms.sudoku.SudokuGame;

public class SetSudokuNumberCommand extends SudokuCommand {

    final public static byte type = 0;
    final private int row;
    final private int col;
    final private int oldNumber;
    final private int newNumber;

    public SetSudokuNumberCommand(SudokuGame sudokuGame, int row, int column, int newNumber, int oldNumber) {
        super(sudokuGame);
        this.row = row;
        this.col = column;
        this.oldNumber = oldNumber;
        this.newNumber = newNumber;
    }

    @Override
    public void execute() {
        var sudokuGame = this.getSudokuGame();
        sudokuGame.setNumberPlay(this.getRow(), this.getCol(), newNumber);
   }

    @Override
    public void undo() {
        var sudokuGame = this.getSudokuGame();
        sudokuGame.setNumberPlay(this.getRow(), this.getCol(), oldNumber);
    }

    public int getOldNumber() {
        return oldNumber;
    }

    public int getNewNumber() {
        return newNumber;
    }

    public byte[] getCommandsAsBytes() {
        byte[] bytes = new byte[0];
        var type = this.getSudokuGame().getSudoku().getType();

        byte commandType = this.getType();
        byte row = (byte) this.getRow();
        byte column = (byte) this.getCol();
        byte oldNumber = (byte) this.getOldNumber();
        byte newNumber = (byte) this.getNewNumber();


        switch (type) {
            case SIXTEEN -> {
                bytes = new byte[5];
                bytes[0] = commandType;
                bytes[1] = row;
                bytes[2] = column;
                bytes[3] = oldNumber;
                bytes[4] = newNumber;
            }
            case FOUR, SIX, NINE -> {
                bytes = new byte[3];
                bytes[0] = commandType;

                var value = (byte) (row << 4);
                value |= (byte) (column & 0xF);
                bytes[1] = value;

                value = (byte) (oldNumber << 4);
                value |= (byte) (newNumber & 0xF);
                bytes[2] = value;
            }
        }

        return bytes;

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public byte getType() {
        return type;
    }
}
