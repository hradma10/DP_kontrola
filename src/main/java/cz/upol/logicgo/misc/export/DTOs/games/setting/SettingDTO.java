package cz.upol.logicgo.misc.export.DTOs.games.setting;

import cz.upol.logicgo.misc.enums.settings.SettingKey;

public class SettingDTO {
    private long id;
    private SettingKey key;
    private String value;
    private long ownerId ;

    public SettingDTO(long id, SettingKey key, String value, long ownerId) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.ownerId = ownerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SettingKey getKey() {
        return key;
    }

    public void setKey(SettingKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
