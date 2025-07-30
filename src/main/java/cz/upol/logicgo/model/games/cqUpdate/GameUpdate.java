package cz.upol.logicgo.model.games.cqUpdate;

import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.Status;

import java.time.Duration;

public abstract class GameUpdate {

    final private long gameId;

    final private User user;

    private Status status;

    private Duration elapsedTime;


    GameUpdate(long gameId, User user) {
        this.gameId = gameId;
        this.user = user;
    }

    public GameUpdate setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public boolean hasStatus() {
        return status != null;
    }

    public GameUpdate setElapsedTime(Duration elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }

    public boolean hasElapsedTime() {
        return elapsedTime != null;
    }

    public long getGameId() {
        return gameId;
    }

    public User getUser() {
        return user;
    }
}
