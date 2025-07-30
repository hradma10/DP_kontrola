package cz.upol.logicgo.controllers.gameChoiceControllers;

import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.misc.enums.TabType;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.helperInit.GameInit;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.animation.ScaleTransition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;

public class GameChoiceController {
    public TilePane sudokuChoiceTilePane;
    public TilePane mazeChoiceTilePane;
    public TilePane bridgeChoiceTilePane;
    public VBox content;

    User user;

    MainScreenController mainController;

    public void initialize(User user, MainScreenController mainController) {
        this.user = user;
        this.mainController = mainController;
        createTypes();
    }

    @FXML
    private void createTypes() {

        TilePane sudokuVariantPane = (TilePane) content.lookup("#sudokuChoiceTilePane");

        var sudokuPaneWidthProperty = sudokuVariantPane.widthProperty();
        Node mini = createGameBox(SudokuType.FOUR.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/four.png").toExternalForm()), SudokuType.FOUR, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node small = createGameBox(SudokuType.SIX.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/six.png").toExternalForm()), SudokuType.SIX, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node five = createGameBox(SudokuType.FIVE.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/five.png").toExternalForm()), SudokuType.FIVE, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node seven = createGameBox(SudokuType.SEVEN.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/seven.png").toExternalForm()), SudokuType.SEVEN, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node eight = createGameBox(SudokuType.EIGHT.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/eight.png").toExternalForm()), SudokuType.EIGHT, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node standard = createGameBox(SudokuType.NINE.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/nine.png").toExternalForm()), SudokuType.NINE, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node ten = createGameBox(SudokuType.TEN.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/ten.png").toExternalForm()), SudokuType.TEN, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node twelve = createGameBox(SudokuType.TWELVE.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/twelve.png").toExternalForm()), SudokuType.TWELVE, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node jumbo = createGameBox(SudokuType.SIXTEEN.getDescription(), new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/sixteen.png").toExternalForm()), SudokuType.SIXTEEN, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        Node seeded = createSeededBox("Seeded", new Image(getClass().getResource("/cz/upol/logicgo/images/games/sudoku/nine.png").toExternalForm()), null, TypeGame.SUDOKU, sudokuPaneWidthProperty);
        sudokuVariantPane.getChildren().addAll(mini, five, small, seven, eight, standard, ten, twelve, jumbo, seeded);

        TilePane bridgeVariantPane = (TilePane) content.lookup("#bridgeChoiceTilePane");

        TilePane mazeVariantPane = (TilePane) content.lookup("#mazeChoiceTilePane");
        // TODO další

    }

    private Node createGameBox(String text, Image image, GameType gameType, TypeGame typeGame, ReadOnlyDoubleProperty parentWidthProperty) {
        StackPane root = new StackPane();
        root.getStyleClass().add("gameTypeBox");

        VBox box = new VBox();
        box.setAlignment(Pos.BOTTOM_CENTER);
        box.setSpacing(5);

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(160);
        imageView.setFitHeight(160);
        imageView.getStyleClass().add("gamePreviewImage");

        StackPane labelContainer = new StackPane();
        labelContainer.getStyleClass().add("labelContainer");

        Label name = new Label(text);
        name.getStyleClass().add("gameNameLabel");
        labelContainer.getChildren().add(name);

        box.getChildren().addAll(imageView, labelContainer);
        root.getChildren().add(box);

        parentWidthProperty.addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();


            double scale = 1.0;
            if (width < 800) {
                scale = 0.8 + 0.2 * (width / 800);
                if (scale < 0.8) scale = 0.8;
            }

            root.setScaleX(scale);
            root.setScaleY(scale);
        });

        root.setOnMouseClicked(e -> {
            GameInit gameInit = new GameInit(user).setGameType(gameType);
            switch (typeGame) {
                case SUDOKU -> {
                    try {
                        mainController.openTab(TabType.SUDOKU_SETTINGS, gameInit);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                case MAZE -> {}
                case BRIDGE -> {}
            }
        });

        root.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), root);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        root.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), root);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });

        return root;
    }


    private Node createSeededBox(String text, Image image, GameType gameType, TypeGame typeGame,
                               ReadOnlyDoubleProperty parentWidthProperty) {
        StackPane root = new StackPane();
        root.getStyleClass().add("gameTypeBox");

        VBox box = new VBox();
        box.setAlignment(Pos.BOTTOM_CENTER);
        box.setSpacing(5);

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(160);
        imageView.setFitHeight(160);
        imageView.getStyleClass().add("gamePreviewImage");

        StackPane imageContainer = new StackPane(imageView);
            Region darkOverlay = new Region();
            darkOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            darkOverlay.setPrefSize(160, 140);

            Label questionMark = new Label("?");
            questionMark.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: white;");
            StackPane.setAlignment(questionMark, Pos.CENTER);

            imageContainer.getChildren().addAll(darkOverlay, questionMark);


        StackPane labelContainer = new StackPane();
        labelContainer.getStyleClass().add("labelContainer");

        Label name = new Label(text);
        name.getStyleClass().add("gameNameLabel");
        labelContainer.getChildren().add(name);

        box.getChildren().addAll(imageContainer, labelContainer);
        root.getChildren().add(box);

        parentWidthProperty.addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();
            double scale = 1.0;
            if (width < 800) {
                scale = 0.8 + 0.2 * (width / 800);
                if (scale < 0.8) scale = 0.8;
            }
            root.setScaleX(scale);
            root.setScaleY(scale);
        });

        root.setOnMouseClicked(e -> {
            GameInit gameInit = new GameInit(user).setGameType(gameType);
            switch (typeGame) {
                case SUDOKU -> {
                    try {
                        mainController.openTab(TabType.SUDOKU_SETTINGS, gameInit);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                case MAZE -> {}
                case BRIDGE -> {}
            }
        });

        root.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), root);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        root.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), root);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });

        return root;
    }



}
