package cz.upol.logicgo.model.games.dao;

import cz.upol.logicgo.misc.enums.settings.BridgeSettings;
import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.enums.settings.SudokuSettings;
import cz.upol.logicgo.misc.enums.settings.UserSettings;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.entity.setting.DefaultGameSetting;
import cz.upol.logicgo.model.games.entity.setting.GameSetting;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.setting.UserSetting;
import cz.upol.logicgo.model.games.entity.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.upol.logicgo.misc.Helper.mapBy;

public class SettingDAO extends AbstractDAO {

    public SettingDAO() {
        super();
    }

    public SettingDAO(EntityManager em) {
        super(em);
    }

    private void initAnonSettings(){

    }

    public void save(Setting setting) {
        em.getTransaction().begin();
        em.persist(setting);
        em.getTransaction().commit();
    }

    public <K extends Setting> void saveAll(List<K> settings) {
        if (settings.isEmpty()) return;
        em.getTransaction().begin();
        settings.forEach(em::persist);
        em.getTransaction().commit();
    }

    public void saveAll(Setting... settings) {
        em.getTransaction().begin();
        Arrays.stream(settings).forEach(em::persist);
        em.getTransaction().commit();
    }

    public void update(Setting setting) {
        em.getTransaction().begin();
        em.merge(setting);
        em.getTransaction().commit();
    }

    public List<UserSetting> getUserSettings(User user) {
        Query query = em.createNamedQuery("UserSetting.findByUser");
        query.setParameter("user", user);
        return (List<UserSetting>) query.getResultList();
    }

    public List<GameSetting> getGameSettings(Game game) {
        Query query = em.createNamedQuery("GameSetting.findByGame");
        query.setParameter("game", game);
        return (List<GameSetting>) query.getResultList();
    }

    public HashMap<SettingKey, UserSetting> getUserSettings(User user, UserSettings... settingsArray) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserSetting> cq = cb.createQuery(UserSetting.class);
        Root<UserSetting> root = cq.from(UserSetting.class);

        Predicate userPredicate = cb.equal(root.get("user"), user);

        CriteriaBuilder.In<UserSettings> inClause = cb.in(root.get("key"));
        for (UserSettings setting : settingsArray) {
            inClause.value(setting);
        }

        cq.select(root).where(cb.and(userPredicate, inClause));

        var resultList = em.createQuery(cq).getResultList();

        return mapBy(resultList, UserSetting::getKey);
    }

    public List<DefaultGameSetting> getDefaultSettingsOfUser(User user) {
        Query query = em.createNamedQuery("DefaultGameSetting.getAllDefaultSettingsOfUser");
        query.setParameter("user", user);
        return (List<DefaultGameSetting>) query.getResultList();
    }


    public void setDefaultGameSettings(User user) {
        List<DefaultGameSetting> existingSettings =  getDefaultSettingsOfUser(user);

        Set<SettingKey> settingNames = existingSettings.stream()
                .map(DefaultGameSetting::getKey)
                .collect(Collectors.toSet());

        List<SettingKey> DEFAULT_KEYS = Stream.concat(
                        Stream.of(SudokuSettings.values()),
                        Stream.of(BridgeSettings.values())
                )
                .collect(Collectors.toList());

        List<DefaultGameSetting> createdSettings = new ArrayList<>();


        for (SettingKey key : DEFAULT_KEYS) {
            if (settingNames.contains(key)) continue;
            DefaultGameSetting setting = new DefaultGameSetting(key, key.getDefaultValue().toString(), user);
            createdSettings.add(setting);
        }

        saveAll(createdSettings);
    }

    public List<UserSettings> getUserSettingNames(User user) {
        Query query = em.createNamedQuery("UserSetting.findKeysByUser");
        query.setParameter("user", user);
        return (List<UserSettings>) query.getResultList();
    }

    public List<UserSetting> findAllUserSettings() {
        Query query = em.createNamedQuery("UserSetting.findAll");
        return (List<UserSetting>) query.getResultList();
    }

    public List<GameSetting> findAllGameSettings() {
        Query query = em.createNamedQuery("GameSetting.findAll");
        return (List<GameSetting>) query.getResultList();
    }

    public User refreshUser(User user) {
        em.refresh(user);
        return user;
    }
}
