package cz.upol.logicgo.util;

import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;

import java.util.ArrayList;

public class SudokuCellConverters {

    public static byte[] serializeBoard(SudokuCell[][] board) {
        ArrayList<Byte> bytes = new ArrayList<>();
        SudokuType type = SudokuType.getTypeByGridSize(board.length);
        if (type == null) return new byte[0];
        bytes.add((byte) type.getGridSize());
        bytes.add((byte) 0);

        switch (type) {
            case SIXTEEN -> {
                for (SudokuCell[] row : board) {
                    for (SudokuCell cell : row) {
                        var val = (byte) cell.getValue();
                        bytes.add(val);
                    }
                }
            }
            case FOUR, SIX, NINE, FIVE, SEVEN, EIGHT, TEN, TWELVE -> {
                boolean halfByte = false;
                byte currentByte = 0;
                for (SudokuCell[] row : board) {
                    for (SudokuCell cell : row) {
                        var val = (byte) cell.getValue();
                        if (halfByte) {
                            currentByte |= (byte) (val & 0xF);
                            bytes.add(currentByte);
                        } else {
                            currentByte = (byte) (val << 4);
                        }
                        halfByte = !halfByte;
                    }
                }

                if (halfByte) {
                    bytes.add(currentByte);
                }
            }
        }


        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }
        return byteArray;
    }

    public static SudokuCell[][] deserializeBoard(byte[] encodedBoard) {
        int gridSize = encodedBoard[0];
        var type = SudokuType.getTypeByGridSize(gridSize);
        var sudokuCells = new SudokuCell[gridSize][gridSize];
        int currentByteIndex = 2;
        boolean halfByte = false;
        switch (type) {
            case SIXTEEN -> {
                for (int i = 0; i < type.getGridSize(); i++) {
                    for (int j = 0; j < type.getGridSize(); j++) {
                        var value = encodedBoard[currentByteIndex];
                        sudokuCells[i][j] = new SudokuCell(i, j, value);
                        currentByteIndex++;
                    }
                }
            }
            case FOUR, SIX, NINE, FIVE, SEVEN, EIGHT, TEN, TWELVE -> {
                byte byteValue = 0;
                int value;
                loop:
                for (int i = 0; i < type.getGridSize(); i++) {
                    for (int j = 0; j < type.getGridSize(); j++) {
                        if (!halfByte) {
                            if (currentByteIndex >= encodedBoard.length) break loop;
                            byteValue = encodedBoard[currentByteIndex];
                            value = (byteValue >> 4) & 0x0F;
                        } else {
                            value = byteValue & 0x0F;
                            currentByteIndex++;
                        }
                        halfByte = !halfByte;
                        sudokuCells[i][j] = new SudokuCell(i, j, value);
                    }
                }
            }
        }
        return sudokuCells;
    }
}
