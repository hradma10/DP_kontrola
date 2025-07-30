package cz.upol.logicgo.controllers.screenControllers;

import cz.upol.logicgo.StartOfApp;
import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.algorithms.sudoku.SudokuUtils;
import cz.upol.logicgo.algorithms.sudoku.TargetedCell;
import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.commands.Command;
import cz.upol.logicgo.commands.CommandExecutor;
import cz.upol.logicgo.commands.sudokuCommand.RestartSudokuCommand;
import cz.upol.logicgo.commands.sudokuCommand.SetSudokuNumberCommand;
import cz.upol.logicgo.controllers.miscControllers.ExportWindowController;
import cz.upol.logicgo.controllers.miscControllers.HowToPlayController;
import cz.upol.logicgo.controllers.miscControllers.NotesWindowController;
import cz.upol.logicgo.controllers.miscControllers.SolutionWindowController;
import cz.upol.logicgo.controllers.popoverControllers.HintPopOverController;
import cz.upol.logicgo.exceptions.game.GameLoadFail;
import cz.upol.logicgo.handlers.sudoku.MouseKeySudokuHandlers;
import cz.upol.logicgo.misc.NodeSnapshotting;
import cz.upol.logicgo.misc.TabState;
import cz.upol.logicgo.misc.dataStructures.QuadTree;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.HintType;
import cz.upol.logicgo.misc.enums.Status;
import cz.upol.logicgo.misc.enums.settings.SettingKey;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.helperInit.GameInit;
import cz.upol.logicgo.misc.windows.AlertBox;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.dao.SettingDAO;
import cz.upol.logicgo.model.games.dao.SudokuDAO;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.Cell;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.setting.GameSetting;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.util.generate.SeedFactory;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;

import static cz.upol.logicgo.algorithms.sudoku.SudokuGenerator.createSudoku;
import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.*;
import static cz.upol.logicgo.commands.CommandExecutor.SavedStacks;
import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.clearCanvas;
import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.redrawCanvas;
import static cz.upol.logicgo.misc.Helper.getSetting;
import static cz.upol.logicgo.misc.Messages.getFormatted;
import static cz.upol.logicgo.misc.SvgLoader.setIcon;
import static cz.upol.logicgo.misc.enums.settings.SudokuSettings.*;

public class SudokuGameController implements Initializable {
    private final ArrayList<SudokuCell> hoveredSudokuCells = new ArrayList<>();
    private final SudokuDAO sudokuDAO;
    private final SettingDAO settingDAO;
    public Button buttonHelp;
    public Button buttonNotes;
    public Button buttonSolution;
    public Button buttonSettings;
    public Button buttonExit;
    public Button buttonExport;
    public Button buttonPause;
    public Canvas primaryCanvas;
    public Canvas secondaryCanvas;
    public Label countdownTimer;
    public Pane gamePane;
    public Button buttonRestart;
    public Button buttonHint;
    public Button buttonRedo;
    public Button buttonUndo;


    Stage stage;
    MainScreenController mainScreenController;
    private SudokuCell selectedSudokuCell;
    private SudokuGame sudokuGame;
    private CommandExecutor commandExecutor;
    private QuadTree quadTree;
    private long startTime;
    private Duration elapsedTime;
    private AnimationTimer elapsedTimeTimer;
    private AnimationTimer hoverTimer;
    private boolean hintChoice = false;
    private boolean timerIsRunning = false;
    // null pokud si uživatel může vybrat, jinak má instanci
    private HintType hintTypeSetBySettings;
    private HintType activeHint;
    private MouseKeySudokuHandlers mouseKeyHandlers;

    private Cell selectedCellOutline;

    private TabState tabState;



    public SudokuGameController() {
        this.settingDAO = new SettingDAO();
        this.commandExecutor = new CommandExecutor();
        this.sudokuDAO = new SudokuDAO();
    }

    @TestOnly
    public SudokuGameController(SettingDAO settingDAO, CommandExecutor commandExecutor, SudokuDAO sudokuDAO) {
        this.settingDAO = settingDAO;
        this.commandExecutor = commandExecutor;
        this.sudokuDAO = sudokuDAO;
    }

    private void setIcons() {
        setIcon(buttonPause, "/cz/upol/logicgo/images/svg/pause.svg");
        setIcon(buttonUndo, "/cz/upol/logicgo/images/svg/arrow-left.svg");
        setIcon(buttonRedo, "/cz/upol/logicgo/images/svg/arrow-right.svg");
        setIcon(buttonExit, "/cz/upol/logicgo/images/svg/exit.svg");
        setIcon(buttonExport, "/cz/upol/logicgo/images/svg/file-export.svg");
        setIcon(buttonHint, "/cz/upol/logicgo/images/svg/progress-help.svg");
        setIcon(buttonSolution, "/cz/upol/logicgo/images/svg/solution.svg");
        //setIcon(buttonHelp, "/cz/upol/logicgo/images/svg/help.svg");
        //setIcon(buttonRestart, "/cz/upol/logicgo/images/svg/reload.svg");
        //setIcon(buttonNotes, "/cz/upol/logicgo/images/svg/notes.svg");
        //setIcon(buttonSettings, "/cz/upol/logicgo/images/svg/settings.svg");
    }

    public void setupTooltips(boolean hintsWindow) {
        addTooltip(buttonPause, "tooltip.sudoku.play");
        addTooltip(buttonUndo, "tooltip.sudoku.undo");
        addTooltip(buttonRedo, "tooltip.sudoku.redo");
        addTooltip(buttonExit, "tooltip.sudoku.exit");
        addTooltip(buttonExport, "tooltip.sudoku.export");
        addTooltip(buttonSolution, "tooltip.sudoku.solution");
        //addTooltip(buttonHelp, "tooltip.sudoku.help");
        addTooltip(buttonHint, hintsWindow ? "tooltip.sudoku.hintWindow" : "tooltip.sudoku.noHintWindow");
        //addTooltip(buttonRestart, "tooltip.sudoku.restart");
        //addTooltip(buttonNotes, "tooltip.sudoku.notes");
        //addTooltip(buttonSettings, "tooltip.sudoku.settings");
    }

    private void addTooltip(Node node, String key) {
        node.setOnMouseEntered(_ -> mainScreenController.setTextToLabel(getFormatted(key)));
        node.setOnMouseExited(_ -> mainScreenController.unsetTextToLabel());
    }

    public void initialize(GameInit gameInit, MainScreenController mainScreenController, TabState tabState) throws GameLoadFail {
        this.mainScreenController = mainScreenController;
        this.tabState = tabState;
        setIcons();

        buttonRestart.setVisible(false);
        buttonPause.setDisable(true);

        quadTree = new QuadTree(primaryCanvas.getWidth(), primaryCanvas.getHeight());
        commandExecutor = new CommandExecutor();

        boolean load = gameInit.getId() != null;
        if (load) {
            loadGame(gameInit);
        } else {
            createGame(gameInit);
        }

        setupTooltips(true);

        tabState.setChangePending();

        tabState.doneProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal){
                saveGame(true);
                tabState.markSavingFinished();
            }
        });

        gamePane.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        gamePane.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());

        redrawCanvas(primaryCanvas, sudokuGame.getSudoku());
        if (load) {
            tabState.setIdOfGame(sudokuGame.getSudoku().getId());
        } else {
            saveGame(true);
        }

        Tab tab = tabState.getAssociatedTab();

        tab.setClosable(true);

        tab.setOnCloseRequest((event) -> {
            tab.getTabPane().setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
            event.consume();

            onExit();
        });
    }

    private void resizeCanvas() {
        Sudoku sudoku = sudokuGame.getSudoku();
        double width = gamePane.getWidth();
        double height = gamePane.getHeight();
        primaryCanvas.setWidth(width);
        primaryCanvas.setHeight(height);
        secondaryCanvas.setWidth(width);
        secondaryCanvas.setHeight(height);


        double widthCell = width / sudokuGame.getSudoku().getType().getGridSize();
        double heightCell = height / sudokuGame.getSudoku().getType().getGridSize();
        changeCellSize(sudoku.getBoard(), widthCell, heightCell);
        changeCellSize(sudoku.getStartingBoard(), widthCell, heightCell);
        changeCellSize(sudoku.getSolutionBoard(), widthCell, heightCell);

        Cell outlineCell = this.getSelectedCellOutline();
        if (outlineCell != null) {
            outlineCell.changeSizeAndPosition(widthCell, heightCell);

        }

        SudokuCell[] flatBoard = flattenBoard(sudoku.getBoard());
        quadTree.clear();
        Arrays.stream(flatBoard)
                .forEach(quadTree::insert);
        redrawCanvas(primaryCanvas, sudokuGame.getSudoku());
        redrawCanvas(secondaryCanvas, selectedSudokuCell);

        if (outlineCell != null) {
            redrawCanvas(secondaryCanvas, outlineCell);
        }

    }

    private void loadGame(GameInit gameInit) throws GameLoadFail {
        Optional<Sudoku> sudokuGameOptional = sudokuDAO.findById(gameInit.getId());
        if (sudokuGameOptional.isPresent()) {
            Sudoku sudoku = sudokuGameOptional.get();
            sudokuGame = new SudokuGame(sudoku, false);

            commandExecutor.loadSudokuCommands(sudokuGame);
            setGameParameters(sudoku);
        } else {
            throw new GameLoadFail("Nelze načíst sudoku.");
        }
    }

    private void setGameParameters(Sudoku sudoku) {
        double width = primaryCanvas.getWidth() / sudokuGame.getSudoku().getType().getGridSize();
        double height = primaryCanvas.getWidth() / sudokuGame.getSudoku().getType().getGridSize();

        changeCellSize(sudoku.getBoard(), width, height);
        changeCellSize(sudoku.getStartingBoard(), width, height);
        changeCellSize(sudoku.getSolutionBoard(), width, height);

        Map<SettingKey, String> settings = getSettingsAsMap(sudoku.getSettings());

        boolean cellNotes = getSetting(CELL_NOTES, settings);

        boolean hintsOn = getSetting(HINTS_ON, settings);

        setCellsProperties(sudoku, false, cellNotes);

        boolean shouldTimerExist = getSetting(TIMER, settings);

        if (!shouldTimerExist) {
            buttonPause.setVisible(false);
            buttonPause.setManaged(false);
            countdownTimer.setVisible(false);
            countdownTimer.setManaged(false);
        }else {
            buttonPause.setVisible(true);
            buttonPause.setManaged(true);
            buttonPause.setDisable(false);
        }

        if (!hintsOn) {
            buttonHint.setVisible(false);
            buttonHint.setManaged(false);
        }

        buttonRestart.setManaged(false);

        boolean preChosenHintType = getSetting(HINT_TYPE, settings);

        if (preChosenHintType) {
            hintTypeSetBySettings = getSetting(HINT_TYPE_CHOICE, settings);
        }


        SudokuCell[] flatBoard = flattenBoard(sudoku.getBoard());
        Arrays.stream(flatBoard)
                .forEach(quadTree::insert);

        setCandidates();
        setCandidates(sudoku.getStartingBoard());
        mouseKeyHandlers = new MouseKeySudokuHandlers(sudokuGame, this);
        start(shouldTimerExist);
    }

    public static List<GameSetting> createSettings(HashMap<SettingKey, String> settingsMap, Game game) {
        ArrayList<GameSetting> list = new ArrayList<>();
        for (Map.Entry<SettingKey, String> entry : settingsMap.entrySet()) {
            SettingKey key = entry.getKey();
            String value = entry.getValue();
            GameSetting gameSetting = new GameSetting(key, value, game);
            list.add(gameSetting);
        }
        return list;
    }


    private void createGame(GameInit gameInit) {
        SudokuType sudokuType = (SudokuType) gameInit.getGameType();
        Difficulty difficulty = gameInit.getDifficulty();
        RegionLayout regionLayout = gameInit.getRegionLayout();
        User player = gameInit.getPlayer();

        long seed = (gameInit.getSeed() != null) ? gameInit.getSeed() : SeedFactory.generateSeed();

        Sudoku sudoku = generateSudokuTimed(seed, player, sudokuType, regionLayout, difficulty, 4);

        sudoku.setSettings(createSettings(gameInit.getSettings(), sudoku));

        this.sudokuGame = new SudokuGame(sudoku, false);

        setGameParameters(sudoku);
    }

    public void hintAction() {
        Map<SettingKey, String> settings = SudokuUtils.getSettingsAsMap(sudokuGame.getSudoku().getSettings());

        boolean hintSet = getSetting(HINT_TYPE, settings);

        hintChoice = true;
        if (hintSet) {
            this.setActiveHint(hintTypeSetBySettings);
        } else {
            showPopOver();
        }
    }

    public void showPopOver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/upol/logicgo/windows/popovers/HintPopOver.fxml"));
            Parent content = loader.load();
            HintPopOverController controller = loader.getController();

            PopOver popOver = new PopOver(content);
            controller.initialize(mainScreenController, getSetting(CELL_NOTES, SudokuUtils.getSettingsAsMap(sudokuGame.getSudoku().getSettings())));
            content.getStyleClass().add("root");
            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            popOver.setAutoHide(true);
            popOver.setDetachable(false);

            controller.setOnSelect(type -> {
                this.setActiveHint(type);
                switch (type) {
                    case RANDOM_CELL -> {
                        ArrayList<TargetedCell> emptyCells = this.getSudokuGame().getSudokuValidator().getEmptyCells();
                        if (emptyCells.isEmpty()) break;
                        var emptyCell = emptyCells.get(new Random().nextInt(emptyCells.size()));


                        SudokuGame sudokuGame = this.getSudokuGame();
                        int row = emptyCell.getRow();
                        int col = emptyCell.getCol();

                        int sol = getSudokuGame().getSudoku().getSolutionBoard()[row][col].getValue();

                        this.getCommandExecutor().execute(new SetSudokuNumberCommand(sudokuGame, row, col, sol, 0));
                        this.tabState.setChangePending();

                        this.setHintChoice(false);
                        this.setActiveHint(null);
                        redrawCanvas(primaryCanvas, sudokuGame.getSudoku());
                    }
                    case CHECK_VALIDITY -> {
                        Sudoku sudoku = this.getSudokuGame().getSudoku();
                        SudokuCell[][] board = sudoku.getBoard();
                        SudokuCell[][] solutionBoard = sudoku.getSolutionBoard();

                        for (int row = 0; row < board.length; row++) {
                            for (int col = 0; col < board[row].length; col++) {
                                SudokuCell cell = board[row][col];
                                if (cell.isChangeable() && cell.getValue() != solutionBoard[row][col].getValue()) {
                                    cell.setBackgroundColor(cell.getValue() == 0 ? Color.CYAN : Color.RED);
                                }
                            }
                        }
                        this.setHintChoice(false);
                        this.setActiveHint(null);
                        redrawCanvas(primaryCanvas, sudokuGame.getSudoku());
                    }
                    default -> {
                        return;
                    }
                }
                popOver.hide();
            });

            popOver.show(buttonHint);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setCandidates() {
        SudokuCell[][] board = sudokuGame.getSudoku().getBoard();
        setCandidates(board);
    }

    public void setCandidates(SudokuCell[][] board) {
        for (int row = 0; row < sudokuGame.getSudoku().getHeight(); row++) {
            for (int col = 0; col < sudokuGame.getSudoku().getWidth(); col++) {
                var candidates = SudokuUtils.getCellCandidatesPlay(sudokuGame.getSudoku(), row, col);
                board[row][col].setCandidates(candidates);
            }
        }
    }

    public void start(boolean startTimer) {
        if (startTimer) {
            startTimer();
            elapsedTimeTimer.start();
        }
        hoverAnimation();
        hoverTimer.start();
    }

    private void startTimer() {
        startTime = System.currentTimeMillis() - (elapsedTime != null ? elapsedTime.toMillis() : 0);
        elapsedTimeTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                elapsedTime = Duration.ofMillis(elapsedMillis);
                sudokuGame.getSudoku().setElapsedTime(elapsedTime);

                long totalSeconds = elapsedTime.toSeconds();

                String text = getFormattedTime(totalSeconds);
                countdownTimer.setText(text);

            }
        };
        timerIsRunning = true;
    }

    public void pause() {
        if (elapsedTimeTimer != null) {
            elapsedTimeTimer.stop();
            elapsedTime = Duration.ofMillis(System.currentTimeMillis() - startTime);
            timerIsRunning = false;
        }
    }


    public static String getFormattedTime(long totalSeconds) {
        String text;

        if (totalSeconds > 86400) {
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;

            text = String.format("%02d:%02d", minutes, seconds);
        } else {
            long hours = totalSeconds / 3600;
            long minutes = totalSeconds % 3600 / 60;
            long seconds = totalSeconds % 3600 % 60;

            text = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return text;
    }

    private void hoverAnimation() {
        hoverTimer = new AnimationTimer() {
            long lastTime = 0;
            double timeSeconds = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                timeSeconds += delta;

                if (!hoveredSudokuCells.isEmpty() && hintChoice) {
                    clearCanvas(secondaryCanvas);
                    SudokuUtils.drawPulsatingCell(secondaryCanvas, timeSeconds, hoveredSudokuCells.toArray(new SudokuCell[0]));
                }
            }
        };

    }

    public void stop() {
        if (elapsedTimeTimer != null) {
            elapsedTimeTimer.stop();
            timerIsRunning = false;
            sudokuGame.getSudoku().setElapsedTime(elapsedTime);
        }
    }

    public void onPauseButtonPressed() {
        if (timerIsRunning) {
            pause();
            setIcon(buttonPause, "/cz/upol/logicgo/images/svg/play.svg");
        }
        else {
            startTimer();
            elapsedTimeTimer.start();
            setIcon(buttonPause, "/cz/upol/logicgo/images/svg/pause.svg");
        }
    }

    public void close() {
        if (elapsedTimeTimer != null || timerIsRunning) {
            AlertBox.saveAlertNotes();
        }
        stop();
        saveGame(true);
    }

    public void gameFinished() {
        sudokuGame.getSudoku().setStatus(Status.FINISHED);
        stop();
        buttonPause.setDisable(true);
        buttonHint.setDisable(true);
        tabState.markSavingFinished();
        AlertBox.YouWonWindow(sudokuGame.getSudoku().getElapsedTime());
    }


    public void saveGame(boolean newThumbnail) {
        Sudoku sudoku = sudokuGame.getSudoku();
        try {
            SavedStacks stacks = commandExecutor.serializeSudokuCommandsToByteArrays();
            sudoku.setUndoStack(stacks.undoStack());
            sudoku.setRedoStack(stacks.redoStack());
            if (newThumbnail) {
                new NodeSnapshotting().makeThumbnail(sudoku, primaryCanvas);
            }
        } catch (Exception _) {

        } finally {
            sudokuDAO.save(sudoku);
            tabState.setIdOfGame(sudoku.getId());
            tabState.unsetChangePending();
        }

    }

    @FXML
    public void openSolution() throws IOException {
        Game game = sudokuGame.getSudoku();
        FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource("/cz/upol/logicgo/windows/misc/solution_window.fxml"));
        Parent root = loader.load();
        SolutionWindowController controller = loader.getController();
        Scene scene = new Scene(root, 500, 500, false);
        Stage stage = new Stage();
        controller.setStage(stage);
        controller.initialize(game);
        stage.setTitle("Řešení");
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.showAndWait();
    }

    @FXML
    public void openNotes() throws IOException {
        Game game = sudokuGame.getSudoku();
        FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource("/cz/upol/logicgo/windows/misc/notes_window.fxml"));
        Parent root = loader.load();
        NotesWindowController controller = loader.getController();
        Scene scene = new Scene(root, 500, 500, false);
        Stage stage = new Stage();
        controller.setStage(stage);
        controller.initialize(game);
        stage.setTitle("Poznámky");
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.showAndWait();
        String notes = sudokuDAO.getGameNotes(game.getId());
        game.setNotes(notes);
    }

    @FXML
    public void openExport() throws IOException {
        Game game = sudokuGame.getSudoku();
        FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource("/cz/upol/logicgo/windows/misc/export_window.fxml"));
        Parent root = loader.load();
        ExportWindowController controller = loader.getController();
        Scene scene = new Scene(root, 700, 700, false);
        Stage stage = new Stage();
        stage.setMinWidth(700);
        stage.setMinHeight(700);
        controller.setStage(stage);
        controller.initialize(game, game.getPlayer());
        stage.setTitle("Export");
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onMousePressed(MouseEvent mouseEvent) {

        //this.getMouseKeyHandlers().on
    }

    @FXML
    public void onMouseClicked(MouseEvent mouseEvent) {
        this.getMouseKeyHandlers().onMouseClicked(mouseEvent);
    }


    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        this.getMouseKeyHandlers().onKeyPressed(keyEvent);
    }


    @FXML
    private void onMouseReleased(MouseEvent mouseEvent) {
        this.getMouseKeyHandlers().onMouseReleased(mouseEvent);
    }


    @FXML
    public void onMouseMoved(MouseEvent mouseEvent) {
        this.getMouseKeyHandlers().onMouseMoved(mouseEvent);
    }


    @FXML
    public void onKeyReleased(KeyEvent keyEvent) {
        this.getMouseKeyHandlers().onKeyReleased(keyEvent);
    }


    public SudokuCell getSelectedSudokuCell() {
        return selectedSudokuCell;
    }

    public void setSelectedSudokuCell(SudokuCell selectedSudokuCell) {
        this.selectedSudokuCell = selectedSudokuCell;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public Canvas getPrimaryCanvas() {
        return primaryCanvas;
    }

    public Canvas getSecondaryCanvas() {
        return secondaryCanvas;
    }

    public QuadTree getQuadTree() {
        return quadTree;
    }

    public SettingDAO getSettingDAO() {
        return settingDAO;
    }

    public SudokuDAO getSudokuDAO() {
        return sudokuDAO;
    }

    public SudokuGame getSudokuGame() {
        return sudokuGame;
    }

    public Label getCountdownTimer() {
        return countdownTimer;
    }

    public Button getButtonExit() {
        return buttonExit;
    }

    public Button getButtonSettings() {
        return buttonSettings;
    }

    public Button getButtonSolution() {
        return buttonSolution;
    }

    public Button getButtonNotes() {
        return buttonNotes;
    }

    public Button getButtonHelp() {
        return buttonHelp;
    }

    public Button getButtonExport() {
        return buttonExport;
    }

    public MouseKeySudokuHandlers getMouseKeyHandlers() {
        return mouseKeyHandlers;
    }

    public TabState getTabState() {
        return tabState;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public AnimationTimer getHoverTimer() {
        return hoverTimer;
    }

    public void setHoverTimer(AnimationTimer hoverTimer) {
        this.hoverTimer = hoverTimer;
    }

    public boolean isHintChoice() {
        return hintChoice;
    }

    public void setHintChoice(boolean hintChoice) {
        this.hintChoice = hintChoice;
    }

    public HintType getActiveHint() {
        return activeHint;
    }

    public void setActiveHint(HintType activeHint) {
        this.activeHint = activeHint;
    }

    public void setHoveredSudokuCells(SudokuCell... cells) {
        if (cells == null) cells = new SudokuCell[0];
        hoveredSudokuCells.clear();
        hoveredSudokuCells.addAll(List.of(cells));
    }

    public ArrayList<SudokuCell> getHoveredSudokuCells() {
        return hoveredSudokuCells;
    }

    public void onSettings() {
        // TODO make settings
    }

    public void onExit() {
        if (tabState.isDone() && !tabState.isChangePending()){
            mainScreenController.closeTab(tabState.getAssociatedTab());
        } else {
            boolean close = AlertBox.initCloseGame();
            if (close) {
                mainScreenController.closeTab(tabState.getAssociatedTab());
            }
        }
    }

    public void onRestart() {
        SudokuCell[][] oldCells = SudokuCell.deepCopyBoard(sudokuGame.getSudoku().getBoard());
        SudokuCell[][] newCells = SudokuCell.deepCopyBoard(sudokuGame.getSudoku().getStartingBoard());

        Command command = new RestartSudokuCommand(sudokuGame,  oldCells, newCells);
        commandExecutor.execute(command);
    }

    public void onRedo() {
        commandExecutor.redo();
        redrawCanvas(primaryCanvas, this.getSudokuGame().getSudoku());
        redrawCanvas(secondaryCanvas, selectedSudokuCell);
    }

    public void onUndo() {
        commandExecutor.undo();
        redrawCanvas(primaryCanvas, this.getSudokuGame().getSudoku());
        redrawCanvas(secondaryCanvas, selectedSudokuCell);
    }

    public void openHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/upol/logicgo/windows/misc/how_to_play_window.fxml"));
            Parent root = loader.load();
            HowToPlayController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Jak hrát Sudoku");
            stage.setScene(new Scene(root, 800, 600));
            controller.initialize(sudokuGame.getSudoku().getGameType());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Cell getSelectedCellOutline() {
        return selectedCellOutline;
    }

    public void setSelectedCellOutline(Cell selectedCellOutline) {
        this.selectedCellOutline = selectedCellOutline;
    }
}
