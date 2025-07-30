package cz.upol.logicgo.model.games.entity.setting;

import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;
import cz.upol.logicgo.util.converters.SettingsConverter;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "setting_name")
    @Convert(converter = SettingsConverter.class)
    private SettingKey key;

    @Column(nullable = false, name = "setting_value")
    private String value;

    public Setting() {

    }

    public Setting(SettingKey key, String value) {
        this.key = key;
        this.value = value;
    }

    public Setting(SettingDTO settingDTO) {
        this.key = settingDTO.getKey();
        this.value = settingDTO.getValue();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public abstract long getOwnerId();

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Setting setting)) return false;
        return Objects.equals(getId(), setting.getId()) && Objects.equals(getKey(), setting.getKey()) && Objects.equals(getValue(), setting.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getKey(), getValue());
    }
}
