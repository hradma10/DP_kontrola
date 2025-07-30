package cz.upol.logicgo.model.games.drawable.elements.sudoku;

import cz.upol.logicgo.model.games.DrawableElement;
import cz.upol.logicgo.model.games.drawable.bounds.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.io.Serializable;

import static cz.upol.logicgo.model.games.drawable.bounds.CalculateBorderPoints.CoordinatesComputeFromCenter.topLeftXFromCenter;
import static cz.upol.logicgo.model.games.drawable.bounds.CalculateBorderPoints.CoordinatesComputeFromCenter.topLeftYFromCenter;

public class Cell extends DrawableElement implements Serializable {

    final private Point point;
    private double width;
    private double height;
    private int row;
    private int col;


    public Cell(double x, double y, double width, double height, int row, int col) {
        super(null, true);
        this.point = new Point(x, y);
        this.width = width;
        this.height = height;
        this.row = row;
        this.col = col;
    }

    public Cell(Point point, double width, double height, int row, int col) {
        this(point.getX(), point.getY(), width, height, row, col);
    }

    public void changeSizeAndPosition(double width, double height) {
        this.setX(col * width);
        this.setY(row * height);
        this.setWidth(width);
        this.setHeight(height);
    }

    @Override
    public void draw(Canvas canvas) {
       /* var gc =  canvas.getGraphicsContext2D();
            gc.setStroke(Color.GREEN);
            gc.strokeRect(point.getX(), point.getY(), width, height);*/
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public Point getPoint() {
        return point;
    }

    public double getX() {
        return point.getX();
    }

    public void setX(double x) {
        point.setX(x);
    }

    public double getY() {
        return point.getY();
    }

    public void setY(double y) {
        point.setY(y);
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
