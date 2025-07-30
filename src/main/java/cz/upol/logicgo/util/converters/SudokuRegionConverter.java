package cz.upol.logicgo.util.converters;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.algorithms.sudoku.layout.SudokuLayouts;
import cz.upol.logicgo.algorithms.sudoku.layout.SudokuLayoutsLoader;
import cz.upol.logicgo.misc.enums.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SudokuRegionConverter implements AttributeConverter<RegionLayout, String> {


    @Override
    public String convertToDatabaseColumn(RegionLayout attribute) {
        return attribute.getName();
    }

    @Override
    public RegionLayout convertToEntityAttribute(String name) {
        return SudokuLayoutsLoader.getLayoutsByName(name);
    }
}
;