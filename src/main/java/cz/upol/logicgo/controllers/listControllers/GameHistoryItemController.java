package cz.upol.logicgo.controllers.listControllers;

import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.list.history.GameHistoryEntry;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TabType;
import cz.upol.logicgo.misc.helperInit.GameInit;
import cz.upol.logicgo.misc.windows.AlertBox;
import cz.upol.logicgo.model.games.dao.SudokuDAO;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Objects;

public class GameHistoryItemController {

    final private SudokuDAO sudokuDAO = new SudokuDAO();
    @FXML
    public Button viewButton;
    @FXML
    public Button deleteButton;
    public HBox list_item;
    @FXML
    private ImageView thumbnailView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label infoLabel;
    private MainScreenController mainScreenController;

    private HistoryViewController historyViewController;
    User user;


    public void initialize(User user, MainScreenController mainScreenController, HistoryViewController historyViewController) {
        this.mainScreenController = mainScreenController;
        this.historyViewController = historyViewController;
        this.user = user;
    }

    public void setData(GameHistoryEntry entry) {
        thumbnailView.setImage(entry.getThumbnail());
        titleLabel.setText(entry.getTitle());
        infoLabel.setText("Obtížnost: " + Difficulty.getTranslation(entry.getDifficulty())  + " | Hráno: " + entry.getPlayedAt());

        viewButton.setOnAction(e -> {
            try {
                var states = mainScreenController.getActiveTabStates();
                for (var state: states) {
                    if (state.getIdOfGame() != null && Objects.equals(state.getIdOfGame(), entry.getId())){
                        mainScreenController.switchToTab(state.getAssociatedTab());
                        return;
                    }
                }


                GameInit gameInit = new GameInit(user).setId(entry.getId());
                mainScreenController.openTab(TabType.SUDOKU, gameInit);
                historyViewController.refresh();
            } catch (IOException ex) {
                throw new RuntimeException(ex); // TODO fix
            }
        });

        deleteButton.setOnAction(_ -> {
            if (AlertBox.deleteGames()){
                sudokuDAO.delete(entry.getId());
                historyViewController.refresh();
            }
        });
    }
}
