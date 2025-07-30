package cz.upol.logicgo.model.games.entity.mazes;

import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.export.DTOs.games.MazeDTO;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "mazes")
public class Maze extends Game {

    public Maze(int height, int width, TypeGame typeGame, Difficulty difficulty, User player) {
        super(height, width, typeGame, difficulty, player);
    }

    public Maze(int height, int width, long seed, TypeGame typeGame, Difficulty difficulty, User player) {
        super(height, width, seed, typeGame, difficulty, player);
    }

    public Maze(int height, int width, long seed, int id, Status status, TypeGame typeGame, int duration, Difficulty difficulty, User player) {
        //super(height, width, seed, id, status, typeGame, duration, difficulty);
    }

    public Maze() {

    }

    public Maze(MazeDTO mazeDTO, User user) {
        super(mazeDTO, user);
    }

    @Override
    @Transient
    public GameType getTypeOfGame() {
        return null;
    }
}
