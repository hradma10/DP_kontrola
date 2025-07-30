package cz.upol.logicgo.model.games.service;

import cz.upol.logicgo.exceptions.LogicGoException;
import cz.upol.logicgo.exceptions.database.UserErrorException;
import cz.upol.logicgo.exceptions.database.UserExistsException;
import cz.upol.logicgo.model.games.dao.UserDAO;
import cz.upol.logicgo.model.games.entity.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UserService {

    UserDAO userDAO = new UserDAO();

    SettingsService settingsService = new SettingsService();

    public Optional<User> login(String username, String password) throws UserErrorException {

        String hashedPassword = userDAO.findHashByUsername(username);
        if (hashedPassword.isEmpty()) {
            return Optional.empty();
        }

        boolean valid = checkPassword(password, hashedPassword);

        if (valid) {
            Optional<User> optionalUser = userDAO.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                settingsService.checkUserSettings(user);
                settingsService.checkDefaultGameSettingsForUser(user);
                return Optional.of(user);
            }
        }

        return Optional.empty();

    }

    public Optional<User> register(String username, String password) throws LogicGoException {
        if (userDAO.findByUsername(username).isPresent()) throw new UserExistsException();

        String hashedPassword = hashPassword(password);

        Optional<User> userOptional = userDAO.save(new User(username, hashedPassword));

        if (userOptional.isEmpty()) throw new UserErrorException();

        User user = userOptional.get();
        settingsService.checkUserSettings(user);
        settingsService.checkDefaultGameSettingsForUser(user);
        return userOptional;
    }

    public User getAnon() {
        return userDAO.findAnon().get();
    }

    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }
}
