package cz.upol.logicgo.misc;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions;
import cz.upol.logicgo.misc.enums.PreviewType;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.windows.JobDoneDialog;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.dao.GameDAO;
import cz.upol.logicgo.model.games.entity.setting.UserSetting;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jetbrains.annotations.TestOnly;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import javafx.application.Platform;

import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.changeCellSize;
import static cz.upol.logicgo.controllers.exportControllers.ExportMultipleGamesController.renderSudokuImage;

public class NodeSnapshotting {
    private static final String sharedDataPath = System.getenv("ProgramData");
    private static final String appFolderName = "LogicGo";
    private static final String appFolderThumbnailsPath = Paths.get(sharedDataPath, appFolderName, "thumbs").toString();

    private final GameDAO gameDAO;

    public NodeSnapshotting() {
        this(new GameDAO());
    }

    @TestOnly
    public NodeSnapshotting(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    private static BufferedImage createArgbImage(Canvas canvas, int width, int height) {
        var image = new WritableImage(width, height);
        var parameters = new SnapshotParameters();
        canvas.snapshot(parameters, image);
        return SwingFXUtils.fromFXImage(image, null);
    }

    private static BufferedImage createRgbImage(BufferedImage argbBufferedImage) {
        var rgbBufferedImage = new BufferedImage(argbBufferedImage.getWidth(), argbBufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        rgbBufferedImage.createGraphics().drawImage(argbBufferedImage, 0, 0, Color.WHITE, null);
        return rgbBufferedImage;
    }

    private static BufferedImage createBufferedImage(Canvas mainCanvas, boolean isPng, int width, int height) {
        var argbBufferedImage = createArgbImage(mainCanvas, width, height);
        return isPng ? argbBufferedImage : createRgbImage(argbBufferedImage);
    }

    public static BufferedImage createThumbnailFromCanvas(Canvas canvas, double scale) {
        if (canvas == null || scale <= 0 || scale > 1) {
            throw new IllegalArgumentException("Invalid canvas or scale.");
        }

        int scaledWidth  = (int) Math.ceil(canvas.getWidth() * scale);
        int scaledHeight = (int) Math.ceil(canvas.getHeight() * scale);

        WritableImage fxImage = new WritableImage(scaledWidth, scaledHeight);
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(Transform.scale(scale, scale));

        canvas.snapshot(params, fxImage);
        return SwingFXUtils.fromFXImage(fxImage, null);
    }

    /**
     * exportuje do pdf
     */
    public void export(String pathOfFile, PreviewType previewType, ArrayList<UserSetting> settings, Game game, boolean print) {
        try {
            if (pathOfFile.isEmpty()) return;
            int indexOfDot = pathOfFile.lastIndexOf('.');
            if (indexOfDot > 0) {
                switch (game.getGameType()) {
                    case SUDOKU -> {
                        double scale = 5.0;
                        Canvas canvas = new Canvas(scale * 800, scale * 800);
                        canvas.getGraphicsContext2D().setImageSmoothing(false);
                        canvas.getGraphicsContext2D().scale(scale, scale);

                        double width = 50;
                        double height = 50;

                        changeCellSize(((Sudoku)game).getBoard(), width, height);

                        try (PDDocument document = new PDDocument()) {
                            BufferedImage img = this.exportHighQuality(game, canvas);

                            PDImageXObject pdImage = LosslessFactory.createFromImage(document, img);

                            PDPage page = new PDPage(PDRectangle.A4);
                            document.addPage(page);

                            PDRectangle mediaBox = page.getMediaBox();
                            float pageWidth = mediaBox.getWidth();
                            float pageHeight = mediaBox.getHeight();

                            float imageWidth = img.getWidth();
                            float imageHeight = img.getHeight();

                            float x = (pageWidth - imageWidth) / 2;
                            float y = pageHeight - imageHeight - 50;

                            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                                contentStream.drawImage(pdImage, x, y, imageWidth, imageHeight);
                            }

                            img.flush();
                            document.save(new File(pathOfFile));

                            if (!print) JobDoneDialog.show(pathOfFile);
                        }
                    }
                    case null, default -> {
                    }
                    // TODO implementovat další typy
                }

            }
        } catch (IOException _) {
        }
    }

    public BufferedImage exportHighQuality(Game game, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (Platform.isFxApplicationThread()){
            ((Sudoku) game).draw(canvas);
        }else {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                ((Sudoku) game).draw(canvas);
                latch.countDown();
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // TODO handle
            }
        }

        WritableImage image = new WritableImage(
                (int) canvas.getWidth(),
                (int) canvas.getHeight()
        );

        if (Platform.isFxApplicationThread()){
            canvas.snapshot(null, image);

            try {
                return SwingFXUtils.fromFXImage(image, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            FutureTask<WritableImage> task = new FutureTask<>(() -> canvas.snapshot(null, image));
            Platform.runLater(task);

            try {
                return SwingFXUtils.fromFXImage(task.get(), null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


    }

    public javafx.scene.image.Image loadThumbnail(String thumbnailName){
        try {
            if (thumbnailName == null || thumbnailName.isEmpty()) return null;
            String filePath = Paths.get(appFolderThumbnailsPath, thumbnailName).toString();
            Path path = Paths.get(filePath);
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                filePath = "default"; // TODO vytvořit
            }
            String uri = Paths.get(filePath).toUri().toString();
            return new javafx.scene.image.Image(uri);
        }catch (Exception e){
            return null;
        }


    }

    public static void removeThumbnail(String thumbnailName){
        try {
            if (thumbnailName == null || thumbnailName.isEmpty()) return;
            Files.deleteIfExists(Paths.get(appFolderThumbnailsPath, thumbnailName));
        }catch (Exception _){

        }

    }


    public void makeThumbnail(Game game, Node node) {
        String newFilePath = null;
        String newThumbnailName = null;
        try {
            Files.createDirectories(Path.of(appFolderThumbnailsPath));
            newThumbnailName = String.format("%s_%s.png", UUID.randomUUID(), System.currentTimeMillis());
            newFilePath = Paths.get(appFolderThumbnailsPath, newThumbnailName).toString();

            Canvas canvas = new Canvas(((Canvas) node).getWidth(), ((Canvas) node).getHeight());

            Sudoku sudoku = Sudoku.createSudokuForPrint(((Sudoku)(game)).getBoard(), ((Sudoku)(game)).getRegionLayout());

            sudoku.draw(canvas);

            BufferedImage bufferedImage = createThumbnailFromCanvas(canvas, 0.5);

            ImageIO.write(bufferedImage, "png", new File(newFilePath));

            if (game.getThumbnailName() != null) {
                try {
                    Files.deleteIfExists(Paths.get(appFolderThumbnailsPath, game.getThumbnailName()));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            game.setThumbnailName(newThumbnailName);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            if (newFilePath != null) {
                try {
                    Files.deleteIfExists(Paths.get(appFolderThumbnailsPath, newThumbnailName));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    public Document convertToPdf(String path, ArrayList<BufferedImage> bufferedImages) throws IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        for (int i = 0; i < bufferedImages.size(); i++) {
            BufferedImage bufferedImage = bufferedImages.get(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();


            Image image = Image.getInstance(imageBytes);
            image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            image.setAlignment(Image.ALIGN_TOP);

            document.add(image);
            document.newPage();
            System.out.println(i);
        }

        document.close();
        return document;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }
}
