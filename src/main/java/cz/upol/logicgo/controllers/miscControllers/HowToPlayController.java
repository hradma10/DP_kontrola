package cz.upol.logicgo.controllers.miscControllers;

import cz.upol.logicgo.misc.enums.TypeGame;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HowToPlayController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize(TypeGame typeGame) {
        String resource = switch (typeGame) {
            case SUDOKU -> "/cz/upol/logicgo/html/how_to_play_sudoku.html";
            case MAZE, BRIDGE -> "";
        };
        try (InputStream input = getClass().getResourceAsStream(resource)) {
            if (input != null) {
                String html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                webView.getEngine().loadContent(html);
            } else {
                webView.getEngine().loadContent("<html><body><h1>Soubor how_to_play.html nenalezen.</h1></body></html>");
            }
        } catch (Exception e) {
            webView.getEngine().loadContent("<html><body><h1>Chyba při načítání nápovědy.</h1></body></html>");
        }
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }
}
