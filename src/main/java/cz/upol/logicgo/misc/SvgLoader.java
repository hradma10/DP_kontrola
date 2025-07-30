package cz.upol.logicgo.misc;

import javafx.scene.control.Labeled;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SvgLoader {

    private static String getColorFromNodeFill(Labeled node){
        node.applyCss();
        Paint textFill = node.getTextFill();
        if (!(textFill instanceof Color)) {
            textFill = Color.WHITE;
        }

        return toHex((Color) textFill);
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static void setIcon(Labeled node, String resourcePath) {
        String colorHex = getColorFromNodeFill(node);
        try (InputStream is = SvgLoader.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                String svgContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                svgContent = svgContent.replace("currentColor", colorHex);

                SVGImage svg = SVGLoader.load(svgContent);
                node.setGraphic(svg);
            } else {
                System.err.println("SVG not found: " + resourcePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadSvgToPane(StackPane pane, String resourcePath) {
        String colorHex = "#000000";
        try (InputStream is = SvgLoader.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                String svgContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                svgContent = svgContent.replace("currentColor", colorHex);
                SVGImage svgImage = SVGLoader.load(svgContent);
                svgImage.setScaleX(3);
                svgImage.setScaleY(3);
                pane.getChildren().setAll(svgImage);
            } else {
                System.err.println("SVG not found: " + resourcePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
