package cz.upol.logicgo.handlers.sudoku;


import cz.upol.logicgo.algorithms.sudoku.SudokuGame;
import cz.upol.logicgo.controllers.screenControllers.SudokuGameController;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

public class MouseKeySudokuHandlers {

    final private MouseClickedSudokuHandler mouseClickedHandler;
    final private KeyPressedSudokuHandler keyPressedHandler;
    final private KeyReleasedSudokuHandler keyReleasedHandler;
    final private MouseReleasedSudokuHandler mouseReleasedHandler;
    final private MouseMovedSudokuHandler mouseMovedHandler;

    public MouseKeySudokuHandlers(SudokuGame sudokuGameState, SudokuGameController sudokuGameController) {
        mouseClickedHandler = new MouseClickedSudokuHandler(sudokuGameState, sudokuGameController);
        keyPressedHandler = new KeyPressedSudokuHandler(sudokuGameState, sudokuGameController);
        keyReleasedHandler = new KeyReleasedSudokuHandler(sudokuGameState, sudokuGameController);
        mouseReleasedHandler = new MouseReleasedSudokuHandler(sudokuGameState, sudokuGameController);
        mouseMovedHandler = new MouseMovedSudokuHandler(sudokuGameState, sudokuGameController);
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        if (consumedEvent(mouseEvent)) return;
        this.getMouseClickedHandler().onMouseClicked(mouseEvent);
    }

    private boolean consumedEvent(Event event) {
        if (Objects.requireNonNull(event.getSource()) instanceof Canvas) {
            return false;
        }
        event.consume();
        return true;
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        this.getKeyPressedHandler().onKeyPressed(keyEvent);
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        if (consumedEvent(keyEvent)) return;
        this.getKeyReleasedHandler().onKeyReleased(keyEvent);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        if (consumedEvent(mouseEvent)) return;
        this.getMouseReleasedHandler().onMouseReleased(mouseEvent);
    }

    public void onMouseMoved(MouseEvent mouseEvent) {
        if (consumedEvent(mouseEvent)) return;
        this.getMouseMovedHandler().onMouseMoved(mouseEvent);
    }

    public MouseClickedSudokuHandler getMouseClickedHandler() {
        return mouseClickedHandler;
    }

    public KeyPressedSudokuHandler getKeyPressedHandler() {
        return keyPressedHandler;
    }

    public KeyReleasedSudokuHandler getKeyReleasedHandler() {
        return keyReleasedHandler;
    }

    public MouseReleasedSudokuHandler getMouseReleasedHandler() {
        return mouseReleasedHandler;
    }

    public MouseMovedSudokuHandler getMouseMovedHandler() {
        return mouseMovedHandler;
    }
}

