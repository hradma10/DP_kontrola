package cz.upol.logicgo.misc.export.DTOs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.export.DTOs.games.BridgeDTO;
import cz.upol.logicgo.misc.export.DTOs.games.MazeDTO;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;
import cz.upol.logicgo.misc.export.DTOs.games.SudokuDTO;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SudokuDTO.class, name = "sudoku"),
        @JsonSubTypes.Type(value = MazeDTO.class, name = "maze"),
        @JsonSubTypes.Type(value = BridgeDTO.class, name = "bridge"),
})
public abstract class GameDTO {

    private long id;
    private int height;
    private int width;
    private long seed;
    private LocalDateTime creationDate;
    private TypeGame typeGame;
    private LocalDateTime lastPlayed;
    private Status status;
    private long elapsedTime;
    private String notes;
    private Difficulty difficulty;
    private long userId;
    private List<SettingDTO> settings;

    public GameDTO(long id, int height, int width, long seed, LocalDateTime creationDate, TypeGame typeGame, LocalDateTime lastPlayed, Status status, long elapsedTime, String notes, Difficulty difficulty, long userId, List<SettingDTO> settings) {
        this.id = id;
        this.height = height;
        this.width = width;
        this.seed = seed;
        this.creationDate = creationDate;
        this.typeGame = typeGame;
        this.lastPlayed = lastPlayed;
        this.status = status;
        this.elapsedTime = elapsedTime;
        this.notes = notes;
        this.difficulty = difficulty;
        this.userId = userId;
        this.settings = settings;
    }

    public long getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getSeed() {
        return seed;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public TypeGame getGameType() {
        return typeGame;
    }

    public LocalDateTime getLastPlayed() {
        return lastPlayed;
    }

    public Status getStatus() {
        return status;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public String getNotes() {
        return notes;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public long getUserId() {
        return userId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setGameType(TypeGame typeGame) {
        this.typeGame = typeGame;
    }

    public void setLastPlayed(LocalDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public TypeGame getTypeGame() {
        return typeGame;
    }

    public List<SettingDTO> getSettings() {
        return settings;
    }
}


