package cz.upol.logicgo.controllers.miscControllers;

import cz.upol.logicgo.controllers.exportControllers.ExportMultipleGamesController;
import cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions;
import cz.upol.logicgo.misc.NodeSnapshotting;
import cz.upol.logicgo.misc.enums.PreviewType;
import cz.upol.logicgo.misc.windows.JobDoneDialog;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.dao.SettingDAO;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.entity.user.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterJob;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;

import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.changeCellSize;
import static cz.upol.logicgo.controllers.exportControllers.ExportMultipleGamesController.*;
import static cz.upol.logicgo.controllers.helpers.ElementsSetterHelpers.setItemsPreviewTypeChoiceBox;
import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.redrawCanvas;
import static cz.upol.logicgo.misc.enums.settings.UserSettings.SAVE_FILE_PATH_EXPORT;

public class ExportWindowController implements Initializable {

    public ChoiceBox<PreviewType> choiceExport;
    Stage stage;
    Game game;
    User user;
    CheckBox newPageEverySudoku;
    CheckBox orientation;

    Sudoku sudoku;

    SettingDAO settingDAO = new SettingDAO();
    @FXML
    Canvas canvas;
    @FXML
    Pane pane;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initialize(Game game, User user) {
        this.game = game;
        this.user = user;

        setItemsPreviewTypeChoiceBox(choiceExport);

        choiceExport.setVisible(false);
        choiceExport.setDisable(true);
        changePreview();
    }



    public void changePreview() {
        PreviewType previewType = PreviewType.UNSOLVED;
        switch (game) {
            case Sudoku sudoku -> {
                if (previewType == PreviewType.BOTH) {
                    // TODO finish
                } else {
                    sudoku = switch (previewType) {
                        case UNSOLVED ->
                                Sudoku.createSudokuForPrint(sudoku.getStartingBoard(), sudoku.getRegionLayout());
                        case CURRENT ->
                                Sudoku.createSudokuForPrint(sudoku.getBoard(), sudoku.getRegionLayout());
                        case SOLVED ->
                                Sudoku.createSudokuForPrint(sudoku.getSolutionBoard(), sudoku.getRegionLayout());
                        default -> throw new IllegalStateException("Unexpected value: " + previewType);
                    };
                    double width = canvas.getWidth() / sudoku.getType().getGridSize();
                    double height = canvas.getHeight() / sudoku.getType().getGridSize();

                    changeCellSize(sudoku.getBoard(), width, height);
                    redrawCanvas(canvas, sudoku);
                }
            }

            default -> throw new IllegalStateException("Unexpected value: " + game);
        }
    }




    public void printAction() {
        Path path = export(true);
        if (path != null) {
            openPrintWindow(path);
        }

    }

    public void exportAction() {
        Path path = export(false);
        if (path == null) return;
        JobDoneDialog.show(path.toString());
    }

    public Path export(boolean print) {
        String path;
        if (print) {
            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "temp-" + UUID.randomUUID() + ".pdf";
            File tempFile = new File(tempDir, fileName);
            path = tempFile.getAbsolutePath();
        } else {
            path = getPathOfFile(stage, user);
        }
        if (path.isEmpty()) return null;
        try {
            Sudoku sudokuExport = (Sudoku) game;
            Sudoku sudokuPrint = Sudoku.createSudokuForPrint(sudokuExport.getBoard(), sudokuExport.getRegionLayout());
            runExportToPdf(sudokuPrint, path, 5.0);
        } catch (Exception e) {
            return null;
        }
        return Path.of(path);
    }

    private String getPathOfFile(Stage stage, User user) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export jako obrázek / PDF");

        String path = user.getSettingsAsMap().get(SAVE_FILE_PATH_EXPORT);
        File file = new File(path);
        if (!(file.exists() && file.isDirectory())) {
            file = new File(".\\");
        }
        fileChooser.setInitialDirectory(file);


        fileChooser.setInitialFileName("sudoku");
        ArrayList<ExtensionFilter> filters = new ArrayList<>();

        ExtensionFilter pdfFilter = new ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
        filters.add(pdfFilter);

        fileChooser.getExtensionFilters().addAll(filters);

        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile == null) {
            return "";
        } else {
            var absolutePath = selectedFile.getAbsolutePath();
            Setting setting = user.getUserSettings().stream()
                    .filter(s -> s.getKey() == SAVE_FILE_PATH_EXPORT)
                    .findFirst().orElse(null);
            if (setting == null) return absolutePath;
            setting.setValue(Path.of(absolutePath).getParent().toString());
            settingDAO.update(setting);
            return absolutePath;
        }
    }

    public void closeExportWindow() {
        stage.close();
    }

    public void openPrintWindow(Path path) {
        try {
            PDDocument document = PDDocument.load(new File(path.toUri()));
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            if (job.printDialog()) {
                job.print();
            }
            document.close();
        } catch (Exception e) {
        } finally {
            try {
                Files.deleteIfExists(path);
            } catch (Exception _) {
            }

        }

    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
