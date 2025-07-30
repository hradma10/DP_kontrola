package cz.upol.logicgo.misc.export.DTOs;

import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;

import java.util.List;

public class ExportDTO {
    private List<GameDTO> gameDTOs;
    private List<UserDTO> userDTOs;
    private List<SettingDTO> settingDTOs;

    public ExportDTO(List<GameDTO> gameDTOs, List<UserDTO> userDTOs, List<SettingDTO> settingDTOs) {
        this.gameDTOs = gameDTOs;
        this.userDTOs = userDTOs;
        this.settingDTOs = settingDTOs;
    }

    public List<GameDTO> getGameDTOs() {
        return gameDTOs;
    }

    public void setGameDTOs(List<GameDTO> gameDTOs) {
        this.gameDTOs = gameDTOs;
    }

    public List<UserDTO> getUserDTOs() {
        return userDTOs;
    }

    public void setUserDTOs(List<UserDTO> userDTOs) {
        this.userDTOs = userDTOs;
    }


    public List<SettingDTO> getSettingDTOs() {
        return settingDTOs;
    }

    public void setSettingDTOs(List<SettingDTO> settingDTOs) {
        this.settingDTOs = settingDTOs;
    }
}
