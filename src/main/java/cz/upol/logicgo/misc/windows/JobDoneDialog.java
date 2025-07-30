package cz.upol.logicgo.misc.windows;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static cz.upol.logicgo.misc.Messages.getFormatted;

public class JobDoneDialog {

    public static void show(String filePath) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(getFormatted("export.generation.window.title"));

        Label message = new Label(getFormatted("export.generation.window.message"));
        Label pathLabel = new Label(filePath);
        pathLabel.setStyle("-fx-font-weight: bold;");

        VBox content = new VBox(10, message, pathLabel);
        content.setMinWidth(400);
        dialog.getDialogPane().setContent(content);

        ButtonType openButtonType = new ButtonType(getFormatted("export.generation.open"), ButtonData.LEFT);
        ButtonType closeButtonType = new ButtonType(getFormatted("export.generation.close"), ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(openButtonType, closeButtonType);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == openButtonType) {
                openFile(filePath);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private static void openFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.err.println(path);
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                System.err.println("Desktop API not supported.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
