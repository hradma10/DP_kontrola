package cz.upol.logicgo.model.games.drawable.bounds;


import cz.upol.logicgo.algorithms.drawing.CohenSutherlandAlgorithm;

import java.util.Objects;

import static cz.upol.logicgo.model.games.drawable.bounds.CalculateBorderPoints.CoordinatesComputeFromCenter.*;
import static cz.upol.logicgo.model.games.drawable.bounds.CalculateBorderPoints.CoordinatesComputeFromStaticPoint.centerFromTopLeftX;
import static cz.upol.logicgo.model.games.drawable.bounds.CalculateBorderPoints.CoordinatesComputeFromStaticPoint.centerFromTopLeftY;


/**
 * třída reprezentující hranice obdelníkového prvku
 */
public class RectangleBounds extends Bounds {
    private final Point center;
    private final Point topLeft;
    private final Point bottomRight;
    private double width, height;

    public RectangleBounds(double topLeftX, double topLeftY, double width, double height) {
        var centerX = centerFromTopLeftX(topLeftX, width);
        var centerY = centerFromTopLeftY(topLeftY, height);
        this.center = new Point(centerX, centerY);
        this.width = width;
        this.height = height;
        topLeft = new Point(topLeftX, topLeftY);
        bottomRight = new Point(bottomRightXFromCenter(centerX, width), bottomRightYFromCenter(centerY, height));
    }


    public void recalculate(double x, double y, double width, double height) {
        this.getCenter().setBothCoordinates(x, y);
        this.setHeight(height);
        this.setWidth(width);
        setTopLeftAndBottomRightCoordinates(x, y, width, height);
    }

    public void recalculate(int row, int col, double width, double height) {
        this.setHeight(height);
        this.setWidth(width);
        double x = col * width;
        double y = row * height;
        setTopLeftCoordinates(x, y);
        setCenterX(centerFromTopLeftX(x, width));
        setCenterY(centerFromTopLeftY(y, height));
        setBottomRightCoordinates(x + width, y + height);
    }

    private void setTopLeftAndBottomRightCoordinates(double x, double y, double width, double height) {
        this.setTopLeftX(topLeftXFromCenter(x, width));
        this.setTopLeftY(topLeftYFromCenter(y, height));
        this.setBottomRightX(bottomRightXFromCenter(x, width));
        this.setBottomRightY(bottomRightYFromCenter(y, height));
    }

    private void setTopLeftCoordinates(double x, double y) {
        this.setTopLeftX(x);
        this.setTopLeftY(y);
    }

    private void setBottomRightCoordinates(double x, double y) {
        this.setBottomRightX(x);
        this.setBottomRightY(y);
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean doElementsIntersect(IBounds element) {
        return switch (element) {
            case IRectangleBounds rectangleBounds -> doElementsIntersect(rectangleBounds);
            case ILineBounds lineBounds -> doElementsIntersect(lineBounds);
            default -> false;
        };
    }

    public boolean doElementsIntersect(IRectangleBounds element) {
        return rectangleIntersectTest(element);
    }

    public boolean doLineElementsIntersect(ILineBounds element) {
        /*var bounds = drawableStraightLine.getLineBounds();
        return isStraightLineIntersectingRectangle(bounds.getStartPoint(), bounds.getEndPoint());*/
        return false;
    }

    private boolean rectangleIntersectTest(IRectangleBounds rectangleBounds) {
        var other = rectangleBounds.getBounds();
        boolean xOverlap = this.getTopLeftX() < other.getTopLeftX() + other.getWidth() && this.getTopLeftX() + this.getWidth() > other.getTopLeftX();
        boolean yOverlap = this.getTopLeftY() < other.getTopLeftY() + other.getHeight() && this.getTopLeftY() + this.getHeight() > other.getTopLeftY();
        return xOverlap && yOverlap;
    }

    public boolean isStraightLineIntersectingRectangle(Point startPoint, Point endPoint) {
        return CohenSutherlandAlgorithm.isIntersecting(this.getTopLeftX(), this.getTopLeftY(), this.getBottomRightX(), this.getBottomRightY(), startPoint, endPoint);
    }

    public boolean pointInBoundsOfElement(double x, double y) {
        double topLeftX = this.getTopLeftX();
        double topLeftY = this.getTopLeftY();
        double bottomRightX = this.getBottomRightX();
        double bottomRightY = this.getBottomRightY();

        boolean withinXBounds = (topLeftX <= x) && (x <= bottomRightX);
        boolean withinYBounds = (topLeftY <= y) && (y <= bottomRightY);

        return withinXBounds && withinYBounds;
    }

    public boolean pointInBoundsOfElement(Point point) {
        return pointInBoundsOfElement(point.getX(), point.getY());
    }


    public double getCenterX() {
        return this.getCenter().getX();
    }

    public void setCenterX(double centerX) {
        this.getCenter().setX(centerX);
    }

    public double getCenterY() {
        return this.getCenter().getY();
    }

    public void setCenterY(double centerY) {
        this.getCenter().setY(centerY);
    }

    public double getTopLeftX() {
        return this.getTopLeft().getX();
    }

    public void setTopLeftX(double rightTopLeftX) {
        this.getTopLeft().setX(rightTopLeftX);
    }

    public double getTopLeftY() {
        return this.getTopLeft().getY();
    }

    public void setTopLeftY(double rightTopLeftY) {
        this.getTopLeft().setY(rightTopLeftY);
    }

    public double getBottomRightX() {
        return this.getBottomRight().getX();
    }

    public void setBottomRightX(double bottomRightX) {
        this.getBottomRight().setX(bottomRightX);
    }

    public double getBottomRightY() {
        return this.getBottomRight().getY();
    }

    public void setBottomRightY(double bottomRightY) {
        this.getBottomRight().setY(bottomRightY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RectangleBounds that)) return false;
        if (!super.equals(o)) return false;
        return Double.compare(that.getWidth(), this.getWidth()) == 0 && Double.compare(that.getHeight(), this.getHeight()) == 0;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    public Point getCenter() {
        return center;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWidth(), getHeight());
    }
}
