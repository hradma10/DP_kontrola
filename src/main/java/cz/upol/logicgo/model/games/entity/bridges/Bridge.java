package cz.upol.logicgo.model.games.entity.bridges;

import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.export.DTOs.games.BridgeDTO;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "bridge")
public class Bridge extends Game {
    public Bridge(int height, int width, TypeGame typeGame, Difficulty difficulty, User player) {
        super(height, width, typeGame, difficulty, player);
    }

    public Bridge(int height, int width, long seed, TypeGame typeGame, Difficulty difficulty, User player) {
        super(height, width, seed, typeGame, difficulty, player);
    }



    public Bridge(int height, int width, long seed, int id, Status status, TypeGame typeGame, int duration, Difficulty difficulty, User player) {
        //super(height, width, seed, id, status, typeGame, duration, difficulty);
    }

    public Bridge() {

    }

    public Bridge(BridgeDTO bridgeDTO, User user) {
        super(bridgeDTO, user);
    }

    @Override
    @Transient
    public GameType getTypeOfGame() {
        return null;
    }
}
