package cz.upol.logicgo.misc.enums;

public enum Format {
    JSON("json"), YAML("yaml"), XML("xml");

    Format(String format) {
        this.format = format;
    }

    final String format;

    public String getFormat() {
        return format;
    }

    public static Format getFormatFromString(String format) {
        return switch (format) {
            case "json" -> Format.JSON;
            case "yaml" -> Format.YAML;
            case "xml" -> Format.XML;
            default -> throw new UnsupportedOperationException("Format " + format + " not supported");
        };
    }

}
