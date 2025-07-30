package cz.upol.logicgo.export;

import cz.upol.logicgo.controllers.exportControllers.ExportMultipleGamesController;
import cz.upol.logicgo.controllers.exportControllers.ExportedGamesListItemController;
import cz.upol.logicgo.controllers.listControllers.GameHistoryItemController;
import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.list.history.GameHistoryEntry;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ExportedHistoryCell extends ListCell<ExportedGamesEntry> {
    private HBox root;
    private ExportedGamesListItemController controller;

    public ExportedHistoryCell(User user, MainScreenController mainScreenController, ExportMultipleGamesController exportMultipleGamesController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/upol/logicgo/windows/exportMultiple/export_multiple_list_item.fxml"));
            root = loader.load();
            controller = loader.getController();
            controller.initialize(user, mainScreenController, exportMultipleGamesController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(ExportedGamesEntry item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            controller.setData(item);
            setGraphic(root);
        }
    }
}
