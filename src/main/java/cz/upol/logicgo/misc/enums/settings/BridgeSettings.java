package cz.upol.logicgo.misc.enums.settings;

public enum BridgeSettings implements SettingKey {
    RESPECT_RULES("respect_rules", Boolean.class, false);

    BridgeSettings(String name, Class<?> clazz, Object defaultValue) {
        this.name = name;
        this.clazz = clazz;
        this.defaultValue = defaultValue;
    }

    final String name;
    final private Class<?> clazz;
    final private Object defaultValue;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNamespace() {
        return "bridge";
    }

    @Override
    public String getQualifiedName() {
        return String.format("%s:%s", getNamespace(), getName());
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }
}
