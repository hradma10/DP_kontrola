package cz.upol.logicgo.misc;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Tab;

public class TabState {
    private final BooleanProperty changeProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty doneProperty = new SimpleBooleanProperty(true); // true = nic neběží
    private final Tab associatedTab;
    private Long idOfGame = null;

    public TabState(Tab associatedTab) {
        this.associatedTab = associatedTab;
    }

    public BooleanProperty changeProperty() {
        return changeProperty;
    }

    public void setChangePending(){
        changeProperty.set(true);
    }

    public void unsetChangePending(){
        changeProperty.set(false);
    }

    public BooleanProperty doneProperty() {
        return doneProperty;
    }

    public boolean isChangePending() {
        return changeProperty.get();
    }

    public boolean isDone() {
        return doneProperty.get();
    }

    public void markSavingStarted() {
        doneProperty.set(false);
    }

    public void markSavingFinished() {
        changeProperty.set(false);
        doneProperty.set(true);
    }

    public Tab getAssociatedTab() {
        return associatedTab;
    }

    public Long getIdOfGame() {
        return idOfGame;
    }

    public void setIdOfGame(Long idOfGame) {
        this.idOfGame = idOfGame;
    }
}
