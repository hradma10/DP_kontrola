package cz.upol.logicgo.model.games;

import cz.upol.logicgo.misc.NodeSnapshotting;
import cz.upol.logicgo.misc.Utils;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.export.DTOs.GameDTO;
import cz.upol.logicgo.model.games.entity.setting.GameSetting;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.Status;
import cz.upol.logicgo.util.converters.*;
import cz.upol.logicgo.util.generate.SeedFactory;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;


@NamedQueries({
        @NamedQuery(
                name = "Game.gamesWithSeedExistsForPlayer",
                query = "SELECT g FROM Game g WHERE g.seed = :seed and g.player = :player"
        ),
        @NamedQuery(
                name = "Game.gamesWithSeedExistsFinishedForPlayer",
                query = "SELECT count(g) as count FROM Game g WHERE g.seed = :seed and g.player = :player and g.status = :status"
        ),
        @NamedQuery(
                name = "Game.gamesPlayed",
                query = "SELECT count(g) as count FROM Game g WHERE g.player = :user"
        ),
        @NamedQuery(
                name = "Game.gamesByStatusCount",
                query = "SELECT count(g) as count FROM Game g WHERE g.player = :user and g.status = :status"
        ),
        @NamedQuery(
                name = "Game.updateGameNotes",
                query = "UPDATE Game g SET g.notes = :note WHERE g.id = :id"
        ),
        @NamedQuery(
                name = "Game.getGameNotes",
                query = "SELECT g.notes FROM Game g WHERE g.id = :id"
        ),
        @NamedQuery(
                name = "Game.updateThumbnailName",
                query = "UPDATE Game g SET g.thumbnailName = :name WHERE g.id = :id"
        ),
        @NamedQuery(
                name = "Game.getThumbnailName",
                query = "SELECT g.thumbnailName FROM Game g WHERE g.id = :id"
        ),
        @NamedQuery(
                name = "Game.deleteById",
                query = "DELETE Game g WHERE g.id = :id"
        ),
        @NamedQuery(
                name = "Game.lastPlayedNonFinishedGame",
                query = "SELECT g FROM Game g WHERE g.status = :status and g.player = :player ORDER BY g.lastPlayed DESC"
        )
})
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class Game  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "game_id")
    private Long id;

    @Column(nullable = false, name = "height")
    private int height;

    @Column(nullable = false, name = "width")
    private int width;

    @Transient
    private Random randomInstance;

    @Column(nullable = false, name = "seed")
    private long seed;

    @Column(nullable = false, name = "date_created")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime creationDate;

    @Column(nullable = false, name = "game_type")
    @Convert(converter = GameTypeConverter.class)
    private TypeGame typeGame;

    @Column(nullable = false, name = "last_played")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastPlayed;

    @Column(nullable = false, name = "status")
    @Convert(converter = StatusConverter.class)
    private Status status;

    @Column(nullable = false, name = "elapsed_time")
    private Duration elapsedTime;

    @Column(name="notes")
    @Convert(converter = NotesConverter.class)
    private String notes;

    @Column(nullable = false, name = "difficulty")
    @Convert(converter = DifficultyConverter.class)
    private Difficulty difficulty;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private User player;

    @Column(name = "thumbnail_name")
    private String thumbnailName;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameSetting> settings;

    public void prePersist() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }

        if (lastPlayed == null) {
            lastPlayed = LocalDateTime.now();
        }

        if (elapsedTime == null) {
            elapsedTime = Duration.ZERO;
        }

        if (status == null) {
            status = Status.IN_PROGRESS;
        }

    }

    @PreUpdate
    public void preUpdate() {
        lastPlayed = LocalDateTime.now();
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    @PreRemove
    public void preRemove() {
        NodeSnapshotting.removeThumbnail(thumbnailName);
    }

    public Game() {
        randomInstance = new Random(seed);
    }

    public Game(int height, int width, TypeGame typeGame, Difficulty difficulty, User player) {
        this.height = height;
        this.width = width;
        this.seed = SeedFactory.generateSeed();
        this.randomInstance = new Random(seed);
        this.status = Status.IN_PROGRESS;
        this.typeGame = typeGame;
        this.elapsedTime = Duration.ZERO;
        this.difficulty = difficulty;
        this.player = player;
    }

    public Game(int height, int width, long seed, TypeGame typeGame, Difficulty difficulty, User player) {
        this.height = height;
        this.width = width;
        this.seed = seed;
        this.randomInstance = new Random(seed);
        this.status = Status.IN_PROGRESS;
        this.typeGame = typeGame;
        this.elapsedTime = Duration.ZERO;
        this.difficulty = difficulty;
        this.player = player;
    }

    public Game(GameDTO gameDTO, User player) {
        this.height = gameDTO.getHeight();
        this.width = gameDTO.getWidth();
        this.seed = gameDTO.getSeed();
        this.randomInstance = new Random(seed);
        this.status = gameDTO.getStatus();
        this.typeGame = gameDTO.getGameType();
        this.elapsedTime = Duration.of(gameDTO.getElapsedTime(), ChronoUnit.MILLIS);
        this.difficulty = gameDTO.getDifficulty();
        this.player = player;
        this.creationDate = gameDTO.getCreationDate();
        this.lastPlayed = gameDTO.getLastPlayed();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Random getRandomInstance() {
        return randomInstance;
    }

    public long getSeed() {
        return seed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeGame getGameType() {
        return typeGame;
    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Duration elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public long getId() {
        return id;
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

    public void setRandomInstance(Random randomInstance) {
        this.randomInstance = randomInstance;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setGameType(TypeGame typeGame) {
        this.typeGame = typeGame;
    }

    public LocalDateTime getLastPlayed() {
        return lastPlayed;
    }

    @Transient
    public String getFormattedLastPlayed() {
        return Utils.dateTimeFormatter(lastPlayed);
    }

    public void setLastPlayed(LocalDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<GameSetting> getSettings() {
        return settings;
    }

    public List<GameSetting> getSettingsAsMap() {
        return settings;
    }


    public void setSettings(List<GameSetting> settings) {
        this.settings = settings;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

    public abstract GameType getTypeOfGame();
}
