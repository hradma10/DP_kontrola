package cz.upol.logicgo.list.history;

import cz.upol.logicgo.controllers.listControllers.GameHistoryItemController;
import cz.upol.logicgo.controllers.listControllers.HistoryViewController;
import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CachedGameHistoryCell extends ListCell<GameHistoryEntry> {
    private HBox root;
    private GameHistoryItemController controller;
    private HistoryViewController historyViewController;

    public CachedGameHistoryCell(User user, MainScreenController mainScreenController, HistoryViewController historyViewController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/upol/logicgo/windows/list/game_history_item.fxml"));
            root = loader.load();
            controller = loader.getController();
            controller.initialize(user, mainScreenController, historyViewController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(GameHistoryEntry item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            controller.setData(item);
            setGraphic(root);
        }
    }
}
