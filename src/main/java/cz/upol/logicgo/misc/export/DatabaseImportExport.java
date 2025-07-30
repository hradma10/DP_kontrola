package cz.upol.logicgo.misc.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.upol.logicgo.misc.export.DTOs.ExportDTO;
import cz.upol.logicgo.misc.export.DTOs.GameDTO;
import cz.upol.logicgo.misc.export.DTOs.UserDTO;
import cz.upol.logicgo.misc.export.DTOs.games.setting.SettingDTO;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.dao.*;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.setting.UserSetting;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.Format;
import cz.upol.logicgo.misc.enums.settings.UserSettings;
import jakarta.persistence.EntityManager;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cz.upol.logicgo.misc.enums.settings.UserSettings.LOAD_FILE_PATH_DATA;
import static cz.upol.logicgo.misc.enums.settings.UserSettings.SAVE_FILE_PATH_DATA;

public class DatabaseImportExport {

    final private GameDAO gameDAO;
    final private UserDAO userDAO;
    final private SettingDAO settingDAO;
    final private SudokuDAO sudokuDAO;


    public DatabaseImportExport(){
        gameDAO = new GameDAO();
        userDAO = new UserDAO();
        settingDAO = new SettingDAO();
        sudokuDAO = new SudokuDAO();
    }

    @TestOnly
    public DatabaseImportExport(EntityManager em){
        gameDAO = new GameDAO(em);
        userDAO = new UserDAO(em);
        settingDAO = new SettingDAO(em);
        sudokuDAO = new SudokuDAO(em);
    }

    public void exportUser(Stage stage, User user) throws IOException {
        var fileName = getPathOfFile(stage, true, user);
        exportUserToFile(user, fileName);
    }

    public void exportDatabase(Stage stage, User user) throws IOException {
        var fileName = getPathOfFile(stage, true, user);
        exportUserToFile(null, fileName);
    }

    private String getPathOfFile(Stage stage, boolean saving, User user) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(saving ? "Export" : "Načtení");

        UserSettings loadedSettings = saving ? SAVE_FILE_PATH_DATA : LOAD_FILE_PATH_DATA;
        Setting setting = settingDAO.getUserSettings(user, loadedSettings).get(loadedSettings);
        File file = new File(setting.getValue());
        if (!(file.exists() && file.isDirectory())) {
            file = new File(".\\");
        }
        fileChooser.setInitialDirectory(file);


        fileChooser.setInitialFileName(".");
        var filters = new ArrayList<FileChooser.ExtensionFilter>();
        var yamlFilter = new FileChooser.ExtensionFilter("YAML Files (*.yaml)", "*.yaml");
        var jsonFilter = new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json");
        var xmlFilter = new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml");

        filters.add(yamlFilter);
        filters.add(jsonFilter);
        filters.add(xmlFilter);

        fileChooser.getExtensionFilters().addAll(filters);

        if (!saving) {
            var allFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
            fileChooser.getExtensionFilters().addAll(allFilter);
        }
        File selectedFile = saving ? fileChooser.showSaveDialog(stage) : fileChooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return "";
        } else {
            var absolutePath = selectedFile.getAbsolutePath();
            setting.setValue(absolutePath);
            settingDAO.update(setting);
            return absolutePath;
        }
    }


    public void exportUserToFile(User user, String fileName) throws IOException {
        int last = fileName.lastIndexOf('.');
        var extension = fileName.substring(last + 1);
        Format format = Format.getFormatFromString(extension);

        // users
        ArrayList<User> users = new ArrayList<>();

        if (user == null) {
            List<User> usersDB = userDAO.findAll();
            users.addAll(usersDB);
        } else {
            User userDB = userDAO.findByUsername(user.getUsername()).get();
            users.add(userDB);
        }

        // settings
        ArrayList<Setting> settings = new ArrayList<>();

        if (user == null) {
            List<UserSetting> settingsDB = settingDAO.findAllUserSettings();
            settings.addAll(settingsDB);
        } else {
            List<UserSetting> settingDBUser = settingDAO.getUserSettings(user);
            settings.addAll(settingDBUser);
        }

        // games
        ArrayList<Game> games = new ArrayList<>();
        // TODO poté přidat mazes a bridges
        if (user == null) {
            List<Sudoku> gamesDB = sudokuDAO.findAll();
            games.addAll(gamesDB);
        } else {
            List<Sudoku> gamesDBUser = sudokuDAO.getAllUserSudokuById(user.getId());
            games.addAll(gamesDBUser);
        }

        List<UserDTO> userDTOs = DTOMapper.serializeUser(users);
        List<GameDTO> gameDTOs = DTOMapper.serializeGame(games);
        List<SettingDTO> settingDTOs = DTOMapper.serializeSettings(settings);
        ExportDTO exportDTO = new ExportDTO(gameDTOs, userDTOs, settingDTOs);

        String output = switch (format) {
            case YAML -> {
                ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                yamlMapper.registerModule(new JavaTimeModule());
                yield yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportDTO);

            }
            case JSON -> {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                yield mapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportDTO);

            }
            case XML -> {
                ObjectMapper xmlMapper = new XmlMapper();
                xmlMapper.registerModule(new JavaTimeModule());
                yield xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportDTO);

            }
        };
        Files.writeString(Paths.get(fileName), output, StandardCharsets.UTF_8);
    }

    public void importUserFromFile(User user, Stage stage) throws IOException {
        String fileName = getPathOfFile(stage, false, user);
        importUserFromFile(user, fileName);
    }

    public void importUserFromFile(User user, String fileName) throws IOException {
        int last = fileName.lastIndexOf('.');
        var extension = fileName.substring(last + 1);
        Format format = Format.getFormatFromString(extension);
        String inputString = Files.readString(Paths.get(fileName), StandardCharsets.UTF_8);
        ExportDTO importDTO = switch (format) {
            case YAML -> {
                ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                yield yamlMapper.readValue(inputString, ExportDTO.class);
            }
            case JSON -> {
                ObjectMapper mapper = new ObjectMapper();
                yield mapper.readValue(inputString, ExportDTO.class);
            }
            case XML -> {
                ObjectMapper xmlMapper = new XmlMapper();
                yield xmlMapper.readValue(inputString, ExportDTO.class);
            }
        };

        // user a salt

        List<UserDTO> userDTOs = importDTO.getUserDTOs();


        ArrayList<User> usersWithSalts = new ArrayList<>();

        ArrayList<String> usernames = new ArrayList<>();

        HashMap<String, Long> usernamesToIds = new HashMap<>();

        HashMap<Long, User> users = new HashMap<>();
        for (UserDTO userDTO : userDTOs) {
            usernames.add(userDTO.getUsername());
            User newUser = new User(userDTO);
            users.put(userDTO.getId(), newUser);
            usernamesToIds.put(userDTO.getUsername(), userDTO.getId());
        }

        // settings

        List<SettingDTO> settingDTOs = importDTO.getSettingDTOs();

        for (SettingDTO settingDTO : settingDTOs) {
            User associatedUser = users.get(settingDTO.getOwnerId());
            UserSetting setting = new UserSetting(settingDTO, associatedUser);
            associatedUser.getUserSettings().add(setting);
        }

        try {
            userDAO.saveAll(usersWithSalts);
        }catch (Exception e){
            return;
        }

        List<User> usersDB = userDAO.findAllByUsernames(usernames);

        for (User userDB : usersDB) {
            users.put(usernamesToIds.get(userDB.getUsername()), userDB);
        }

        List<GameDTO> gameDTOs = importDTO.getGameDTOs();

        ArrayList<Game> games = DTOMapper.deserializeGames(gameDTOs, users);

        try {
            gameDAO.saveAll(games);
        }catch (Exception e){
            userDAO.deleteAll(usersDB);
        }

    }
}





