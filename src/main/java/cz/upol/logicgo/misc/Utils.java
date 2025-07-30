package cz.upol.logicgo.misc;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    @NotNull
    public static String dateTimeFormatter(LocalDateTime timestamp) {
        if (timestamp == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
        return timestamp.format(formatter);
    }
}
