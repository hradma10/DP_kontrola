package cz.upol.logicgo.misc.export.DTOs;

import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;

import java.time.LocalDateTime;
import java.util.List;

public class UserDTO {

    private long id;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogged;
    private String hashedPassword;
    private List<SettingDTO> settings;


    public UserDTO(long id, String username, LocalDateTime createdAt, LocalDateTime lastLogged , String hashedPassword, List<SettingDTO> settings) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
        this.lastLogged = lastLogged;
        this.hashedPassword = hashedPassword;
        this.settings = settings;
    }


    public String getUsername() {
        return username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLogged() {
        return lastLogged;
    }

    public long getId() {
        return id;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastLogged(LocalDateTime lastLogged) {
        this.lastLogged = lastLogged;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public List<SettingDTO> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingDTO> settings) {
        this.settings = settings;
    }
}
