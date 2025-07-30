package cz.upol.logicgo.model.games.service;

import cz.upol.logicgo.model.games.dao.SettingDAO;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.setting.UserSetting;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.settings.UserSettings;

import java.util.ArrayList;
import java.util.List;

public class SettingsService {

    private SettingDAO settingDAO = new SettingDAO();

    SettingsService() {

    }

    /**
     * zkontroluje, zda má uživatel nastavena všechna nastavení
     */
    public void checkUserSettings(User user) {
        List<UserSettings> settingNames = UserSettings.getAllSettingNames();

        List<UserSettings> settings = settingDAO.getUserSettingNames(user);

        ArrayList<UserSettings> unsetSettings = new ArrayList<>();

        for (UserSettings setting : settingNames) {
            if (!settings.contains(setting)) {
                unsetSettings.add(setting);
            }
        }

        ArrayList<Setting> unsetSettingNames = new ArrayList<>();
        unsetSettings.forEach(setting -> {
            Object defaultValue = setting.getDefaultValue();
            UserSetting newSetting = new UserSetting(setting, String.valueOf(defaultValue), user);
            unsetSettingNames.add(newSetting);
        });

        settingDAO.saveAll(unsetSettingNames);
    }

    public void checkDefaultGameSettingsForUser(User user) {
        settingDAO.setDefaultGameSettings(user);
    }

}
