package cz.upol.logicgo.algorithms.sudoku.layout;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static cz.upol.logicgo.misc.Messages.getFormatted;

public class SudokuLayoutsLoader {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<Integer, List<RegionLayout>> layoutsBySize = new HashMap<>();
    private static final Map<String, RegionLayout> layoutsByName = new HashMap<>();
    private static final Map<String, String> layoutsTranslated = new HashMap<>();

    static {
        try (InputStream is = SudokuLayoutsLoader.class.getResourceAsStream("/cz/upol/logicgo/game/sudoku/layouts.json")) {
            if (is == null) {
                throw new IOException("layouts.json not found in resources");
            }
            load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(InputStream input) throws IOException {
        SudokuLayouts layouts = mapper.readValue(input, SudokuLayouts.class);
        layoutsBySize.clear();
        layouts.getLayouts().forEach(layout -> layoutsBySize.put(layout.getSize(), layout.getRegionLayouts()));

        layouts.getLayouts().stream().flatMap(layout ->
                layout.getRegionLayouts().stream())
                .forEach(regionLayout -> layoutsByName.put(regionLayout.getName(), regionLayout));

        for (SudokuLayouts.SizeLayouts layout : layouts.getLayouts()) {
            for (RegionLayout regionLayout : layout.getRegionLayouts()) {
                String oldName = regionLayout.getName();
                String translated = getFormatted(oldName);
                regionLayout.setTranslatedName(translated);
                layoutsTranslated.put(translated, oldName);
            }
        }
    }

    public static List<RegionLayout> getLayoutsForSize(int size) {
        return layoutsBySize.getOrDefault(size, Collections.emptyList());
    }

    public static RegionLayout getLayoutsByName(String name) {
        return layoutsByName.getOrDefault(name, null);
    }

    public static RegionLayout getTypeOfRegionSizeBySize(int size, int type) {
        var list = layoutsBySize.getOrDefault(size, null);
        return list.stream().filter(layout -> type == layout.getType()).findFirst().orElse(null);
    }

    public static Map<String, String> getLayoutsTranslated() {
        return layoutsTranslated;
    }

}
