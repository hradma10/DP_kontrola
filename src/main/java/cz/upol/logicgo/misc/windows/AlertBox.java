package cz.upol.logicgo.misc.windows;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static cz.upol.logicgo.controllers.screenControllers.SudokuGameController.getFormattedTime;
import static cz.upol.logicgo.misc.Messages.getFormatted;

public class AlertBox {

    public static boolean initCloseApp() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(getFormatted("confirmDialogTitle"));
        dialog.setHeaderText(getFormatted("closeBox.header"));

        dialog.setContentText(getFormatted("closeBox.content"));

        ButtonType buttonOK = new ButtonType(getFormatted("closeBox.okButton"), ButtonBar.ButtonData.FINISH);
        ButtonType buttonCancel = new ButtonType(getFormatted("cancelButton"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(buttonOK, buttonCancel);
        Optional<ButtonType> result = dialog.showAndWait();

        return result.isPresent() && result.get() == buttonOK;
    }

    public static boolean initCloseGame() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(getFormatted("confirmDialogTitle"));
        dialog.setHeaderText(getFormatted("closeGame.header"));

        dialog.setContentText(getFormatted("closeGame.content"));

        ButtonType buttonOK = new ButtonType(getFormatted("closeGame.okButton"), ButtonBar.ButtonData.FINISH);
        ButtonType buttonCancel = new ButtonType(getFormatted("cancelButton"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(buttonOK, buttonCancel);
        Optional<ButtonType> result = dialog.showAndWait();

        return result.isPresent() && result.get() == buttonOK;
    }

    public static Optional<Boolean> saveAlertNotes() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(getFormatted("confirmDialogTitle"));
        dialog.setHeaderText(getFormatted("notes.alert.header"));
        ButtonType buttonSave = new ButtonType(getFormatted("notes.alert.saveButton"), ButtonBar.ButtonData.YES);
        ButtonType buttonDontSave = new ButtonType(getFormatted("notes.alert.doNotSaveButton"), ButtonBar.ButtonData.NO);
        ButtonType buttonCancel = new ButtonType(getFormatted("cancelButton"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().setAll(buttonSave, buttonDontSave, buttonCancel);
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(AlertBox.class.
                getResource("/cz/upol/logicgo/css/alert_box.css")).toExternalForm());

        var result = dialog.showAndWait();
        if (result.isEmpty()) return Optional.empty();
        if (result.get().equals(buttonSave)) {
            return Optional.of(true);
        } else if (result.get().equals(buttonDontSave)) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }


    public static Boolean deleteGames() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(getFormatted("confirmDialogTitle"));
        dialog.setHeaderText(getFormatted("gamelist.alert.header"));

        ButtonType buttonYes = new ButtonType(getFormatted("gamelist.alert.saveButton"), ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType(getFormatted("cancelButton"), ButtonBar.ButtonData.NO);

        dialog.getButtonTypes().setAll(buttonYes, buttonNo);

        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(AlertBox.class
                .getResource("/cz/upol/logicgo/css/alert_box.css")).toExternalForm());

        var result = dialog.showAndWait();

        return result.map(buttonType -> buttonType.equals(buttonYes)).orElse(false);

    }

    public static Boolean deleteExportedGames() { // TODO vytvořit nový text
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(getFormatted("confirmDialogTitle"));
        dialog.setHeaderText(getFormatted("gamelist.alert.header"));

        ButtonType buttonYes = new ButtonType(getFormatted("gamelist.alert.saveButton"), ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType(getFormatted("cancelButton"), ButtonBar.ButtonData.NO);

        dialog.getButtonTypes().setAll(buttonYes, buttonNo);

        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(AlertBox.class
                .getResource("/cz/upol/logicgo/css/alert_box.css")).toExternalForm());

        var result = dialog.showAndWait();

        return result.map(buttonType -> buttonType.equals(buttonYes)).orElse(false);

    }

    public static void YouWonWindow(Duration timer) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle(getFormatted("sudoku.win.window.title"));
        dialog.getDialogPane().getStylesheets().add(
                AlertBox.class.getResource("/cz/upol/logicgo/css/alert_box.css").toExternalForm()
        );
        if (timer != null) {
            long totalSeconds = timer.toSeconds();

            String time = getFormattedTime(totalSeconds);
            dialog.setHeaderText(getFormatted("sudoku.win.window.header", time));
        }
        ButtonType buttonOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        dialog.getButtonTypes().setAll(buttonOK);
        dialog.showAndWait();
    }


    public static void OkWindowError(String headerText) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("Chyba");
        dialog.setHeaderText(headerText);

        ButtonType buttonOK = new ButtonType("Ukončit", ButtonBar.ButtonData.OK_DONE);

        dialog.getButtonTypes().setAll(buttonOK);
        dialog.showAndWait();
    }

    public static void initExistingUser(String username) {
        OkWindowError(getFormatted("register.existingUser",  username));
    }

    public static void initNotExistingUser(String username) {
        OkWindowError(getFormatted("login.notExistingUser",  username));
    }

    public static void initNonValidPassword() {
        OkWindowError(getFormatted("login.wrongPassword"));
    }

}
