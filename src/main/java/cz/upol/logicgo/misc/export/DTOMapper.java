package cz.upol.logicgo.misc.export;

import cz.upol.logicgo.misc.export.DTOs.GameDTO;
import cz.upol.logicgo.misc.export.DTOs.UserDTO;
import cz.upol.logicgo.misc.export.DTOs.games.BridgeDTO;
import cz.upol.logicgo.misc.export.DTOs.games.MazeDTO;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;
import cz.upol.logicgo.misc.export.DTOs.games.SudokuDTO;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.entity.bridges.Bridge;
import cz.upol.logicgo.model.games.entity.mazes.Maze;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.util.SudokuCellConverters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cz.upol.logicgo.util.SudokuCellConverters.serializeBoard;

public class DTOMapper {

    public static ArrayList<UserDTO> serializeUser(List<User> users) {
        return serializeUser(users.toArray(new User[0]));
    }

    public static ArrayList<UserDTO> serializeUser(User... users) {
        ArrayList<UserDTO> userDTOs = new ArrayList<>();
        for (User u : users) {
            ArrayList<SettingDTO> settings = serializeSettings(u.getUserSettings());
            UserDTO user = new UserDTO(
                    u.getId(),
                    u.getUsername(),
                    u.getCreatedAt(),
                    u.getLastLogged(),
                    u.getHashedPassword(),
                    settings);
            userDTOs.add(user);
        }

        return userDTOs;
    }

    public static ArrayList<Game> deserializeGames(List<GameDTO> gameDTOs, HashMap<Long, User> users) {
        ArrayList<Game> games = new ArrayList<>();
        for (GameDTO gameDTO : gameDTOs) {
            User user = users.get(gameDTO.getUserId());
            Game game = switch (gameDTO) {
                case SudokuDTO sudokuDTO -> deserializeSudoku(sudokuDTO, user);
                case MazeDTO mazeDTO -> deserializeMaze(mazeDTO, user);
                case BridgeDTO bridgeDTO -> deserializeBridge(bridgeDTO, user);
                default -> null;
            };
            games.add(game);
        }
        return games;
    }

    public static Sudoku deserializeSudoku(SudokuDTO sudokuDTO, User user) {
        SudokuCell[][] board = SudokuCellConverters.deserializeBoard(sudokuDTO.getBoard());
        SudokuCell[][] startingBoard = SudokuCellConverters.deserializeBoard(sudokuDTO.getBoard());
        SudokuCell[][] solutionBoard = SudokuCellConverters.deserializeBoard(sudokuDTO.getBoard());
        Sudoku.Boards boards = new Sudoku.Boards(board, solutionBoard, startingBoard);
        return new Sudoku(sudokuDTO, user, boards); // TODO fix the deserialization of settings in user and games
    }

    public static Maze deserializeMaze(MazeDTO mazeDTO, User user) {
        return new Maze(mazeDTO, user);
    }

    public static Bridge deserializeBridge(BridgeDTO bridgeDTO, User user) {
        return new Bridge(bridgeDTO, user);
    }

    public static <T extends Setting> ArrayList<SettingDTO> serializeSettings(List<T> settings) {
        return serializeSettings(settings.toArray(new Setting[0]));
    }

    public static ArrayList<SettingDTO> serializeSettings(Setting... settings) {
        ArrayList<SettingDTO> settingsDTOs = new ArrayList<>();
        for (Setting s : settings) {
            SettingDTO setting = new SettingDTO(
                    s.getId(),
                    s.getKey(),
                    s.getValue(),
                    s.getOwnerId());
            settingsDTOs.add(setting);
        }

        return settingsDTOs;
    }

    public static ArrayList<GameDTO> serializeGame(Game... games) {
        ArrayList<GameDTO> gameDTOs = new ArrayList<>();
        for (Game g : games) {
            List<SettingDTO> settings = serializeSettings(g.getSettings());
            switch (g) {
                case Sudoku sudoku -> gameDTOs.add(serializeSudoku(sudoku, settings));
                case Maze maze -> gameDTOs.add(serializeMaze(maze, settings));
                case Bridge bridge -> gameDTOs.add(serializeBridge(bridge, settings));
                default -> throw new IllegalStateException("Unexpected value: " + g); // TODO změnit
            }
        }
        return gameDTOs;

    }

    public static SudokuDTO serializeSudoku(Sudoku s, List<SettingDTO> settings) {
        return new SudokuDTO(
                s.getId(),
                s.getHeight(),
                s.getWidth(),
                s.getSeed(),
                s.getCreationDate(),
                s.getGameType(),
                s.getLastPlayed(),
                s.getStatus(),
                s.getElapsedTime().toMillis(),
                s.getNotes(),
                s.getDifficulty(),
                s.getPlayer().getId(),
                settings,
                s.getType(),
                serializeBoard(s.getBoard()),
                serializeBoard(s.getSolutionBoard()),
                serializeBoard(s.getStartingBoard()),
                s.getUndoStack(),
                s.getRedoStack());
    }

    public static MazeDTO serializeMaze(Maze m, List<SettingDTO> settings) {
        return new MazeDTO(
                m.getId(),
                m.getHeight(),
                m.getWidth(),
                m.getSeed(),
                m.getCreationDate(),
                m.getGameType(),
                m.getLastPlayed(),
                m.getStatus(),
                m.getElapsedTime().toMillis(),
                m.getNotes(),
                m.getDifficulty(),
                m.getPlayer().getId(),
                settings);
    }

    public static ArrayList<GameDTO> serializeGame(List<Game> games) {
        return serializeGame(games.toArray(new Game[0]));
    }

    public static BridgeDTO serializeBridge(Bridge b, List<SettingDTO> settings) {
        return new BridgeDTO(
                b.getId(),
                b.getHeight(),
                b.getWidth(),
                b.getSeed(),
                b.getCreationDate(),
                b.getGameType(),
                b.getLastPlayed(),
                b.getStatus(),
                b.getElapsedTime().toMillis(),
                b.getNotes(),
                b.getDifficulty(),
                b.getPlayer().getId(),
                settings);
    }
}
