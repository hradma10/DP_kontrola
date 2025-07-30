package cz.upol.logicgo.misc.enums.settings.gameTypes;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.misc.enums.TypeGame;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cz.upol.logicgo.algorithms.sudoku.layout.SudokuLayoutsLoader.getLayoutsForSize;

public enum SudokuType implements GameType {
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    TWELVE(12),
    SIXTEEN(16);

    private static final Map<Integer, SudokuType> GRID_SIZE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(SudokuType::getGridSize, t -> t));
    private static final Map<String, SudokuType> DESCRIPTION_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(SudokuType::getDescription, t -> t));

    private final int gridSize;
    private final int cellCount;
    private final String description;
    private final Integer[] numbers;
    private final List<RegionLayout> regionLayouts;

    public static int calcCellCount(int gridSize){
        return gridSize * gridSize;
    }

    SudokuType(int gridSize) {
        this.gridSize = gridSize;
        this.cellCount = calcCellCount(gridSize);
        this.description = buildDescription(gridSize);
        this.numbers = genNumbers(1, gridSize);
        this.regionLayouts = getLayoutsForSize(gridSize);
    }

    public static String buildDescription(int size) {
        return String.format("%sx%s Sudoku", size, size);
    }

    public static Integer[] genNumbers(int start, int stop) {
        if (stop < start) {
            throw new IllegalArgumentException();
        }
        return IntStream.range(0, stop - start + 1).mapToObj(i -> start + i).toArray(Integer[]::new);
    }

    public static SudokuType getTypeByGridSize(int gridSize) {
        return GRID_SIZE_MAP.get(gridSize);
    }

    public static SudokuType getInstanceByDescription(String description) {
        return DESCRIPTION_MAP.get(description);
    }

    public boolean isSupported(int num){
        return num >= 0 && num <= gridSize;
    }

    @Override
    public TypeGame getTypeGame() {
        return TypeGame.SUDOKU;
    }

    public int getGridSize() {
        return gridSize;
    }

    public String getDescription() {
        return description;
    }

    public Integer[] getPossibleNumbers() {
        return numbers;
    }

    @Override
    public String toString() {
        return description + " (" + gridSize + "x" + gridSize + ")";
    }

    public int getCellCount() {
        return cellCount;
    }

    public List<RegionLayout> getRegionLayouts() {
        return regionLayouts;
    }
}
