package cz.upol.logicgo.misc.enums;

public enum Status {
    IN_PROGRESS(0, "In progress"),
    FINISHED(1, "Finished"),
    FAILED(2, "Failed");


    final int value;
    final String description;

    Status(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public static Status getStatus(int status) {
        return switch (status) {
            case 0 -> IN_PROGRESS;
            case 1 -> FINISHED;
            case 2 -> FAILED;
            default -> null;
        };
    }

    public String getDescription() {
        return description;
    }

    public static Status getInstanceByDescription(String description) {
        for (Status status : Status.values()){
            if (status.getDescription().equals(description)){
                return status;
            }
        }
        return null;
    }
}
