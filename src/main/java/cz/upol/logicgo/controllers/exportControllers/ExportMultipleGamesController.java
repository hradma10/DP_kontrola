package cz.upol.logicgo.controllers.exportControllers;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.controllers.screenControllers.MainScreenController;
import cz.upol.logicgo.export.ExportedGamesEntry;
import cz.upol.logicgo.export.ExportedHistoryCell;
import cz.upol.logicgo.misc.NodeSnapshotting;
import cz.upol.logicgo.misc.TabState;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.PreviewType;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.windows.JobDoneDialog;
import cz.upol.logicgo.model.games.dao.ExportedGamesDAO;
import cz.upol.logicgo.model.games.dao.SettingDAO;
import cz.upol.logicgo.model.games.entity.export.ExportedGames;
import cz.upol.logicgo.model.games.entity.setting.Setting;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.util.generate.SeedFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.generateSudokuTimed;
import static cz.upol.logicgo.controllers.helpers.ElementsSetterHelpers.*;
import static cz.upol.logicgo.misc.Validators.isCountValid;
import static cz.upol.logicgo.misc.enums.settings.UserSettings.SAVE_FILE_PATH_EXPORT;

public class ExportMultipleGamesController {
    private static final int ITEMS_PER_PAGE = 5;
    private static final int MAX_GENERATED = 1000;
    private final int[] progressCount = new int[2];
    public ChoiceBox<TypeGame> gameChoiceBox;
    public ChoiceBox<GameType> gameTypeChoiceBox;
    public ChoiceBox<Difficulty> difficultyChoiceBox;
    public ChoiceBox<RegionLayout> regionLayoutChoiceBox;
    public TextField countGames;
    public ProgressBar progressBar;
    public Pagination pagination;
    public ChoiceBox<PreviewType> previewTypeChoiceBox;
    public Label progressBarLabel;
    public Button okButton;
    public Canvas canvas;
    MainScreenController mainScreenController;
    ProgressTimer progressTimer = null;
    TabState tabState;
    @FXML
    private ListView<ExportedGamesEntry> exportedListView;
    private List<ExportedGamesEntry> allEntries;
    private User user;
    private ExportedGamesDAO exportedGamesDAO = new ExportedGamesDAO();
    private SettingDAO settingDAO = new SettingDAO();

    public static void runExportToPdf(Sudoku sudoku, String path, double scale) throws IOException {
        try (PDDocument document = new PDDocument()) {

            if (sudoku == null) return;

            BufferedImage img = renderSudokuImage(sudoku, scale);

            PDImageXObject pdImage = LosslessFactory.createFromImage(document, img);
            PDPage page = new PDPage(PDRectangle.A4);

            double imageWidth = img.getWidth();
            double imageHeight = img.getHeight();

            double pageWidth = page.getMediaBox().getWidth();
            double pageHeight = page.getMediaBox().getHeight();

            double maxWidth = pageWidth - 40;
            double maxHeight = pageHeight - 100;

            double scaleI = Math.min(1f, Math.min(maxWidth / imageWidth, maxHeight / imageHeight));
            double scaledWidth = imageWidth * scaleI;
            double scaledHeight = imageHeight * scaleI;

            double offset = switch (sudoku.getType()) {
                case FOUR -> 190;
                case FIVE -> 180;
                case SIX -> 170;
                case SEVEN -> 155;
                case EIGHT -> 140;
                case NINE -> 120;
                case TEN -> 105;
                case TWELVE -> 70;
                case SIXTEEN -> 5;
            };


           double x = ((pageWidth - scaledWidth) / 2) + offset;
            double y = pageHeight - scaledHeight - 50;

            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, (float) x, (float) y, (float) scaledWidth, (float) scaledHeight);
                img.flush();
            }

            document.save(new File(path));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static BufferedImage renderSudokuImage(Sudoku sudoku, double scale) {
        final Canvas canvas = new Canvas(scale * 800, scale * 800);
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        final BufferedImage[] result = new BufferedImage[1];

        final Runnable renderTask = () -> {
            try {
                gc.setImageSmoothing(false);
                gc.scale(scale, scale);

                NodeSnapshotting snap = new NodeSnapshotting();
                snap.exportHighQuality(sudoku, canvas);

                WritableImage fxImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, fxImage);

                result[0] = SwingFXUtils.fromFXImage(fxImage, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (Platform.isFxApplicationThread()) {
            renderTask.run();
        } else {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    renderTask.run();
                } finally {
                    latch.countDown();
                }
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return result[0];
    }


    public void initialize(User user, MainScreenController mainScreenController, TabState tabState) {
        this.user = user;
        this.tabState = tabState;
        this.mainScreenController = mainScreenController;

        var exportedGames = exportedGamesDAO.findByUser(user);
        this.allEntries = convertToEntries(exportedGames);


        countGames.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isCountValid(newValue, 0, MAX_GENERATED)) {
                if (!countGames.getStyleClass().contains("error")) {
                    countGames.getStyleClass().add("error");
                }
            } else {
                countGames.getStyleClass().removeAll(Collections.singleton("error"));
            }
        });

        exportedListView.setCellFactory(list -> new ExportedHistoryCell(user, mainScreenController, this));

        setupPagination();

        setItemsPreviewTypeChoiceBox(previewTypeChoiceBox);

        setItemsDifficultyChoiceBox(difficultyChoiceBox);

        setItemsRegionTypeChoiceBox(regionLayoutChoiceBox, SudokuType.NINE, canvas);

        ObservableList<TypeGame> typeGameList = FXCollections.observableArrayList(TypeGame.values());

        gameChoiceBox.setItems(typeGameList);

        gameChoiceBox.getSelectionModel().select(TypeGame.SUDOKU);

        gameChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(TypeGame pt) {
                if (pt != null) {
                    return pt.getDescription();
                }

                return "žádné";
            }

            @Override
            public TypeGame fromString(String string) {
                return TypeGame.getInstanceByDescription(string);
            }
        });

        gameChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setGameType(newValue);
            onRegionTypeChange(canvas, (SudokuType) gameTypeChoiceBox.getSelectionModel().getSelectedItem(), regionLayoutChoiceBox);
        });

        gameTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setSudokuTypeRegions((SudokuType) gameTypeChoiceBox.getSelectionModel().getSelectedItem());
            onRegionTypeChange(canvas, (SudokuType) gameTypeChoiceBox.getSelectionModel().getSelectedItem(), regionLayoutChoiceBox);
        });

        setGameType(TypeGame.SUDOKU);

    }

    private void setSudokuTypeRegions(SudokuType gameType) {
        setItemsRegionTypeChoiceBox(regionLayoutChoiceBox, gameType, canvas);

    }

    private void setGameType(TypeGame typeGame) {
        var types = switch (typeGame) {
            case SUDOKU -> SudokuType.values();
            case MAZE -> null;
            case BRIDGE -> null;
        };

        var defaultType = switch (typeGame) {
            case SUDOKU -> SudokuType.NINE;
            case MAZE -> null;
            case BRIDGE -> null;
        };

        ObservableList<GameType> gameTypes = FXCollections.observableArrayList(types);

        gameTypeChoiceBox.setItems(gameTypes);

        gameTypeChoiceBox.getSelectionModel().select(defaultType);

        gameTypeChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(GameType pt) {
                if (pt != null) {
                    return pt.getDescription();
                }

                return "žádné";
            }

            @Override
            public GameType fromString(String string) {
                GameType gameType = SudokuType.getInstanceByDescription(string);
                if (gameType != null) {
                    return gameType;
                }

                gameType = SudokuType.getInstanceByDescription(string); // TODO maze
                if (gameType != null) {
                    return gameType;
                }

                gameType = SudokuType.getInstanceByDescription(string); // TODO bridge
                if (gameType != null) {
                    return gameType;
                }

                return null;
            }
        });
    }

    public void generateGames() {
        int count = Integer.parseInt(countGames.getText());
        Difficulty diff = difficultyChoiceBox.getValue();
        TypeGame type = gameChoiceBox.getValue();
        GameType gameType = gameTypeChoiceBox.getValue();
        RegionLayout regionLayout = regionLayoutChoiceBox.getValue();

        runGeneration(count, (SudokuType) gameType, regionLayout, diff, type);
    }

    void runGeneration(int count, SudokuType gameType, RegionLayout regionLayout, Difficulty diff, TypeGame type) {
        runGeneration(count, gameType, regionLayout, diff, type, null);
    }

    void runGeneration(int count, SudokuType gameType, RegionLayout regionLayout, Difficulty diff, TypeGame type, List<Long> seeds) {
        String path = getPathOfFile(mainScreenController.getStage(), user);
        tabState.setChangePending();
        progressCount[0] = 0;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean alreadyGeneratedSeeds = (seeds != null && !seeds.isEmpty());
                if (path == null || path.isEmpty()) return null;

                List<Long> newSeeds = Collections.synchronizedList(new ArrayList<>());
                if (alreadyGeneratedSeeds) {
                    newSeeds.addAll(seeds);
                }

                progressCount[1] = count;
                progressTimer = new ProgressTimer(progressBarLabel, progressBar, progressCount);
                progressTimer.start();

                double scale = 5.0;

                ReentrantLock documentLock = new ReentrantLock();
                ReentrantLock seedLock = new ReentrantLock();

                try (PDDocument document = new PDDocument()) {
                    ExecutorService executor = Executors.newFixedThreadPool(Math.min(count, Runtime.getRuntime().availableProcessors()));

                    List<Future<Void>> futures = new ArrayList<>();

                    for (int i = 0; i < count; i++) {
                        final int index = i;

                        futures.add(executor.submit(() -> {
                            long seed;
                            if (alreadyGeneratedSeeds) {
                                seed = seeds.get(index);
                            } else {
                                seed = SeedFactory.generateSeed();
                                seedLock.lock();
                                try {
                                    newSeeds.add(seed);
                                } finally {
                                    seedLock.unlock();
                                }
                            }

                            Sudoku sudoku = generateSudokuTimed(seed, user, gameType, regionLayout, diff, 5);
                            if (sudoku == null) return null;
                            sudoku = Sudoku.createSudokuForPrint(sudoku.getBoard(), regionLayout);

                            BufferedImage img = renderSudokuImage(sudoku, scale);

                            PDImageXObject pdImage = LosslessFactory.createFromImage(document, img);
                            PDPage page = new PDPage(PDRectangle.A4);

                            double imageWidth = img.getWidth();
                            double imageHeight = img.getHeight();

                            double pageWidth = page.getMediaBox().getWidth();
                            double pageHeight = page.getMediaBox().getHeight();

                            double maxWidth = pageWidth - 40;
                            double maxHeight = pageHeight - 100;

                            double scaleI = Math.min(1f, Math.min(maxWidth / imageWidth, maxHeight / imageHeight));
                            double scaledWidth = imageWidth * scaleI;
                            double scaledHeight = imageHeight * scaleI;

                            double offset = switch (sudoku.getType()) {
                                case FOUR -> 190;
                                case FIVE -> 180;
                                case SIX -> 170;
                                case SEVEN -> 155;
                                case EIGHT -> 140;
                                case NINE -> 120;
                                case TEN -> 105;
                                case TWELVE -> 70;
                                case SIXTEEN -> 5;
                            };


                            double x = ((pageWidth - scaledWidth) / 2) + offset;
                            double y = pageHeight - scaledHeight - 50;

                            documentLock.lock();
                            try {
                                document.addPage(page);
                                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                                    contentStream.drawImage(pdImage, (float) x, (float) y, (float) scaledWidth, (float) scaledHeight);
                                }
                            } finally {
                                documentLock.unlock();
                            }

                            img.flush();

                            synchronized (progressCount) {
                                progressCount[0]++;
                                updateProgress(progressCount[0], count);
                            }

                            return null;
                        }));
                    }


                    for (Future<Void> future : futures) {
                        future.get();
                    }

                    executor.shutdown();
                    document.save(new File(path));
                }

                if (!alreadyGeneratedSeeds) {
                    exportedGamesDAO.save(new ExportedGames(newSeeds, diff, type, regionLayout, gameType, user));
                }

                Platform.runLater(() -> refresh());

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> JobDoneDialog.show(path));
            }

            @Override
            protected void done() {
                Platform.runLater(() -> {
                    if (progressTimer != null) {
                        progressTimer.stop();
                    }
                });
                tabState.markSavingFinished();
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
        });

        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            task.getException().printStackTrace();
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
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
        ArrayList<FileChooser.ExtensionFilter> filters = new ArrayList<>();

        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
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


    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) allEntries.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));

        pagination.setPageFactory(pageIndex -> {
            updatePage(pageIndex);
            return new Pane();
        });
    }


    private void updatePage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, allEntries.size());
        List<ExportedGamesEntry> subList = allEntries.subList(fromIndex, toIndex);
        exportedListView.getItems().setAll(subList);
    }

    public List<ExportedGamesEntry> convertToEntries(List<ExportedGames> exportedGames) {
        List<ExportedGamesEntry> entries = new ArrayList<>();
        exportedGames.forEach(exportedGame -> {
            ExportedGamesEntry entry = new ExportedGamesEntry(exportedGame.getId(), exportedGame.getSudokuType(), exportedGame.getTypeGame(), exportedGame.getRegionLayout(), exportedGame.getDifficulty(), exportedGame.getGenerationTime(), (short) exportedGame.getSeeds().size());
            entries.add(entry);
        });
        return entries;
    }

    public void refresh() {
        List<ExportedGames> games = exportedGamesDAO.findByUser(user);
        this.allEntries = convertToEntries(games);
        setupPagination();
    }
}
