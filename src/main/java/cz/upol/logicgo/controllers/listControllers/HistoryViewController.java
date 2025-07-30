package cz.upol.logicgo.controllers.listControllers;

import cz.upol.logicgo.StartOfApp;
import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.list.history.CachedGameHistoryCell;
import cz.upol.logicgo.list.history.FilterProperties;
import cz.upol.logicgo.list.history.GameHistoryEntry;
import cz.upol.logicgo.misc.NodeSnapshotting;
import cz.upol.logicgo.misc.enums.SortOption;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.dao.GameDAO;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryViewController {

    private static final int ITEMS_PER_PAGE = 20;

    MainScreenController mainScreenController;

    GameDAO gameDAO = new GameDAO();
    User user;

    @FXML private Pagination pagination;
    @FXML private ListView<GameHistoryEntry> historyListView;
    FilterProperties filterProperties;

    private List<GameHistoryEntry> allEntries;

    @FXML
    public void initialize(User user, MainScreenController mainScreenController) {
        this.user = user;
        this.mainScreenController = mainScreenController;

        filterProperties = new FilterProperties(user).setSortOption(SortOption.LAST_PLAYED);
        List<Game> games = gameDAO.getGamesByFilter(filterProperties);
        this.allEntries = convertToEntries(games);

        historyListView.setCellFactory(list -> new CachedGameHistoryCell(user, mainScreenController, this));

        historyListView.setMinHeight(Region.USE_PREF_SIZE);
        historyListView.setPrefHeight(600);
        historyListView.setMaxHeight(Region.USE_PREF_SIZE);

        setupPagination();
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) allEntries.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setPageFactory(this::updatePage);
    }

    private Node updatePage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, allEntries.size());
        List<GameHistoryEntry> subList = allEntries.subList(fromIndex, toIndex);
        historyListView.getItems().setAll(subList);

        return new Group();
    }

    public List<GameHistoryEntry> convertToEntries(List<Game> games) {
        List<GameHistoryEntry> entries = new ArrayList<>();
        games.forEach(game -> {
            Image image = new NodeSnapshotting().loadThumbnail(game.getThumbnailName());
            String title = String.format("%s - %s", game.getGameType(), ((Sudoku) game).getRegionLayout().getTranslatedName());
            GameHistoryEntry entry = new GameHistoryEntry(game.getId(), image, title, game.getDifficulty(), game.getLastPlayed());
            entries.add(entry);
        });
        return entries;
    }

    @FXML
    public void onFilterButtonAction() throws IOException {
        FilterProperties newFilter = openFilterWindow();
        if (newFilter == null) return;
        filterProperties = newFilter;
        var filteredGames = gameDAO.getGamesByFilter(newFilter);
        allEntries = convertToEntries(filteredGames);
        setupPagination();
    }

    @FXML
    public FilterProperties openFilterWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(StartOfApp.class.getResource("/cz/upol/logicgo/windows/list/filter_window.fxml"));
        Parent root = loader.load();
        FilterController controller = loader.getController();
        Scene scene = new Scene(root, 200, 400, false);
        var filterStage = new Stage();
        controller.setStage(filterStage);
        controller.initialize(new FilterProperties(user));
        filterStage.setTitle("Filtrace");
        filterStage.setScene(scene);
        filterStage.showAndWait();
        return controller.getFilterProperties();
    }

    public void refresh() {
        if (filterProperties == null){
            filterProperties = new FilterProperties(user).setSortOption(SortOption.LAST_PLAYED);
        }
        List<Game> games = gameDAO.getGamesByFilter(filterProperties);
        this.allEntries = convertToEntries(games);
        setupPagination();
    }
}
