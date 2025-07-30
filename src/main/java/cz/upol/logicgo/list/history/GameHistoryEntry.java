package cz.upol.logicgo.list.history;

import cz.upol.logicgo.misc.enums.Difficulty;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cz.upol.logicgo.misc.Utils.dateTimeFormatter;

public class GameHistoryEntry {
    private final Long id;
    private final Image thumbnail;
    private final String title;
    private final Difficulty difficulty;
    private final LocalDateTime playedAt;

    public GameHistoryEntry(Long id, Image thumbnail, String title, Difficulty difficulty, LocalDateTime playedAt) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.title = title;
        this.difficulty = difficulty;
        this.playedAt = playedAt;
    }

    public Image getThumbnail() { return thumbnail; }
    public String getTitle() { return title; }
    public Difficulty getDifficulty() { return difficulty; }

    public String getPlayedAt() {
        return getFormattedDateTime();
    }

    private String getFormattedDateTime() {
        return dateTimeFormatter(playedAt);
    }

    public Long getId() {
        return id;
    }
}
