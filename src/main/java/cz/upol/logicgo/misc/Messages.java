package cz.upol.logicgo.misc;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle(
            "cz.upol.logicgo.messages.messages",
            new Locale("cs")
    );

    public static String getFormatted(String key, Object... args) {
        String pattern = MESSAGES.getString(key);
        return String.format(pattern, args);
    }

}
