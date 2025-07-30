package cz.upol.logicgo.model.games.entity.setting;

import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;
import cz.upol.logicgo.model.games.entity.user.User;
import jakarta.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "DefaultGameSetting.getAllDefaultSettingsOfUser",
                query = "SELECT dgs FROM DefaultGameSetting dgs WHERE dgs.user = :user"
        ),
        @NamedQuery(
                name = "DefaultGameSetting.findAll",
                query = "SELECT dgs FROM DefaultGameSetting dgs"
        ),
})
@Table(name = "default_game_settings")
public class DefaultGameSetting extends Setting {


    public DefaultGameSetting(User user) {
        this.user = user;
    }

    public DefaultGameSetting(SettingKey key, String value, User user) {
        super(key, value);
        this.user = user;
    }

    public DefaultGameSetting(SettingDTO settingDTO, User user) {
        super(settingDTO);
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "user")
    User user;

    public DefaultGameSetting() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public long getOwnerId() {
        return user.getId();
    }
}
