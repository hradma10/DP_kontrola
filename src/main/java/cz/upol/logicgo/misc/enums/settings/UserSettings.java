package cz.upol.logicgo.misc.enums.settings;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public enum UserSettings implements SettingKey {
    // zobrazení času
    ZONE_ID("zone_id", ZoneId.class, ZoneId.systemDefault()),
    // cesty pro ukládání a export
    SAVE_FILE_PATH_DATA("save_file_path_data", String.class, "."),
    LOAD_FILE_PATH_DATA("load_file_path_data", String.class, "."),
    SAVE_FILE_PATH_EXPORT("save_file_path_export", String.class, "."),
    LOAD_FILE_PATH_EXPORT("load_file_path_export", String.class, "."),
    // font pro poznámky
    NOTES_FONT_NAME("notes_font_name", String.class, "Calibri"),
    NOTES_FONT_SIZE("notes_font_size", Double.class, 14d),

    // jak bude konstruováno pdf
    ORIENTATION("orientation", Boolean.class, true),
    EVERY_SUDOKU_NEW_PAGE("every_sudoku_new_page", Boolean.class, true),;


    final private String name;
    final private Class<?> clazz;
    final private Object defaultValue;

    UserSettings(String name, Class<?> clazz, Object defaultValue) {
        this.name = name;
        this.clazz = clazz;
        this.defaultValue = defaultValue;
    }

    public static UserSettings getSettingByString(String value) {
        return Arrays.stream(values()).filter(settings -> settings.name.equals(value)).findFirst().orElse(null);
    }

    public static List<UserSettings> getAllSettingNames() {
        return List.of(values());
    }


    public String getName() {
        return name;
    }

    @Override
    public String getNamespace() {
        return "user";
    }

    @Override
    public String getQualifiedName() {
        return String.format("%s:%s", getNamespace(), getName());
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
