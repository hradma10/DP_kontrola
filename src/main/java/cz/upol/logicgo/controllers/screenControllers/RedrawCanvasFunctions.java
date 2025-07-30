package cz.upol.logicgo.controllers.screenControllers;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.model.games.Drawable;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.Cell;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Objects;


/**
 * metody obsluhující překreslování pláten ( instance Canvas)
 */
public class RedrawCanvasFunctions {

    /**
     * vymaže vše na canvasu
     */
    public static void clearCanvas(Canvas canvas) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * vymaže vše na canvasu
     */
    public static void clearCanvases(Canvas... canvases) {
        for (Canvas canvas : canvases) {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
        }
    }

    /**
     * vykreslí
     *
     * @param canvas
     * @param drawables
     */
    public static void redrawCanvas(Canvas canvas, boolean drawToPrimary, Drawable... drawables) {
        clearCanvas(canvas);
        if (areElsNull(drawables)) return;
        for (Drawable drawable : drawables) {
            if (drawable.isDrawToPrimary() == drawToPrimary) {
                drawable.draw(canvas);
            }
        }
    }

    public static boolean areElsNull(Drawable ... drawables) {
        if (drawables == null) return true;
        return Arrays.stream(drawables).anyMatch(Objects::isNull);
    }

    public static void redrawCanvas(Canvas canvas, Drawable... drawables) {
        clearCanvas(canvas);
        if (drawables == null) return;
        for (Drawable drawable : drawables) {
            if (drawable == null) continue;
            drawable.draw(canvas);
        }
    }

    public static void redrawCanvas(Canvas canvas, Sudoku sudoku) {
        clearCanvas(canvas);
        sudoku.draw(canvas);
    }

    public static void strokeBoard(RegionLayout regionLayout, double cellWidth, double cellHeight, GraphicsContext gc) {
        var regionMap = regionLayout.getRegions();
        int size = regionMap.length;

        gc.setStroke(Color.BLACK);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int currentRegion = regionMap[row][col];

                double x = col * cellWidth;
                double y = row * cellHeight;
                double rightX = x + cellWidth;
                double bottomY = y + cellHeight;

                if (row > 0 && regionMap[row - 1][col] != currentRegion) {
                    gc.setLineWidth(3.0);
                    gc.strokeLine(x, y, x + cellWidth, y);
                }

                if (row < size - 1 && regionMap[row + 1][col] != currentRegion) {
                    gc.setLineWidth(3.0);
                    gc.strokeLine(x, bottomY, x + cellWidth, bottomY);
                }

                if (col > 0 && regionMap[row][col - 1] != currentRegion) {
                    gc.setLineWidth(3.0);
                    gc.strokeLine(x, y, x, y + cellHeight);
                }

                if (col < size - 1 && regionMap[row][col + 1] != currentRegion) {
                    gc.setLineWidth(3.0);
                    gc.strokeLine(rightX, y, rightX, y + cellHeight);
                }
            }
        }

        gc.setLineWidth(2.0);
        gc.strokeRect(0, 0, size * cellWidth, size * cellHeight);

        gc.setLineWidth(1.0);
        for (int i = 1; i < size; i++) {
            double y = i * cellHeight;
            gc.strokeLine(0, y, size * cellWidth, y);

            double x = i * cellWidth;
            gc.strokeLine(x, 0, x, size * cellHeight);
        }
    }




    public static void animateTo(SudokuCell from, SudokuCell to, Cell cell, Canvas canvas) {
        final double speed = 25;
        final double x = to.getTopLeft().getX();
        final double y = to.getTopLeft().getY();

        cell.setX(from.getTopLeft().getX());
        cell.setY(from.getTopLeft().getY());

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double currentX = cell.getX();
                double currentY = cell.getY();

                double dx = x - currentX;
                double dy = y - currentY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > speed) {
                    double nx = dx / distance;
                    double ny = dy / distance;

                    cell.setX(currentX + nx * speed);
                    cell.setY(currentY + ny * speed);
                } else {
                    cell.setX(x);
                    cell.setY(y);
                    this.stop();
                }

                clearCanvas(canvas);
                cell.draw(canvas);
            }
        };

        timer.start();
    }


}
