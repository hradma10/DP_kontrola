package cz.upol.logicgo.model.games.drawable.elements.sudoku;

import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.model.games.DrawableElement;
import cz.upol.logicgo.model.games.drawable.bounds.IRectangleBounds;
import cz.upol.logicgo.model.games.drawable.bounds.Point;
import cz.upol.logicgo.model.games.drawable.bounds.RectangleBounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

import static java.awt.Color.BLUE;
import static javafx.scene.paint.Color.CYAN;

public class SudokuCell extends DrawableElement implements IRectangleBounds, Serializable {
    private final int row;
    private final int col;
    private int value;
    private boolean staticCell;
    private List<Integer> candidates = new ArrayList<>();
    private double width;
    private double height;
    private boolean changeable = true;
    private boolean hints = false;
    private boolean forPrint = false;
    private transient Color backgroundColor = Color.WHITE;
    private SudokuType sudokuType;

    static HashMap<Integer, String> valueToString;

    static {
        valueToString = new HashMap<>();

        for (int i = 0; i <= 9; i++) {
            valueToString.put(i, String.valueOf(i));
        }

        valueToString.put(10, "A");
        valueToString.put(11, "B");
        valueToString.put(12, "C");
        valueToString.put(13, "D");
        valueToString.put(14, "E");
        valueToString.put(15, "F");
        valueToString.put(16, "G");
    }

    public SudokuCell(int row, int col, int value, boolean staticCell) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.staticCell = staticCell;
        var x = row * 50;
        var y = col * 50;
        var bounds = new RectangleBounds(x, y, 50, 50);
        super(bounds, true);
    }

    public double getWidth(){
        return getBounds().getWidth();
    }

    public double getHeight(){
        return getBounds().getHeight();
    }

    public void changeCellSize(int row, int col, double width, double height){
        this.width = width;
        this.height = height;
        this.getBounds().recalculate(row, col, width, height);
    }

    public SudokuCell(int row, int col, int value) {
        this(row, col, value, false);
    }

    public SudokuCell(int row, int col, boolean staticCell) {
        this(row, col, 0, staticCell);
    }

    public SudokuCell(int row, int col) {
        this(row, col, 0);
    }

    public SudokuCell(SudokuCell cell) {
        this(cell.row, cell.col, cell.value);
    }

    @Override
    public void draw(Canvas canvas) {
        var gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(2);

        double width = this.getBounds().getWidth();
        double height = this.getBounds().getHeight();
        double x = this.getBounds().getTopLeftX();
        double y = this.getBounds().getTopLeftY();

        if (backgroundColor != Color.WHITE) {
            Color semiTransparent = new Color(
                    backgroundColor.getRed(),
                    backgroundColor.getGreen(),
                    backgroundColor.getBlue(),
                    0.5);

            gc.setFill(semiTransparent);
            gc.fillRect(x, y, width, height);
        }

        // zvýraznění sekundárního výběru
        if (!isDrawToPrimary()) {
            Color blue = Color.BLUE;
            Color semiTransparentBlue = new Color(
                    blue.getRed(),
                    blue.getGreen(),
                    blue.getBlue(),
                    0.2);
            gc.setStroke(semiTransparentBlue);
            gc.setFill(semiTransparentBlue);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
        }

        // pokud je buňka prázdná a nemá kandidáty
        if (value == 0 && !hints) return;

        if (hints && value == 0) {
            int maxCandidates = this.getSudokuType().getGridSize();
            int gridSize = (int) Math.ceil(Math.sqrt(maxCandidates));

            double subCellWidth = width / gridSize;
            double subCellHeight = height / gridSize;

            double fontSize = Math.min(subCellWidth, subCellHeight) * 0.6;
            gc.setFont(new Font("Arial", fontSize));

            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(Color.GRAY);

            for (int candidate = 1; candidate <= maxCandidates; candidate++) {
                if (!candidates.contains(candidate)) continue;

                int row = (candidate - 1) / gridSize;
                int col = (candidate - 1) % gridSize;

                double tx = x + col * subCellWidth + subCellWidth / 2;
                double ty = y + row * subCellHeight + subCellHeight / 2;

                gc.fillText(valueToString.get(candidate), tx, ty);
            }
        } else {
            gc.setFont(new Font("Arial", Math.min(width, height) * 0.6));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);

            if (forPrint) {
                gc.setFill(Color.BLACK);
            } else {
                gc.setFill(isChangeable() ? Color.DARKRED : Color.BLACK);

            }
            gc.fillText(valueToString.get(value), x + width / 2.0, y + height / 2.0);
        }
    }

    @Override
    public void setDrawToPrimary(boolean drawToPrimary) {
        super.setDrawToPrimary(drawToPrimary);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SudokuCell cell)) return false;
        if (!super.equals(o)) return false;
        return getRow() == cell.getRow() && getCol() == cell.getCol() && getValue() == cell.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getCol(), getValue());
    }

    @Override
    public String toString() {
        return "SudokuCell{" +
                "row=" + row +
                ", col=" + col +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean pointInside(Point point) {
        return pointInside(point.getX(), point.getY());
    }

    @Override
    public boolean pointInside(double x, double y) {
        return this.getBounds().pointInBoundsOfElement(x, y);
    }

    @Override
    public RectangleBounds getBounds() {
        return (RectangleBounds) super.getBounds();
    }

    public boolean isStaticCell() {
        return staticCell;
    }

    public void setStaticCell(boolean staticCell) {
        this.staticCell = staticCell;
    }

    public void setCandidates(List<Integer> candidates) {
        this.candidates.clear();
        this.candidates.addAll(candidates);
    }

    public List<Integer> getCandidates() {
        return candidates;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public Point getCenter(){
        double x = this.getBounds().getTopLeftX();
        double y = this.getBounds().getTopLeftY();
        return new Point(x + getBounds().getWidth(), y +  getBounds().getHeight());
    }

    public Point getTopLeft(){
        double x = this.getBounds().getTopLeftX();
        double y = this.getBounds().getTopLeftY();
        return new Point(x, y);
    }

    public void setHints(boolean hints) {
        this.hints = hints;
    }

    public boolean isHints() {
        return hints;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isForPrint() {
        return forPrint;
    }

    public void setForPrint(boolean forPrint) {
        this.forPrint = forPrint;
    }

    public static SudokuCell[][] deepCopyBoard(SudokuCell[][] original) {
        if (original == null) {
            return null;
        }

        int rows = original.length;
        int cols = original[0].length;
        SudokuCell[][] copy = new SudokuCell[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                SudokuCell cell = original[r][c];
                if (cell != null) {
                    SudokuCell newCell = deepCopySudokuCell(cell);

                    copy[r][c] = newCell;
                } else {
                    copy[r][c] = null;
                }
            }
        }

        return copy;
    }

    @NotNull
    private static SudokuCell deepCopySudokuCell(SudokuCell cell) {
        SudokuCell newCell = new SudokuCell(cell.getRow(), cell.getCol(), cell.getValue(), cell.isStaticCell());

        newCell.setCandidates(new ArrayList<>()); // není potřeba, počitám stejně před překreslením
        newCell.setChangeable(cell.isChangeable());
        newCell.setHints(cell.isHints());
        newCell.setForPrint(cell.isForPrint());
        newCell.setBackgroundColor(cell.getBackgroundColor());
        newCell.setSudokuType(cell.getSudokuType());
        newCell.changeCellSize(cell.getRow(), cell.getCol(), cell.getWidth(), cell.getHeight());
        return newCell;
    }

    public static void copyBoardProperties(SudokuCell[][] source, SudokuCell[][] target) {
        if (source == null || target == null) return;

        int rows = source.length;
        int cols = source[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                SudokuCell sourceCell = source[r][c];
                SudokuCell targetCell = target[r][c];

                if (sourceCell != null && targetCell != null) {
                    targetCell.setStaticCell(sourceCell.isStaticCell());

                    targetCell.setChangeable(sourceCell.isChangeable());
                    targetCell.setHints(sourceCell.isHints());
                    targetCell.setForPrint(sourceCell.isForPrint());
                    targetCell.setSudokuType(sourceCell.getSudokuType());

                    targetCell.changeCellSize(sourceCell.getRow(), sourceCell.getCol(), sourceCell.getWidth(), sourceCell.getHeight());
                }
            }
        }
    }

    public SudokuType getSudokuType() {
        return sudokuType;
    }

    public void setSudokuType(SudokuType sudokuType) {
        this.sudokuType = sudokuType;
    }
}
