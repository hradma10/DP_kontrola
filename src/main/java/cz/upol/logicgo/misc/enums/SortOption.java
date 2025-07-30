package cz.upol.logicgo.misc.enums;

public enum SortOption {
    LAST_PLAYED("lastPlayed"),
    TYPE("typeGame"),
    STATUS("status"),
    ELAPSED_TIME("elapsedTime"),
    DIFFICULTY("difficulty");


    private final String description;

    SortOption(String description) {
        this.description = description;
    }

    public static SortOption getInstanceByDescription(String description) {
        for (SortOption sortOption : SortOption.values()){
            if (sortOption.getDescription().equals(description)){
                return sortOption;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }
}
