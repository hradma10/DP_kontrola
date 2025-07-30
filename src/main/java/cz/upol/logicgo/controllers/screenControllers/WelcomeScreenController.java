package cz.upol.logicgo.controllers.screenControllers;

import cz.upol.logicgo.algorithms.sudoku.SudokuUtils;
import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.algorithms.sudoku.layout.SudokuLayoutsLoader;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.Status;
import cz.upol.logicgo.misc.enums.TabType;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.helperInit.GameInit;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.dao.GameDAO;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ResourceBundle;

import static cz.upol.logicgo.algorithms.sudoku.SudokuGenerator.createSudoku;
import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.changeCellSize;
import static cz.upol.logicgo.misc.Messages.*;
import static cz.upol.logicgo.misc.SvgLoader.loadSvgToPane;
import static cz.upol.logicgo.util.generate.SeedFactory.generateDailySeed;

public class WelcomeScreenController implements Initializable {

    @FXML
    public Label gamePlayedLabel;
    @FXML
    public Label wonGamesLabel;
    @FXML
    public Label gamesInProgress;
    @FXML
    public Label welcomeLabel;
    public VBox closeAppButton;
    public VBox settingsButton;
    public VBox historyButton;
    public VBox gameButton;
    public VBox exportButton;
    public Canvas dailyGameCanvas;
    public Button dailyGameButton;
    public GridPane gridPane;
    public Label appTitle;
    public Label lastPlayedGameType;
    public Label lastPlayedGameTime;
    public Button resumeLastGameButton;
    public Label lastPlayedGameTitle;
    public Label playBoxLabel;
    public Label historyBoxLabel;
    public Label settingsBoxLabel;
    public Label exportBoxLabel;
    public Label closeBoxLabel;
    public Label dailyGameTitle;
    public StackPane playIconPane;
    public StackPane historyIconPane;
    public StackPane settingsIconPane;
    public StackPane exportIconPane;
    public StackPane exitIconPane;
    public Pane dailyGamePane;
    private Game dailyGame;

    private Game lastPlayedGame;

    private User user;
    final private GameDAO gameDAO;
    private Stage stage;
    MainScreenController mainScreenController;

    @TestOnly
    public WelcomeScreenController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public WelcomeScreenController() {
        this.gameDAO = new GameDAO();
    }

    public void initialize(User user, MainScreenController mainScreenController) {
        this.user = user;
        this.mainScreenController = mainScreenController;

        setIcons();
        animateTitle();
        setTextToFields(user);

        dailyGamePane.setMaxWidth(200);
        dailyGamePane.setMinWidth(200);
        dailyGamePane.setMinHeight(200);
        dailyGamePane.setMaxHeight(200);
        dailyGamePane.setPrefWidth(200);
        dailyGamePane.setPrefHeight(200);
    }

    private void setIcons(){
        loadSvgToPane(playIconPane, "/cz/upol/logicgo/images/svg/play.svg");
        loadSvgToPane(historyIconPane, "/cz/upol/logicgo/images/svg/history.svg");
        //loadSvgToPane(settingsIconPane, "/cz/upol/logicgo/images/svg/settings.svg");
        loadSvgToPane(exportIconPane, "/cz/upol/logicgo/images/svg/file-export.svg");
        loadSvgToPane(exitIconPane, "/cz/upol/logicgo/images/svg/exit.svg");
    }

    private void setTextToFields(User user) {
        LocalDateTime lastLogged = user.getLastLogged();
        String formattedWelcome;
        if (lastLogged == null || !user.isAuthenticated()) {
            formattedWelcome = getFormatted("welcome.noTime");
        } else {
            String duration = getDurationAsString(lastLogged);
            formattedWelcome = duration.isEmpty()
                    ? getFormatted("welcome.noTime")
                    : getFormatted("welcome.withTime", duration);
        }

        welcomeLabel.setText(formattedWelcome);

        createDailyGame();

        lastPlayedGame = gameDAO.getLastPlayedNonFinishedGame(user);

        if (lastPlayedGame != null) {
            resumeLastGameButton.setText(getFormatted("welcome.lastGame.buttonContinue"));
            lastPlayedGameType.setText(getFormatted("welcome.lastGame.type", lastPlayedGame.getTypeOfGame().getDescription()));
            lastPlayedGameTitle.setText(getFormatted("welcome.lastGame.title"));
            lastPlayedGameTime.setText(getFormatted("welcome.lastGame.playedTime", lastPlayedGame.getFormattedLastPlayed()));
        }else {
            resumeLastGameButton.setDisable(true);
        }

        playBoxLabel.setText(getFormatted("welcome.box.play"));
        historyBoxLabel.setText(getFormatted("welcome.box.history"));
        //settingsBoxLabel.setText(getFormatted("welcome.box.settings"));
        exportBoxLabel.setText(getFormatted("welcome.box.export"));
        closeBoxLabel.setText(getFormatted("welcome.box.close"));

        dailyGameTitle.setText(getFormatted("welcome.dailyGame.title"));

        Game game = gameDAO.getExistingGameWithSeed(dailyGame.getSeed(), user);
        if (game == null) {
            dailyGameButton.setText(getFormatted("welcome.dailyGame.button.new"));
            dailyGameButton.setDisable(false);
        } else if (game.getStatus() == Status.FINISHED) {
            dailyGameButton.setText(getFormatted("welcome.dailyGame.button.new"));
            dailyGameButton.setDisable(true);
        } else {
            dailyGameButton.setText(getFormatted("welcome.dailyGame.button.continue"));
            dailyGameButton.setDisable(false);
        }
        setupTooltips();
    }

    public void setupTooltips() {
        addTooltip(gameButton, "tooltip.welcome.play");
        addTooltip(closeAppButton, "tooltip.welcome.exit");
        //addTooltip(settingsButton, "tooltip.welcome.settings");
        addTooltip(historyButton, "tooltip.welcome.history");
        addTooltip(exportButton, "tooltip.welcome.export");
    }

    private void addTooltip(Node node, String key) {
        node.setOnMouseEntered(_ -> mainScreenController.setTextToLabel(getFormatted(key)));
        node.setOnMouseExited(_ -> mainScreenController.unsetTextToLabel());
    }

    public void playDailyGame() throws IOException {
        Game game = gameDAO.getExistingGameWithSeed(dailyGame.getSeed(), user);
        if (game == null) {
            openSudokuSettings(dailyGame.getSeed(), dailyGame.getTypeOfGame(), dailyGame.getGameType());
        }else {
            resumeDailyGame(game.getId());
        }

    }

    public void createDailyGame(){
        long seed = generateDailySeed();
        Difficulty difficulty = Difficulty.MEDIUM;
        RegionLayout regionLayout = SudokuLayoutsLoader.getLayoutsForSize(9).getFirst();
        Sudoku sudoku = createSudoku(SudokuType.NINE, regionLayout, difficulty, user, seed);

        int gridSize = sudoku.getType().getGridSize();;

        double width = dailyGameCanvas.getWidth() / gridSize;
        double height = dailyGameCanvas.getWidth() / gridSize;

        changeCellSize(sudoku.getBoard(), width, height);

        for (SudokuCell cell: SudokuUtils.flattenBoard(sudoku.getBoard())) {
            cell.setForPrint(true);
        }

        RedrawCanvasFunctions.redrawCanvas(dailyGameCanvas, sudoku);
        Arrays.stream(SudokuUtils.flattenBoard(sudoku.getBoard())).forEach(sudokuCell -> sudokuCell.setForPrint(true));
        dailyGame = sudoku;
    }

    public static String getDurationAsString(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(time, now);

        long totalSeconds = duration.getSeconds();

        if (totalSeconds <= 60) {
            return "";
        }

        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        return makeDurationString(days, hours, minutes).trim();
    }

    private static String makeDurationString(long days, long hours, long minutes) {
        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days);
            sb.append(hours == 1 ? " dnem" : " dny");
        }
        if (!sb.isEmpty()) sb.append(", ");
        if (hours > 0) {
            sb.append(hours);
            sb.append(hours == 1 ? " hodinou" : " hodinami");
        }

        if (!sb.isEmpty() && minutes > 0) sb.append(" a ");

        if (minutes > 0) {
            sb.append(minutes);
            sb.append(minutes == 1 ? " minutou" : " minutami");
        }
        return sb.toString();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void openMenu() throws IOException {
        if (!mainScreenController.checkOpen(TabType.GAME_LIST)) {
            mainScreenController.openTab(TabType.GAME_LIST);
        }
    }

    public void openHistoryList() throws IOException {
        if (!mainScreenController.checkOpen(TabType.PLAYED_LIST)) {
            mainScreenController.openTab(TabType.PLAYED_LIST);
        }
    }

    public void openExportButton() throws IOException {
        if (!mainScreenController.checkOpen(TabType.EXPORT_MULTIPLE)) {
            mainScreenController.openTab(TabType.EXPORT_MULTIPLE);
        }
    }

    public void openSudokuSettings(long seed, GameType gameType, TypeGame typeGame) throws IOException {
        GameInit gameInit = new GameInit(user).setGameType(gameType).setTypeGame(typeGame).setSeed(seed).setUse(true);
        mainScreenController.openTab(TabType.SUDOKU_SETTINGS, gameInit);
    }

    public void closeApp() {
        mainScreenController.close();
    }

    public void animateTitle(){
        appTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 64));
        appTitle.setEffect(new DropShadow(6, Color.rgb(60, 0, 90, 0.4)));

        DoubleProperty offset = new SimpleDoubleProperty(0);

        offset.addListener((obs, oldVal, newVal) -> {
            double o = newVal.doubleValue();
            Stop[] stops = new Stop[] {
                    new Stop(Math.max(0, o - 0.2), Color.web("#a64bf4")),
                    new Stop(o, Color.web("#8324ff")),
                    new Stop(Math.min(1, o + 0.2), Color.web("#c94bff"))
            };
            LinearGradient gradient = new LinearGradient(
                    0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            appTitle.setTextFill(gradient);
        });

        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(0), new KeyValue(offset, 0)),
                new KeyFrame(javafx.util.Duration.seconds(4), new KeyValue(offset, 1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    public void resumeLastGame() throws IOException {
        long id = lastPlayedGame.getId();
        var gameInit = new GameInit(user).setId(id);
        mainScreenController.openTab(TabType.SUDOKU, gameInit);
    }

    public void resumeDailyGame(long id) throws IOException {
        var gameInit = new GameInit(user).setId(id);
        mainScreenController.openTab(TabType.SUDOKU, gameInit);
    }
}
