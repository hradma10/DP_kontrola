package cz.upol.logicgo.util.converters;

import cz.upol.logicgo.misc.enums.Difficulty;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DifficultyConverter implements AttributeConverter<Difficulty, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Difficulty status) {
        if (status == null) {
            return null;
        }
        return status.getValue();
    }

    @Override
    public Difficulty convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        return Difficulty.getDifficultyFromValue(value);
    }
}
