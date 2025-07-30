package cz.upol.logicgo.controllers.screenControllers;

import cz.upol.logicgo.StartOfApp;
import cz.upol.logicgo.controllers.exportControllers.ExportMultipleGamesController;
import cz.upol.logicgo.controllers.gameChoiceControllers.GameChoiceController;
import cz.upol.logicgo.controllers.gameChoiceControllers.SudokuSettingsMenu;
import cz.upol.logicgo.controllers.listControllers.HistoryViewController;
import cz.upol.logicgo.exceptions.game.GameLoadFail;
import cz.upol.logicgo.misc.TabState;
import cz.upol.logicgo.misc.enums.TabType;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.helperInit.GameInit;
import cz.upol.logicgo.misc.windows.AlertBox;
import cz.upol.logicgo.model.games.dao.UserDAO;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;



public class MainScreenController implements Initializable {

    final private UserDAO userDAO;
    private final Map<Tab, TabState> tabStateMap = new HashMap<>();
    @FXML
    public TabPane tabPane;
    @FXML
    public MenuBar menuBar;
    public Label mainScreenTextHelp;
    public BorderPane root;
    private User user;
    private Stage stage;

    public MainScreenController() {
        userDAO = new UserDAO();
    }

    public MainScreenController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initialize(User loggedUser) throws IOException {
        this.user = loggedUser;
        user.setLastLogged(LocalDateTime.now());
        userDAO.update(user);

        openTab(TabType.MAIN);

        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);

        tabPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getSource() == tabPane && event.getTarget() != tabPane) {
                return;
            }

            Tab selected = tabPane.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Node content = selected.getContent();
                if (content == null) return;
                switch ((TabType) selected.getUserData()) {
                    case SUDOKU -> {
                        Node target = content.lookup("#gamePane");

                        if (target instanceof Pane pane) {
                            pane.fireEvent(event);
                        }
                        event.consume();
                    }
                    default -> event.consume();
                }

            }
        });

        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();

            if ((event.isControlDown() && (code == KeyCode.TAB || code == KeyCode.PAGE_DOWN || code == KeyCode.PAGE_UP || code == KeyCode.RIGHT || code == KeyCode.LEFT))
                    || (event.isAltDown() && (code == KeyCode.RIGHT || code == KeyCode.LEFT))) {
                event.consume();
            }
        });

        stage.setOnCloseRequest(event -> {
            event.consume();

            if (getActiveTabStates().isEmpty() || AlertBox.initCloseApp()) close();
        });


    }

    public void openTab(TabType tabType) throws IOException {
        openTab(tabType, null);
    }

    public void switchToTab(Tab tab) {
        for (Tab t : tabPane.getTabs()) {
            if (t == tab) {
                tabPane.getSelectionModel().select(tab);
            }
        }
    }

    public boolean checkOpen(TabType tabType){
        for (Tab t : tabPane.getTabs()) {
            if (t.getUserData() == tabType) {
                tabPane.getSelectionModel().select(t);
                return true;
            }
        }
        return false;
    }

    public int closeTab(Tab tab) {
        int index = tabPane.getTabs().indexOf(tab);
        if (index != -1) {
            tabPane.getTabs().remove(tab);
        }
        tabStateMap.remove(tab);
        return index;
    }

    public void closeCurrentTab() {
        int index = tabPane.getTabs().indexOf(tabPane.getSelectionModel().getSelectedItem());
        if (index != -1) tabPane.getTabs().remove(index);
    }

    public void close() {
        ArrayList<TabState> tabStates = getActiveTabStates();

        for (TabState tabState : tabStates) {
            tabState.markSavingStarted();
        }

        watchUntilAllDone(() -> {
            user.setLastLogged(LocalDateTime.now());
            userDAO.update(user);
            stage.close();
            Platform.exit();
            System.exit(0);
        }, tabStates);
    }

    public void closeCurrentAndReplace(TabType tabType) throws IOException {
        closeCurrentAndReplace(tabType, null);

    }

    public void setTextToLabel(String text) {
        mainScreenTextHelp.setText(text);
    }

    public void unsetTextToLabel() {
        mainScreenTextHelp.setText("");
    }

    public void closeCurrentAndReplace(TabType tabType, GameInit gameInit) throws IOException {
        Tab current = tabPane.getSelectionModel().getSelectedItem();
        if (current == null) return;

        int index = closeTab(current);
        if (index > tabPane.getTabs().size()) {
            index = tabPane.getTabs().size();
        }

        openTab(tabType, gameInit, index);
    }

    public void openTab(TabType tabType, GameInit gameInit) throws IOException {
        openTab(tabType, gameInit, null);
    }


    public Tab openTab(TabType tabType, GameInit gameInit, Integer index) throws IOException {
        Tab tab = null;
        TabState tabState = null;

        switch (tabType) {
            case MAIN -> {
                String resourcePath = "/cz/upol/logicgo/windows/welcome_screen.fxml";
                FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource(resourcePath));
                Parent content = loader.load();
                WelcomeScreenController controller = loader.getController();
                content.getStyleClass().add("root");
                tab = new Tab("Úvod", content);
                controller.initialize(user, this);
                controller.setStage(stage);
                tab.setClosable(false);
            }

            case GAME_LIST -> {
                String resourcePath = "/cz/upol/logicgo/windows/gameChoice/game_choice.fxml";
                FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource(resourcePath));
                Parent content = loader.load();
                content.getStyleClass().add("root");
                GameChoiceController controller = loader.getController();
                tab = new Tab("Seznam", content);
                controller.initialize(user, this);
                tab.setClosable(true);
            }

            case SUDOKU -> {
                try {
                    String resourcePath = "/cz/upol/logicgo/windows/sudoku_game.fxml";
                    FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource(resourcePath));
                    Parent content = loader.load();
                    SudokuGameController controller = loader.getController();
                    content.getStyleClass().add("root");
                    tab = new Tab("Sudoku", content);
                    tabState = new TabState(tab);
                    controller.initialize(gameInit, this, tabState);
                    tab.setClosable(true);
                } catch (GameLoadFail e) {
                    System.out.println(e.getMessage());
                }
            }
            case SUDOKU_SETTINGS -> {
                String resourcePath = "/cz/upol/logicgo/windows/gameChoice/sudoku_settings_menu.fxml";
                FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource(resourcePath));
                Parent content = loader.load();
                content.getStyleClass().add("root");
                SudokuSettingsMenu controller = loader.getController();
                tab = new Tab("Sudoku", content);
                controller.initialize(user, this, gameInit);
                tab.setClosable(true);
            }

            case PLAYED_LIST -> {
                String resourcePath = "/cz/upol/logicgo/windows/list/history_view.fxml";
                FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource(resourcePath));
                Parent content = loader.load();
                content.getStyleClass().add("root");
                HistoryViewController controller = loader.getController();
                tab = new Tab("Historie", content);
                controller.initialize(user, this);
                tab.setClosable(true);
            }
            case EXPORT_MULTIPLE -> {
                String resourcePath = "/cz/upol/logicgo/windows/exportMultiple/export_multiple_games.fxml";
                FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource(resourcePath));
                Parent content = loader.load();
                content.getStyleClass().add("root");
                ExportMultipleGamesController controller = loader.getController();
                tab = new Tab("Export", content);
                tabState = new TabState(tab);
                controller.initialize(user, this, tabState);
                tab.setClosable(true);
            }
        }

        if (tab != null) {
            tab.setUserData(tabType);
            if (index != null) {
                if (index < 0) index = 0;
                tabPane.getTabs().add(index, tab);
            } else {
                tabPane.getTabs().add(tab);
            }
            tabPane.getSelectionModel().select(tab);
        }
        if (tabState != null) {
            TabState finalTabState = tabState;
            Tab finalTab = tab;
            tab.setOnCloseRequest(event -> {
                if (!finalTabState.isDone()) {
                    event.consume();
                    boolean close = AlertBox.initCloseGame();
                    if (close) {
                        this.closeTab(finalTab);
                    }else return;
                } else {
                    tabStateMap.remove(finalTab);
                    return;
                }
                tabStateMap.remove(finalTab);
            });
            tabStateMap.put(tab, tabState);
        }

        return tab;
    }


    public ArrayList<TabState> getActiveTabStates() {
        return tabStateMap.values()
                .stream().filter(TabState::isChangePending)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean allDone(ArrayList<TabState> tabStates) {
        return tabStates.stream().allMatch(TabState::isDone);
    }


    public void watchUntilAllDone(Runnable onAllDone, ArrayList<TabState> tabStates) {
        if (allDone(tabStates)) {
            onAllDone.run();
            return;
        }

        for (TabState state : tabStates) {
            state.doneProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    if (allDone(tabStates)) {
                        onAllDone.run();
                    }
                }
            });
        }
    }


    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
