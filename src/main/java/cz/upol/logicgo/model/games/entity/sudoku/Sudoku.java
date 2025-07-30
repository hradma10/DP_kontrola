package cz.upol.logicgo.model.games.entity.sudoku;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.misc.export.DTOs.games.SudokuDTO;
import cz.upol.logicgo.model.games.Drawable;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.util.converters.SudokuCellConverter;
import cz.upol.logicgo.util.converters.SudokuRegionConverter;
import jakarta.persistence.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static cz.upol.logicgo.algorithms.sudoku.SudokuUtils.*;
import static cz.upol.logicgo.controllers.screenControllers.RedrawCanvasFunctions.strokeBoard;


@Entity
@NamedQueries({
        @NamedQuery(
                name = "Sudoku.updateStacks",
                query = "UPDATE Sudoku s SET s.undoStack =: undoStack, s.redoStack =: redoStack, s.lastPlayed =: lastPlayed WHERE s.id = id"
        ),
        @NamedQuery(
                name = "Sudoku.getAllUserSudokuById",
                query = "SELECT s FROM Sudoku s WHERE s.player.id = :id"
        ),
        @NamedQuery(
                name = "Sudoku.getAllUserSudokuByUsername",
                query = "SELECT s FROM Sudoku s WHERE s.player.username = :username"
        ),
        @NamedQuery(
                name = "Sudoku.getLastNumberOfGames",
                query = "SELECT s FROM Sudoku s WHERE s.player.id = :id ORDER BY lastPlayed DESC"
        ),
        @NamedQuery(
                name = "Sudoku.findAll",
                query = "SELECT s FROM Sudoku s"
        ),
})
@Table(name = "sudoku")
public class Sudoku extends Game implements Drawable {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "sudoku_type")
    private SudokuType type;

    @Column(nullable = false, name = "region_layout")
    @Convert(converter = SudokuRegionConverter.class)
    private RegionLayout regionLayout;

    @Column(nullable = false, name = "current_board")
    @Convert(converter = SudokuCellConverter.class)
    private SudokuCell[][] board;

    @Column(nullable = false, name = "solution_board")
    @Convert(converter = SudokuCellConverter.class)
    private SudokuCell[][] solutionBoard;

    @Column(nullable = false, name = "starting_board")
    @Convert(converter = SudokuCellConverter.class)
    private SudokuCell[][] startingBoard;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "undo_stack")
    private byte[] undoStack;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "redo_stack")
    private byte[] redoStack;



    public Sudoku(SudokuType type, RegionLayout regionLayout, Difficulty difficulty, User player) {
        this.board = createEmptyCells(type);
        this.regionLayout = regionLayout;
        int height = type.getGridSize();
        int width = type.getGridSize();
        super(height, width, TypeGame.SUDOKU, difficulty, player);
        this.type = type;
    }

    public Sudoku(SudokuType type,RegionLayout regionLayout, Difficulty difficulty, long seed, User player) {
        this.board = createEmptyCells(type);
        this.regionLayout = regionLayout;
        int height = type.getGridSize();
        int width = type.getGridSize();
        super(height, width, seed, TypeGame.SUDOKU, difficulty, player);
        this.type = type;
    }

    public static Sudoku createSudokuForPrint(SudokuCell[][] sudokuCells, RegionLayout regionLayout){
        var sudoku = new Sudoku();
        sudoku.setBoard(SudokuCell.deepCopyBoard(sudokuCells));
        sudoku.setType(SudokuType.getTypeByGridSize(sudokuCells.length));
        sudoku.setRegionLayout(regionLayout);
        Arrays.stream(flattenBoard(sudoku.getBoard())).forEach(cell -> {
            cell.setForPrint(true);
            cell.setBackgroundColor(Color.WHITE);
            cell.setDrawToPrimary(true);
        });
        return sudoku;
    }

    public Sudoku(Boards boards, RegionLayout regionLayout, Difficulty difficulty, User player) {
        this.board = boards.board();
        this.regionLayout = regionLayout;
        this.startingBoard = boards.startingBoard();
        this.solutionBoard = boards.solutionBoard();
        int height = boards.board().length;
        int width = boards.board().length;
        super(height, width, TypeGame.SUDOKU, difficulty, player);
        this.type = SudokuType.getTypeByGridSize(height);
    }

    public Sudoku(Boards boards, RegionLayout regionLayout, Difficulty difficulty, long seed, User player) {
        this.board = boards.board();
        this.regionLayout = regionLayout;
        this.startingBoard = boards.startingBoard();
        this.solutionBoard = boards.solutionBoard();
        int height = boards.board().length;
        int width = boards.board().length;
        super(height, width, seed, TypeGame.SUDOKU, difficulty, player);
        this.type = SudokuType.getTypeByGridSize(height);
    }

    public Sudoku(Sudoku sudoku) {
        super(sudoku.getHeight(), sudoku.getWidth(), sudoku.getSeed(), TypeGame.SUDOKU, sudoku.getDifficulty(), sudoku.getPlayer());
        this.type = sudoku.type;
        this.regionLayout = sudoku.regionLayout;
        this.board = getBoardCopy(sudoku.getBoard());
        this.solutionBoard = getBoardCopy(sudoku.getSolutionBoard());
        this.startingBoard = getBoardCopy(sudoku.getStartingBoard());
        this.undoStack = sudoku.getUndoStack();
        this.redoStack = sudoku.getRedoStack();
        this.undoStack = sudoku.undoStack != null ? sudoku.undoStack.clone() : null;
        this.redoStack = sudoku.redoStack != null ? sudoku.redoStack.clone() : null;
    }

    public Sudoku(SudokuDTO sudokuDTO, User player, Boards boards) {
        super(sudokuDTO, player);
        this.type = sudokuDTO.getSudokuType();
        this.board = boards.board(); // TODO add region
        this.solutionBoard = boards.solutionBoard();
        this.startingBoard = boards.startingBoard();
        this.undoStack = sudokuDTO.getUndoStack();
        this.redoStack = sudokuDTO.getRedoStack();
    }

    public Sudoku() {

    }

    @Override
    public GameType getTypeOfGame() {
        return this.getType();
    }

    @PrePersist
    public void prePersistSudoku() {
        super.prePersist();
        if (undoStack == null) {
            undoStack = new byte[0];
        }
        if (redoStack == null) {
            redoStack = new byte[0];
        }
    }

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        boolean forPrint = board[0][0].isForPrint();

        double width = board[0][0].getWidth();
        double height = board[0][0].getHeight();
        int size = type.getGridSize();

        for (SudokuCell[] row : board) {
            for (SudokuCell cell : row) {
                cell.draw(canvas);
            }
        }

        strokeBoard(regionLayout, width, height, gc);

        if (!forPrint) {
            double totalWidth = size * width;
            double totalHeight = size * height;

            gc.setLineWidth(3);
            gc.setStroke(Color.BLACK);

            // horní a dolní okraj
            gc.strokeLine(0, 0, totalWidth, 0);
            gc.strokeLine(0, totalHeight, totalWidth, totalHeight);

            // levý a pravý okraj
            gc.strokeLine(0, 0, 0, totalHeight);
            gc.strokeLine(totalWidth, 0, totalWidth, totalHeight);
        }
    }

    @Override
    public boolean isDrawToPrimary() {
        return true;
    }

    public SudokuCell getSudokuCell(int row, int col) {
        return board[row][col];
    }

    public int setNumber(int row, int col, int num) {
        int oldNum = board[row][col].getValue();
        board[row][col].setValue(num);
        return oldNum;
    }

    public int setNumberToZero(int row, int col) {
        int val = board[row][col].getValue();
        board[row][col].setValue(0);
        return val;
    }

    public int getNumber(int row, int col) {
        return board[row][col].getValue();
    }

    public SudokuType getType() {
        return type;
    }

    public void setType(SudokuType type) {
        this.type = type;
    }

    public boolean isZero(int row, int col) {
        return board[row][col].getValue() == 0;
    }

    public SudokuCell[][] getBoard() {
        return board;
    }

    public void setBoard(SudokuCell[][] board) {
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sudoku other)) return false;
        return areBoardsEqual(this.getStartingBoard(), other.getStartingBoard()) && areBoardsEqual(this.getSolutionBoard(), other.getSolutionBoard()) && areBoardsEqual(this.getBoard(), other.getBoard());
    }

    @Override
    public int hashCode() {
        ArrayList<Integer> hashes = new ArrayList<>();

        for (SudokuCell[] cells : solutionBoard) {
            for (SudokuCell cell : cells) {
                hashes.add(Objects.hash(cell));
            }
        }

        for (SudokuCell[] sudokuCells : startingBoard) {
            for (SudokuCell sudokuCell : sudokuCells) {
                hashes.add(Objects.hash(sudokuCell));
            }
        }

        for (SudokuCell[] sudokuCells : board) {
            for (SudokuCell sudokuCell : sudokuCells) {
                hashes.add(Objects.hash(sudokuCell));
            }
        }

        return Objects.hash(hashes.toArray());
    }

    public SudokuCell[][] getSolutionBoard() {
        return solutionBoard;
    }

    public void setSolutionBoard(SudokuCell[][] solutionBoard) {
        this.solutionBoard = solutionBoard;
    }

    public SudokuCell[][] getStartingBoard() {
        return startingBoard;
    }

    public void setStartingBoard(SudokuCell[][] startingBoard) {
        this.startingBoard = startingBoard;
    }

    public byte[] getUndoStack() {
        return undoStack;
    }

    public void setUndoStack(byte[] undoStack) {
        this.undoStack = undoStack;
    }

    public byte[] getRedoStack() {
        return redoStack;
    }

    public void setRedoStack(byte[] redoStack) {
        this.redoStack = redoStack;
    }

    public RegionLayout getRegionLayout() {
        return regionLayout;
    }

    public void setRegionLayout(RegionLayout regionLayout) {
        this.regionLayout = regionLayout;
    }

    public record Boards(SudokuCell[][] board, SudokuCell[][] solutionBoard, SudokuCell[][] startingBoard) {
    }
}
