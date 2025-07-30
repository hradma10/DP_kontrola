package cz.upol.logicgo.misc.enums;

import cz.upol.logicgo.misc.Messages;
import cz.upol.logicgo.misc.dataStructures.SimpleBiMap;

public enum HintType implements Parseable {
    RANDOM_CELL("hint_type.random_cell"),
    CHOSEN_CELL("hint_type.chosen_cell"),

    CHECK_ROW("hint_type.check_row"),
    CHECK_COLUMN("hint_type.check_column"),
    CHECK_CELL("hint_type.check_cell"),
    CHECK_BOX("hint_type.check_box"),
    CHECK_VALIDITY("hint_type.check_validity"),
    CHECK_CANDIDATES("hint_type.check_candidates"),;


    final String name;

    private static SimpleBiMap<String, String> translations = null;

    public String getDescription(){
        return this.name;
    }

    public static HintType getInstanceByDescription(String description) {
        for (HintType hintType : HintType.values()){
            if (hintType.getDescription().equals(description)){
                return hintType;
            }
        }
        return null;
    }

    private static SimpleBiMap<String, String> createBiMap(){
        SimpleBiMap<String, String> bimap = new SimpleBiMap<>();
        for (HintType hintType : HintType.values()){
            bimap.put(hintType.getDescription(), Messages.getFormatted(hintType.getDescription()));
        }
        return bimap;
    }

    public static String getTranslation(HintType hintType) {
        synchronized (SimpleBiMap.class) {
            if (translations == null) {
                translations = createBiMap();
            }
        }
        return translations.getForward(hintType.getDescription());
    }

    public static String getNaming(String translation){
        synchronized (SimpleBiMap.class) {
            if (translations == null) {
                translations = createBiMap();
            }
        }
        return translations.getBackward(translation);
    }

    HintType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean matches(String input) {
        return getDescription().equals(input);
    }
}
