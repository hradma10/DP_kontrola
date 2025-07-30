package cz.upol.logicgo.commands.sudokuCommand;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.util.SudokuCellConverters;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RestartSudokuCommand extends SudokuCommand {

    final public static byte type = 1;

    final private SudokuCell[][] oldCells;

    final private SudokuCell[][] newCells;

    public RestartSudokuCommand(SudokuGame sudokuGame, SudokuCell[][] oldCells, SudokuCell[][] newCells) {
        super(sudokuGame);
        this.oldCells = oldCells;
        this.newCells = newCells;
    }

    @Override
    public void execute() {
        SudokuCell.copyBoardProperties(getSudokuGame().getSudoku().getBoard(), newCells);
        getSudokuGame().getSudoku().setBoard(newCells);
    }

    @Override
    public void undo() {
        SudokuCell.copyBoardProperties(getSudokuGame().getSudoku().getBoard(), oldCells);
        getSudokuGame().getSudoku().setBoard(oldCells);
    }

    @Override
    public byte[] getCommandsAsBytes() {
        byte[] oldCellsByte = SudokuCellConverters.serializeBoard(oldCells);
        byte[] newCellsByte = SudokuCellConverters.serializeBoard(newCells);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            dos.writeByte(this.getType());

            dos.writeShort(oldCellsByte.length);
            dos.write(oldCellsByte);

            dos.writeShort(newCellsByte.length);
            dos.write(newCellsByte);

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SudokuCell[][] getOldCells() {
        return oldCells;
    }

    public SudokuCell[][] getNewCells() {
        return newCells;
    }

    @Override
    public byte getType() {
        return type;
    }
}
