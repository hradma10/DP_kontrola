package cz.upol.logicgo.model.games.entity.user;

import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.export.DTOs.UserDTO;
import cz.upol.logicgo.model.games.entity.export.ExportedGames;
import cz.upol.logicgo.model.games.entity.setting.DefaultGameSetting;
import cz.upol.logicgo.model.games.entity.setting.GameSetting;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.setting.UserSetting;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "User.findByUsername",
                query = "SELECT u FROM User u WHERE u.username = :username"
        ),
        @NamedQuery(
                name = "User.findPasswordHashByUsername",
                query = "SELECT u.hashedPassword FROM User u WHERE u.username = :username"
        ),
        @NamedQuery(
                name = "User.findAnon",
                query = "SELECT u FROM User u WHERE u.hashedPassword = ''"
        ),
        @NamedQuery(
                name = "User.findAll",
                query = "SELECT u FROM User u"
        ),

})
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String hashedPassword;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSetting> userSettings;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DefaultGameSetting> defaultGameSetting;

    @Column(name = "last_logged", nullable = false)
    private LocalDateTime lastLogged;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ExportedGames> exportedGames = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastLogged == null) {
            lastLogged = LocalDateTime.now();
        }
    }

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public User(UserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.hashedPassword = userDTO.getHashedPassword();
        this.createdAt = userDTO.getCreatedAt();
        this.lastLogged = userDTO.getLastLogged();
    }

    /**
     * pro vytvoření anonymního účtu
     *
     * @param username jméno anonymního účtu
     */
    public User(String username) {
        this.username = username;
        hashedPassword = null;
    }

    public boolean isAuthenticated() {
        return hashedPassword != null;
    }


    public User() {

    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Map<SettingKey, String> getSettingsAsMap() {
        Map<SettingKey, String> settingsMap = new HashMap<>();
        if (userSettings != null) {
            for (Setting setting : userSettings) {
                settingsMap.put(setting.getKey(), setting.getValue());
            }
        }
        if (defaultGameSetting != null) {
            for (DefaultGameSetting defaultGameSetting : defaultGameSetting) {
                settingsMap.put(defaultGameSetting.getKey(), defaultGameSetting.getValue());
            }
        }
        return settingsMap;
    }

    public void setUserSettings(List<UserSetting> settings) {
        this.userSettings = settings;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setLastLogged(LocalDateTime lastLogged) {
        this.lastLogged = lastLogged;
    }

    public LocalDateTime getLastLogged() {
        return lastLogged;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public List<UserSetting> getUserSettings() {
        return userSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getHashedPassword(), user.getHashedPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getHashedPassword());
    }

    public List<DefaultGameSetting> getDefaultGameSetting() {
        return defaultGameSetting;
    }

    public void setDefaultGameSetting(List<DefaultGameSetting> defaultGameSetting) {
        this.defaultGameSetting = defaultGameSetting;
    }

    public List<ExportedGames> getExportedGames() {
        return exportedGames;
    }

    public void setExportedGames(List<ExportedGames> exportedGames) {
        this.exportedGames = exportedGames;
    }
}
