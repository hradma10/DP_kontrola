package cz.upol.logicgo.util.generate;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.algorithms.sudoku.layout.SudokuLayoutsLoader;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class SeedFactory {


    public static synchronized long generateSeed() {
        return System.nanoTime()
                ^ System.currentTimeMillis()
                ^ Double.doubleToLongBits(Math.random());
    }

    public static long generateDailySeed(){
        LocalDate today = LocalDate.now();
        return today.getYear() * 10000L + today.getMonthValue() * 100L + today.getDayOfMonth();
    }

    /**
     * vytvoří seed z libovolného vstupu
     */
    public static long hashToLong(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            long result = 0;
            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (hash[i] & 0xFF);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    public static ParsedSeed parseSeed(String input) {
        if (input == null || input.isBlank()) {
            return new ParsedSeed(SudokuType.NINE, SudokuType.NINE.getRegionLayouts().getFirst(), Difficulty.MEDIUM, generateSeed());
        }

        String[] parts = input.split("-", 4);

        if (parts.length == 4) {
            try {
                int gridSize = Integer.parseInt(parts[0]);
                SudokuType type = SudokuType.getTypeByGridSize(gridSize);

                int regionLayoutType = Integer.parseInt(parts[1]);

                RegionLayout regionLayout = SudokuLayoutsLoader.getTypeOfRegionSizeBySize(gridSize, regionLayoutType);

                Difficulty difficulty = Difficulty.getDifficultyFromValue(Integer.parseInt(parts[2]));

                if (type != null && regionLayout != null && difficulty != null) {
                    return new ParsedSeed(type, regionLayout, difficulty, resolveSeed(parts[3]));
                }
            } catch (NumberFormatException ignored) {}
        } else if (parts.length == 1) {
            long seed = resolveSeed(input);
            return new ParsedSeed(SudokuType.NINE, SudokuType.NINE.getRegionLayouts().getFirst(), Difficulty.MEDIUM, seed);
        }

        // fallback
        return new ParsedSeed(SudokuType.NINE, SudokuType.NINE.getRegionLayouts().getFirst(), Difficulty.MEDIUM, resolveSeed(input));
    }


    public static long resolveSeed(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return hashToLong(input);
        }
    }

    public record ParsedSeed(SudokuType sudokuType, RegionLayout regionLayout, Difficulty difficulty, long seed){

    }
}
