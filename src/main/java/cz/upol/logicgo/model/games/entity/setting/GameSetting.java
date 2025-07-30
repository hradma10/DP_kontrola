package cz.upol.logicgo.model.games.entity.setting;

import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;
import cz.upol.logicgo.model.games.Game;
import jakarta.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "GameSetting.findByGame",
                query = "SELECT gs FROM GameSetting gs WHERE gs.game = :game"
        ),
        @NamedQuery(
                name = "GameSetting.findNamesByUser",
                query = "SELECT gs.key FROM GameSetting gs WHERE gs.game = :game"
        ),
        @NamedQuery(
                name = "GameSetting.findAll",
                query = "SELECT gs FROM GameSetting gs"
        ),
})
@Table(name = "game_settings")
public class GameSetting extends Setting {

    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameSetting() {
        super();
    }

    @Override
    public long getOwnerId() {
        if (game == null) return -1;
        return game.getId();
    }

    public GameSetting(SettingKey key, String value, Game game) {
        super(key, value);
        this.game = game;
    }

    public GameSetting(SettingDTO settingDTO, Game game) {
        SettingKey key = settingDTO.getKey();
        String value = settingDTO.getValue();
        super(key, value);
        this.game = game;
    }
}

