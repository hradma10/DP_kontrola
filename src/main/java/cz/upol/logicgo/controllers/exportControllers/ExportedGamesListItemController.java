package cz.upol.logicgo.controllers.exportControllers;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.export.ExportedGamesEntry;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.windows.AlertBox;
import cz.upol.logicgo.model.games.dao.ExportedGamesDAO;
import cz.upol.logicgo.model.games.entity.export.ExportedGames;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class ExportedGamesListItemController {

    final private ExportedGamesDAO exportedGamesDAO = new ExportedGamesDAO();

    @FXML
    public Button deleteButton;
    public Label descriptionLabel;
    public Label countLabel;
    public Label difficultyLabel;
    public Button downloadButton;
    public Label timeLabel;
    User user;
    private MainScreenController mainScreenController;
    private ExportMultipleGamesController exportMultipleGamesController;

    public void initialize(User user, MainScreenController mainScreenController, ExportMultipleGamesController exportMultipleGamesController) {
        this.mainScreenController = mainScreenController;
        this.exportMultipleGamesController = exportMultipleGamesController;
        this.user = user;
    }

    public void setData(ExportedGamesEntry entry) {
        descriptionLabel.setText(String.format("%s - %s", entry.getTypeGame(), entry.getGameType()));
        countLabel.setText(String.valueOf(entry.getCount()));
        difficultyLabel.setText(entry.getDifficulty().name());
        timeLabel.setText(entry.getGeneratedAt());

        downloadButton.setOnAction(e -> {
            ExportedGames exportedGames = exportedGamesDAO.findById(entry.getId());
            int count = exportedGames.getSeeds().size();
            Difficulty diff = entry.getDifficulty();
            GameType gameType = entry.getGameType();
            TypeGame typeGame = entry.getTypeGame();
            RegionLayout regionLayout = entry.getRegionLayout();
            exportMultipleGamesController.runGeneration(count, (SudokuType) gameType, regionLayout, diff, typeGame, exportedGames.getSeeds());

        });

        deleteButton.setOnAction(_ -> {
            if (AlertBox.deleteExportedGames()) {
                exportedGamesDAO.delete(entry.getId());
                exportMultipleGamesController.refresh();
            }
        });
    }

}
