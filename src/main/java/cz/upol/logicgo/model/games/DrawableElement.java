package cz.upol.logicgo.model.games;

import cz.upol.logicgo.model.games.drawable.bounds.Bounds;

import java.util.Objects;
import java.util.UUID;

public abstract class DrawableElement implements Drawable {
    Bounds bounds;
    private boolean selected = false;
    private boolean drawToPrimary = true;
    final private UUID id = UUID.randomUUID();

    public DrawableElement(Bounds bounds, boolean drawToPrimary) {
        this.bounds = bounds;
        this.drawToPrimary = drawToPrimary;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDrawToPrimary() {
        return drawToPrimary;
    }

    public void setDrawToPrimary(boolean drawToPrimary) {
        this.drawToPrimary = drawToPrimary;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DrawableElement that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
