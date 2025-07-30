package cz.upol.logicgo.misc.enums.settings;


public interface SettingKey {


    String getName();

    String getNamespace();

    String getQualifiedName();

    Class<?> getClazz();

    Object getDefaultValue();
}
