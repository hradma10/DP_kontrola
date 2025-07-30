package cz.upol.logicgo.controllers.miscControllers;

import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.*;
import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.redrawCanvas;

public class SolutionWindowController implements Initializable {

    @FXML
    public Canvas canvas;
    public Pane pane;

    @FXML

    Game game;

    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initialize(Game game) {
        this.game = game;

        Sudoku sudokuGame = (Sudoku) game;
        int gridSize = sudokuGame.getType().getGridSize();

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        SudokuCell[][] cells = getBoardCopy(sudokuGame.getSolutionBoard());
        setCellsProperties(cells);
        ChangeListener<Number> resizeListener = (_, _, _) -> {
            adaptBoardToSize(gridSize, cells);
        };

        canvas.widthProperty().addListener(resizeListener);
        canvas.heightProperty().addListener(resizeListener);

        adaptBoardToSize(gridSize, cells);

    }

    private void adaptBoardToSize(int gridSize, SudokuCell[][] cells) {
        double paneWidth = pane.getWidth();
        double paneHeight = pane.getHeight();

        double cellWidth = paneWidth / gridSize;
        double cellHeight = paneHeight / gridSize;

        changeCellSize(cells, cellWidth, cellHeight);
        redrawCanvas(canvas, flattenBoard(cells));

        GraphicsContext gc = canvas.getGraphicsContext2D();

        for (int i = 0; i <= gridSize; i++) {
            gc.setLineWidth(i % 3 == 0 ? 2.0 : 1.0);
            gc.strokeLine(0, i * cellHeight, gridSize * cellWidth, i * cellHeight); // řádky
            gc.strokeLine(i * cellWidth, 0, i * cellWidth, gridSize * cellHeight); // sloupce
        }
    }

    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
