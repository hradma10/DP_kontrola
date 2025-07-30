package cz.upol.logicgo.model.games.entity.mazes;

import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class KruskalMaze extends Maze {
    static final int START = 16;  // reprezentuje start
    static final int END = 32;    // reprezentuje konec
    //zdroj - http://weblog.jamisbuck.org/2011/1/3/maze-generation-kruskal-s-algorithm
    private static final byte N = 1, S = 2, E = 4, W = 8; // používáno na reprezentaci
    byte[][] grid;
    Tree[][] sets;

    public KruskalMaze(int height, int width, Difficulty difficulty, User player) {
        super(height, width, TypeGame.MAZE, difficulty, player);
        grid = new byte[height][width];
        sets = new Tree[height][width];
        initializeTrees();
        var edges = createListOfEdges();
        var random = new Random(this.getSeed());
        shuffleEdges(edges, random);
        kruskalAlgorithm(edges);
        createStartAndEnd(random);
    }

    public KruskalMaze(int height, int width, long seed, Difficulty difficulty, User player) {
        super(height, width, seed, TypeGame.MAZE, difficulty, player);
        grid = new byte[height][width];
        sets = new Tree[height][width];
        initializeTrees();
        var edges = createListOfEdges();
        var random = new Random(seed);
        shuffleEdges(edges, random);
        kruskalAlgorithm(edges);
        createStartAndEnd(random);
    }

    public byte[][] getGrid() {
        return grid;
    }

    public Tree[][] getSets() {
        return sets;
    }

    private void drawNode(int node, int x, int y, Canvas canvas) {
        // y == height
        // x == width
        double topX, topY, bottomX, bottomY;

        final double startCoordinatesX = 50;
        final double startCoordinatesY = 50;
        final double sizeOfMove = 30;

        var gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        boolean west = (node & W) == 0;
        boolean east = (node & E) == 0;
        boolean north = (node & N) == 0;
        boolean south = (node & S) == 0;

        topX = startCoordinatesX + sizeOfMove * x;
        topY = startCoordinatesY + sizeOfMove * y;
        bottomX = topX + sizeOfMove;
        bottomY = topY + sizeOfMove;

        if (north) {
            gc.strokeLine(topX, topY, bottomX, topY);
        }
        if (south) {
            gc.strokeLine(topX, bottomY, bottomX, bottomY);
        }
        if (east) {
            gc.strokeLine(bottomX, topY, bottomX, bottomY);
        }
        if (west) {
            gc.strokeLine(topX, topY, topX, bottomY);
        }


    }


    public void displayMaze(Canvas canvas) {
        var grid = this.getGrid();
        var g = canvas.getGraphicsContext2D();
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                drawNode(grid[y][x], x, y, canvas);
            }
        }
    }


    private void createStartAndEnd(Random random) {
        var width = this.getWidth();
        var height = this.getHeight();
        boolean top = random.nextInt(-1, 1) != 0;
        if (top) {
            int startX = random.nextInt(width);
            int endX = random.nextInt(width);
            grid[0][startX] |= N;
            grid[grid.length - 1][endX] |= S;
            grid[0][startX] |= START;
            grid[grid.length - 1][endX] |= END;
        } else {
            int startY = random.nextInt(height);
            int endY = random.nextInt(height);
            grid[startY][0] |= W;
            grid[endY][grid[0].length] |= E;
            grid[startY][0] |= START;
            grid[endY][grid[0].length] |= END;
        }

    }

    private byte moveInXChange(int direction) {
        return switch (direction) {
            case N, S -> 0;
            case E -> 1;
            case W -> -1;
            default -> throw new IllegalStateException("Unexpected value: " + direction); // TODO handle
        };
    }

    private byte moveInYChange(int direction) {
        return switch (direction) {
            case E, W -> 0;
            case S -> 1;
            case N -> -1;
            default -> throw new IllegalStateException("Unexpected value: " + direction); // TODO handle
        };
    }

    private byte oppositeDirection(int direction) {
        return switch (direction) {
            case N -> S;
            case S -> N;
            case E -> W;
            case W -> E;
            default -> throw new IllegalStateException("Unexpected value: " + direction); // TODO handle
        };
    }

    private void initializeTrees() {
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                sets[y][x] = new Tree();
            }
        }
    }

    private ArrayList<Edge> createListOfEdges() {
        var edges = new ArrayList<Edge>();
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                if (y > 0) edges.add(new Edge(x, y, N));
                if (x > 0) edges.add(new Edge(x, y, W));
            }
        }
        return edges;
    }

    private void shuffleEdges(ArrayList<Edge> edges, Random random) {
        Collections.shuffle(edges, random);
    }

    private void kruskalAlgorithm(ArrayList<Edge> edges) {
        while (!edges.isEmpty()) {
            var edge = edges.removeLast();
            int x = edge.x();
            int y = edge.y();
            byte direction = edge.direction();
            int newX = x + moveInXChange(direction);
            int newY = y + moveInYChange(direction);

            var firstSet = sets[y][x];
            var secondSet = sets[newY][newX];

            if (!firstSet.connected(secondSet)) {
                firstSet.connect(secondSet);
                grid[y][x] |= direction;
                grid[newY][newX] |= oppositeDirection(direction);
            }
        }
    }

    public record Edge(int x, int y, byte direction) {
    }

    public static class Tree {
        Tree parent = null;


        public Tree root() {
            return parent != null ? parent.root() : this;
        }


        public boolean connected(Tree other) {
            return this.root() == other.root();
        }


        public void connect(Tree other) {
            other.root().parent = this;
        }
    }
}
