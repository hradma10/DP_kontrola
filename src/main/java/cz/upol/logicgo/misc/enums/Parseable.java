package cz.upol.logicgo.misc.enums;

public interface Parseable {
    boolean matches(String input);

    static <T extends Enum<T> & Parseable> T parse(Class<T> enumClass, String value) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.matches(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No matching enum constant for value: " + value);
    }
}
