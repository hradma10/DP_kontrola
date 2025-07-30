package cz.upol.logicgo.misc.helperInit;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.model.games.entity.user.User;

import java.util.Arrays;
import java.util.HashMap;

public class GameInit {

    public GameInit(User player){
        this.settings = new HashMap<>();
        this.id = null;
        this.difficulty = Difficulty.HARD;
        this.seed = null;
        this.player = player;
        this.typeGame = TypeGame.SUDOKU; // TODO fix init
        this.use = false;
    }

    public GameInit(long id, User player){
        this.id = id;
        this.player = player;
    }

    private Long id;

    private HashMap<SettingKey, String> settings;

    private Difficulty difficulty;

    private Long seed;

    private GameType gameType;

    private User player;

    private TypeGame typeGame;

    private RegionLayout regionLayout;

    private boolean use;

    public GameInit setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getId() {
        return id;
    }

    public HashMap<SettingKey, String> getSettings() {
        return settings;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public GameInit setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public Long getSeed() {
        return seed;
    }

    public GameInit setSeed(Long seed) {
        this.seed = seed;
        return this;
    }

    public GameType getGameType() {
        return gameType;
    }

    public GameInit setGameType(GameType gameType) {
        this.gameType = gameType;
        return this;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public GameInit setTypeGame(TypeGame typeGame) {
        this.typeGame = typeGame;
        return this;
    }

    public TypeGame getTypeGame() {
        return typeGame;
    }

    public boolean isUse() {
        return use;
    }

    public GameInit setUse(boolean use) {
        this.use = use;
        return this;
    }

    public RegionLayout getRegionLayout() {
        return regionLayout;
    }

    public GameInit setRegionLayout(RegionLayout regionLayout) {
        this.regionLayout = regionLayout;
        return this;
    }

    public record GameSetting(SettingKey settingKey, String value){}

    public void addSetting(GameSetting gameSetting){
        addSetting(gameSetting.settingKey(), gameSetting.value());
    }

    public void addSetting(SettingKey settingKey, String value){
        settings.put(settingKey, value);
    }

    public void addSettings(GameSetting ... gameSettings){
        Arrays.stream(gameSettings).forEach(this::addSetting);
    }
}
