package cz.upol.logicgo.util.converters;

import cz.upol.logicgo.misc.enums.TypeGame;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class GameTypeConverter implements AttributeConverter<TypeGame, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TypeGame typeGame) {
        if (typeGame == null) {
            return null;
        }
        return typeGame.getValue();
    }

    @Override
    public TypeGame convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return TypeGame.getGameType(dbData);
    }
}
