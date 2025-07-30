package cz.upol.logicgo.export;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import jakarta.persistence.Transient;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cz.upol.logicgo.misc.Utils.dateTimeFormatter;

public class ExportedGamesEntry {
    private final Long id;
    private final GameType gameType;
    private final TypeGame typeGame;
    private final Difficulty difficulty;
    private final RegionLayout regionLayout;
    private final LocalDateTime generatedAt;
    private final short count;

    public ExportedGamesEntry(Long id, GameType gameType, TypeGame typeGame, RegionLayout regionLayout, Difficulty difficulty, LocalDateTime generatedAt, short count) {
        this.id = id;
        this.gameType = gameType;
        this.typeGame = typeGame;
        this.difficulty = difficulty;
        this.regionLayout = regionLayout;
        this.generatedAt = generatedAt;
        this.count = count;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getGeneratedAt() {
        return dateTimeFormatter(generatedAt);
    }



    public Long getId() {
        return id;
    }

    public short getCount() {
        return count;
    }

    public GameType getGameType() {
        return gameType;
    }

    public TypeGame getTypeGame() {
        return typeGame;
    }

    public RegionLayout getRegionLayout() {
        return regionLayout;
    }
}
