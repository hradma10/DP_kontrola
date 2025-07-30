package cz.upol.logicgo.util.converters;

import cz.upol.logicgo.misc.enums.settings.BridgeSettings;
import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.enums.settings.SudokuSettings;
import cz.upol.logicgo.misc.enums.settings.UserSettings;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import jakarta.persistence.AttributeConverter;

import java.util.HashMap;

public class SettingsConverter implements AttributeConverter<SettingKey, String> {

    private static final HashMap<String, SettingKey> map = new HashMap<>();

    static {
        for (UserSettings setting : UserSettings.values()) {
            map.put(setting.getQualifiedName(), setting);
        }

        for (SudokuSettings setting : SudokuSettings.values()) {
            map.put(setting.getQualifiedName(), setting);
        }

        for (BridgeSettings setting : BridgeSettings.values()) {
            map.put(setting.getQualifiedName(), setting);
        }
    }

    @Override
    public String convertToDatabaseColumn(SettingKey settingKey) {
        return settingKey.getQualifiedName();
    }

    @Override
    public SettingKey convertToEntityAttribute(String name) {
        return map.get(name);
    }
}
