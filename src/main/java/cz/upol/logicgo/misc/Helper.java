package cz.upol.logicgo.misc;

import cz.upol.logicgo.misc.enums.Parseable;
import cz.upol.logicgo.misc.enums.settings.SettingKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Helper {
    private static final Map<Class<?>, Function<String, ?>> PARSERS = Map.of(
            String.class, s -> s,
            Integer.class, Integer::parseInt,
            Long.class, Long::parseLong,
            Boolean.class, Boolean::parseBoolean,
            Double.class, Double::parseDouble,
            Float.class, Float::parseFloat,
            Short.class, Short::parseShort,
            Byte.class, Byte::parseByte
    );


    public static <T, K, V> HashMap<K, V> mapBy(List<T> list, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return list.stream().collect(Collectors.toMap(
                keyMapper,
                valueMapper,
                (a, b) -> b,
                HashMap::new
        ));
    }

    public static <T, K> HashMap<K, T> mapBy(List<T> list, Function<T, K> keyMapper) {
        return list.stream().collect(Collectors.toMap(
                keyMapper,
                Function.identity(),
                (a, b) -> b,
                HashMap::new
        ));
    }


    public static <T> T parseValueAs(String value, Class<T> targetType) {
        Object result = parseValue(value, targetType);
        return targetType.cast(result);
    }

    public static <T> T parseSetting(SettingKey setting, String value) {
        return (T) parseValue(value, setting.getClazz());
    }

    public static <T> T getSetting(SettingKey setting, Map<SettingKey, String> settings) {
        return (T) parseValue(settings.get(setting), setting.getClazz());
    }

    public static Object parseValue(String value, Class<?> targetType) {
        Function<String, ?> parser = PARSERS.get(targetType);
        if (parser != null) return parser.apply(value);

        if (Enum.class.isAssignableFrom(targetType) && Parseable.class.isAssignableFrom(targetType)) {
            return parseEnum(targetType, value);
        }

        throw new IllegalArgumentException("Unsupported type: " + targetType);
    }
    private static <T extends Enum<T> & Parseable> T parseEnum(Class<?> targetType, String value) {
        Class<T> enumClass = (Class<T>) targetType;
        return Parseable.parse(enumClass, value);
    }


}
