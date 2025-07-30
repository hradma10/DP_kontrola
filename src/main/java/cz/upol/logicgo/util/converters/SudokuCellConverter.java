package cz.upol.logicgo.util.converters;

import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.util.SudokuCellConverters;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SudokuCellConverter implements AttributeConverter<SudokuCell[][], byte[]> {


    public SudokuCellConverter() {

    }

    @Override
    public byte[] convertToDatabaseColumn(SudokuCell[][] board) {
        return SudokuCellConverters.serializeBoard(board);
    }

    @Override
    public SudokuCell[][] convertToEntityAttribute(byte[] encodedBoard) {
        return SudokuCellConverters.deserializeBoard(encodedBoard);
    }
}