package cz.upol.logicgo.commands;


import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.commands.sudokuCommand.RestartSudokuCommand;
import cz.upol.logicgo.commands.sudokuCommand.SetSudokuNumberCommand;
import cz.upol.logicgo.commands.sudokuCommand.SudokuCommand;
import cz.upol.logicgo.misc.dataStructures.LimitedStack;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.util.SudokuCellConverters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * třída, která provádí veškeré reprezentované příkazy (Command instance)
 */
public class CommandExecutor {

    final private LimitedStack<Command> undoStack = new LimitedStack<>(300);
    final private LimitedStack<Command> redoStack = new LimitedStack<>(300);

    public CommandExecutor() {

    }

    /**
     * metoda pro provedené příkazu
     *
     * @param command příkaz, který má být proveden
     */
    public void execute(Command command) {
        if (command == null) return;
        command.execute();
        undoStack.push(command);
        clearRedoStack();
    }

    /**
     * pro vrácení operace zpět
     * reprezentuje akci po stisknutí tlačítka Zpět
     */
    public void undo() {
        if (canUndo()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * pro navrácení vrácené operace
     * reprezentuje akci po stisknutí tlačítka Znovu
     */
    public void redo() {
        if (canRedo()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    public void loadSudokuCommands(SudokuGame sudokuGame) {
        var undoStack = deserializeSudokuCommands(sudokuGame, sudokuGame.getSudoku().getUndoStack());
        var redoStack = deserializeSudokuCommands(sudokuGame, sudokuGame.getSudoku().getRedoStack());
        this.getUndoStack().addAll(undoStack);
        this.getRedoStack().addAll(redoStack);
    }

    public void loadMazeCommands(SavedStacks savedStacks, TypeGame typeGame, Sudoku sudokuBoard) {
    }

    public void loadBridgesCommands(SavedStacks savedStacks, TypeGame typeGame, Sudoku sudokuBoard) {
    }

    private ArrayList<SudokuCommand> deserializeSudokuCommands(SudokuGame sudokuGame, byte[] data) {
        if (data == null || data.length == 0) return new ArrayList<>();

        int currentByte = 0;
        ArrayList<SudokuCommand> commands = new ArrayList<>();

        while (currentByte < data.length) {
            byte commandType = data[currentByte++];

            switch (commandType) {
                case SetSudokuNumberCommand.type -> {
                    SudokuCommand cmd = deserializeSetNumberCommand(sudokuGame, data, sudokuGame.getSudoku().getType(), currentByte);
                    if (cmd != null) {
                        commands.add(cmd);
                        currentByte += getSetNumberCommandLength(sudokuGame.getSudoku().getType());
                    }

                }

                case RestartSudokuCommand.type -> {
                    var result = deserializeUpdateBoardCommand(sudokuGame, data, currentByte);
                    if (result != null) {
                        commands.add(result.command());
                        currentByte = result.newIndex();
                    }
                }

                default -> {
                    System.err.println("Neznámý příkaz: " + commandType);
                    return commands;
                }
            }
        }

        return commands;
    }

    private SudokuCommand deserializeSetNumberCommand(SudokuGame sudokuGame, byte[] data, SudokuType type, int offset) {
        try {
            return switch (type) {
                case SIXTEEN -> {
                    if (offset + 4 > data.length) yield null;
                    byte row = data[offset];
                    byte col = data[offset + 1];
                    byte oldNumber = data[offset + 2];
                    byte newNumber = data[offset + 3];
                    yield new SetSudokuNumberCommand(sudokuGame, row, col, oldNumber, newNumber);
                }
                case FOUR, SIX, NINE, FIVE, SEVEN, EIGHT, TEN, TWELVE -> {
                    if (offset + 2 > data.length) yield null;
                    byte first = data[offset];
                    byte second = data[offset + 1];

                    byte row = (byte) ((first >> 4) & 0xF);
                    byte col = (byte) (first & 0xF);
                    byte oldNumber = (byte) ((second >> 4) & 0xF);
                    byte newNumber = (byte) (second & 0xF);

                    yield new SetSudokuNumberCommand(sudokuGame, row, col, oldNumber, newNumber);
                }
            };
        } catch (Exception e) {
            return null;
        }
    }

    private int getSetNumberCommandLength(SudokuType type) {
        return switch (type) {
            case SIXTEEN -> 4;
            case FOUR, SIX, NINE, FIVE, SEVEN, EIGHT, TEN, TWELVE -> 2;
        };
    }
    private record RestartBoardResult(SudokuCommand command, int newIndex) {}

    private RestartBoardResult deserializeUpdateBoardCommand(SudokuGame game, byte[] data, int offset) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data, offset, data.length - offset))) {

            int oldLength = dis.readShort();
            byte[] oldCellsBytes = dis.readNBytes(oldLength);

            int newLength = dis.readShort();
            byte[] newCellsBytes = dis.readNBytes(newLength);

            int totalLength = 2 + 2 + oldLength + newLength;

            if (offset + totalLength > data.length) {
                return null;
            }


            SudokuCell[][] oldCells = SudokuCellConverters.deserializeBoard(oldCellsBytes);
            SudokuCell[][] newCells = SudokuCellConverters.deserializeBoard(newCellsBytes);

            SudokuCommand command = new RestartSudokuCommand(game, oldCells, newCells);

            return new RestartBoardResult(command, offset + totalLength);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





    public SavedStacks serializeSudokuCommandsToByteArrays() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (var command : this.getUndoStack()) {
            var serializedCommand = command.getCommandsAsBytes();
            baos.write(serializedCommand);
        }
        var byteUndoStack = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        for (var command : this.getRedoStack()) {
            var serializedCommand = command.getCommandsAsBytes();
            baos.write(serializedCommand);
        }
        var byteRedoStack = baos.toByteArray();
        return new SavedStacks(byteUndoStack, byteRedoStack);
    }

    public SavedStacks serializeMazeCommandsToByteArray(SudokuType sudokuType) {
        return null;
    }


    public SavedStacks serializeBridgesCommandsToByteArray(SudokuType sudokuType) {
        return null;
    }


    public void clearRedoStack() {
        redoStack.clear();
    }

    public void clearUndoStack() {
        undoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public LimitedStack<Command> getUndoStack() {
        return undoStack;
    }

    public LimitedStack<Command> getRedoStack() {
        return redoStack;
    }

    public record SavedStacks(byte[] undoStack, byte[] redoStack) {
        public boolean undoStackIsEmpty() {
            return undoStack == null || undoStack.length == 0;
        }

        public boolean redoStackIsEmpty() {
            return redoStack == null || redoStack.length == 0;
        }
    }

}
