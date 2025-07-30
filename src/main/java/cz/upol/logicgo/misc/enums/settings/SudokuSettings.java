package cz.upol.logicgo.misc.enums.settings;

import cz.upol.logicgo.misc.enums.HintType;

public enum SudokuSettings implements SettingKey {
    HINTS_ON("hints_on", Boolean.class, true),
    HINT_TYPE_CHOICE("hint_type_choice", HintType.class, HintType.CHOSEN_CELL),
    HINT_TYPE("hint_type", Boolean.class, false),
    CELL_NOTES("cell_notes", Boolean.class, false),
    TIMER("timer", Boolean.class, true),;

    SudokuSettings(String name, Class<?> clazz, Object defaultValue) {
        this.name = name;
        this.clazz = clazz;
        this.defaultValue = defaultValue;
    }

    final String name;
    final private Class<?> clazz;
    final private Object defaultValue;

    @Override
    public String getName() {
        return name;
    }

    public String getNamespace() {
        return "sudoku";
    }

    @Override
    public String getQualifiedName() {
        return String.format("%s:%s", getNamespace(), getName());
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
