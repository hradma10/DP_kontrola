package cz.upol.logicgo.model.games.entity.setting;

import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;
import cz.upol.logicgo.model.games.entity.user.User;
import jakarta.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "UserSetting.findByUser",
                query = "SELECT us FROM UserSetting us WHERE us.user = :user"
        ),
        @NamedQuery(
                name = "UserSetting.findKeysByUser",
                query = "SELECT us.key FROM UserSetting us WHERE us.user = :user"
        ),
        @NamedQuery(
                name = "UserSetting.findAll",
                query = "SELECT us FROM UserSetting us"
        ),
})
@Table(name = "user_settings")
public class UserSetting extends Setting {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserSetting() {
        super();
    }

    public UserSetting(SettingKey key, String value, User user) {
        super(key, value);
        this.user = user;
    }

    public UserSetting(SettingDTO settingDTO, User user) {
        SettingKey key = settingDTO.getKey();
        String value = settingDTO.getValue();
        super(key, value);
        this.user = user;
    }

    @Override
    public long getOwnerId() {
        if (user == null) return -1;
        return user.getId();
    }
}
