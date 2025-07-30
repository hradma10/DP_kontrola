package cz.upol.logicgo.misc.export.DTOs.games;

import cz.upol.logicgo.misc.export.DTOs.GameDTO;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.Status;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;

import java.time.LocalDateTime;
import java.util.List;

public class SudokuDTO extends GameDTO {
    final private SudokuType sudokuType;
    final private byte[] board;
    final private byte[] solutionBoard;
    final private byte[] startingBoard;
    final private byte[] undoStack;
    final private byte[] redoStack;

    public SudokuDTO(long id, int height, int width, long seed, LocalDateTime creationDate, TypeGame typeGame, LocalDateTime lastPlayed, Status status, long elapsedTime, String notes, Difficulty difficulty, long userId, List<SettingDTO> settings, SudokuType sudokuType, byte[] board, byte[] solutionBoard, byte[] startingBoard, byte[] undoStack, byte[] redoStack) {
        super(id, height, width, seed, creationDate, typeGame, lastPlayed, status, elapsedTime, notes, difficulty, userId, settings);
        this.sudokuType = sudokuType;
        this.board = board;
        this.solutionBoard = solutionBoard;
        this.startingBoard = startingBoard;
        this.undoStack = undoStack;
        this.redoStack = redoStack;
    }

    public SudokuType getSudokuType() {
        return sudokuType;
    }


    public byte[] getBoard() {
        return board;
    }

    public byte[] getSolutionBoard() {
        return solutionBoard;
    }

    public byte[] getStartingBoard() {
        return startingBoard;
    }

    public byte[] getUndoStack() {
        return undoStack;
    }

    public byte[] getRedoStack() {
        return redoStack;
    }
}
