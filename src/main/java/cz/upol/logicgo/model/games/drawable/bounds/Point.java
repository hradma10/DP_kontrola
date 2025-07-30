package cz.upol.logicgo.model.games.drawable.bounds;

import java.util.Objects;

/**
 * třída reprezentující bod
 */
public final class Point {
    private double x = 0;
    private double y = 0;

    public Point() {

    }

    public Point(Point point) {
        this(point.getX(), point.getY());
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point makeCopy() {
        return new Point(this);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setBothCoordinates(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public void setBothCoordinates(Point point) {
        setBothCoordinates(point.getX(), point.getY());
    }

    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point point)) return false;
        return Double.compare(getX(), point.getX()) == 0 && Double.compare(getY(), point.getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Point{" + "x=" + x + ", y=" + y + '}';
    }

}
