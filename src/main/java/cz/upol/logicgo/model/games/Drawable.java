package cz.upol.logicgo.model.games;

import javafx.scene.canvas.Canvas;

public interface Drawable {
    void draw(Canvas canvas);

    boolean isDrawToPrimary();
}
