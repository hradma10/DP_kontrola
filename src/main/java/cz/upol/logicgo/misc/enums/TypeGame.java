package cz.upol.logicgo.misc.enums;

import java.io.Serializable;

public enum TypeGame {
    SUDOKU(0, "Sudoku"),
    MAZE(1, "Maze"),
    BRIDGE(2, "Bridge");


    final int value;
    final String description;

    TypeGame(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static TypeGame getGameType(int gameType) {
        return switch (gameType) {
            case 0 -> SUDOKU;
            case 1 -> MAZE;
            case 2 -> BRIDGE;
            default -> null;
        };
    }

    public static TypeGame getInstanceByDescription(String description) {
        for (TypeGame typeGame : TypeGame.values()){
            if (typeGame.getDescription().equals(description)){
                return typeGame;
            }
        }
        return null;
    }
}
