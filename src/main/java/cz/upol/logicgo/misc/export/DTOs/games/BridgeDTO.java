package cz.upol.logicgo.misc.export.DTOs.games;

import cz.upol.logicgo.misc.export.DTOs.GameDTO;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.Status;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;

import java.time.LocalDateTime;
import java.util.List;

public class BridgeDTO extends GameDTO {

    public BridgeDTO(long id, int height, int width, long seed, LocalDateTime creationDate, TypeGame typeGame, LocalDateTime lastPlayed, Status status, long elapsedTime, String notes, Difficulty difficulty, long userId, List<SettingDTO> settings) {
        super(id, height, width, seed, creationDate, typeGame, lastPlayed, status, elapsedTime, notes, difficulty, userId, settings);
    }
}
