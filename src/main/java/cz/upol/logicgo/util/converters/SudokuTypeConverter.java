package cz.upol.logicgo.util.converters;

import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SudokuTypeConverter implements AttributeConverter<SudokuType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SudokuType sudokuType) {
        if (sudokuType == null) {
            return null;
        }
        return sudokuType.getGridSize();
    }

    @Override
    public SudokuType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return SudokuType.getTypeByGridSize(dbData);
    }
}
