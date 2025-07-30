package cz.upol.logicgo.misc.enums;

import cz.upol.logicgo.misc.Messages;
import cz.upol.logicgo.misc.dataStructures.SimpleBiMap;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;

public enum Difficulty {

    EASY(0, "difficulty.easy"),
    MEDIUM(1, "difficulty.medium"),
    HARD(2, "difficulty.hard"),
    VERY_HARD(3, "difficulty.very_hard");

    final int value;

    final String name;

    Difficulty(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static Difficulty getDifficultyFromValue(int difficulty) {
        return switch (difficulty) {
            case 0 -> EASY;
            case 1 -> MEDIUM;
            case 2 -> HARD;
            case 3 -> VERY_HARD;
            default -> null;
        };
    }

    private static SimpleBiMap<String, String> translations = null;


    private static SimpleBiMap<String, String> createBiMap(){
        SimpleBiMap<String, String> bimap = new SimpleBiMap<>();
        for (Difficulty difficulty : Difficulty.values()){
            bimap.put(difficulty.getName(), Messages.getFormatted(difficulty.getName()));
        }
        return bimap;
    }

    public static String getTranslation(Difficulty difficulty) {
        synchronized (SimpleBiMap.class) {
            if (translations == null) {
                translations = createBiMap();
            }
        }
        return translations.getForward(difficulty.getName());
    }

    public static String getNaming(String translation){
        synchronized (SimpleBiMap.class) {
            if (translations == null) {
                translations = createBiMap();
            }
        }
        return translations.getBackward(translation);
    }

    public String getName() {
        return name;
    }

    public static Difficulty getInstanceByDescription(String description) {
        for (Difficulty difficulty : Difficulty.values()){
            if (difficulty.getName().equals(description)){
                return difficulty;
            }
        }
        return null;
    }

    public static int getSudokuCellsToRemove(SudokuType sudokuType, Difficulty difficulty, long seed) {
        int gridSize = sudokuType.getGridSize(); // e.g., 9 for 9x9
        int totalCells = gridSize * gridSize;

        double ratio = 0.4 + (difficulty.getValue() * 0.1);
        int baseRemove = (int) (totalCells * ratio);

        int minClues = switch (sudokuType) {
            case NINE -> 26;  // 81 - 55 removed
            case SIX -> 18;  // 36 - 18 removed
            case FOUR -> 5;   // 16 - 11 removed
            case SIXTEEN -> 116;  // 256 - 140 TODO finish problem gen
            case FIVE      -> 5; // 25 - 20
            case SEVEN -> 13; // 49 - 36 maybe 2 less  TODO finish
            case TEN -> 36; //   100 - 64                diagonal problem
            case TWELVE -> 54 ; // 144 - 90 až 92 TODO finish problem gen
            case EIGHT -> 24; // 64 - 40 removed, maybe more TODO finish
        };

        minClues = Math.max(1, Math.min(minClues, totalCells - 1));
        int maxRemovable = totalCells - minClues;

        int removableCells = Math.min(baseRemove, maxRemovable);

        int variation = (int) (Math.abs(seed) % Math.max(1, removableCells / 10));

        return Math.min(removableCells + variation, maxRemovable);
    }



}
