package cz.upol.logicgo.model.games.drawable.bounds;

public interface IRectangleBounds extends IBounds {
    boolean pointInside(Point p);

    boolean pointInside(double x, double y);

    RectangleBounds getBounds();
}
