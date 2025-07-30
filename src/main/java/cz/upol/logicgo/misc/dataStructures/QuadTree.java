package cz.upol.logicgo.misc.dataStructures;

import cz.upol.logicgo.model.games.drawable.bounds.*;
import cz.upol.logicgo.model.games.drawable.elements.sudoku.SudokuCell;

import java.util.*;

import static cz.upol.logicgo.model.games.drawable.bounds.CalculateBorderPoints.CoordinatesComputeFromCenter.*;

/**
 * třída reprezentující čtyřstrom
 */
public class QuadTree {
    final private QuadTreeNode root;

    /**
     * vytvoří čtyřstrom
     *
     * @param width  šířka stromu
     * @param height výška stromu
     */
    public QuadTree(double width, double height) {
        this.root = new QuadTreeNode(0, 0, height, width, 0, null, null);
        root.setRoot(root);
        createNodesOfQuadTree();
    }

    /**
     * vytvoří uzly čtyřstromu
     */
    private void createNodesOfQuadTree() {
        root.createAllNodesInTree();
        root.setActive(true);
        for (var node : root.getChildrenNodes()) {
            node.setLeaf(true);
            node.setActive(true);
        }
    }

    public void balanceDelete() {
        root.checkChildrenAndBalanceDelete();
    }

    public void balanceInsert() {
        root.checkChildrenAndBalanceInsert();
    }

    public double getRootHeight() {
        return root.height;
    }

    public double getRootWidth() {
        return root.width;
    }

    public QuadTreeNode getRoot() {
        return root;
    }

    /**
     * metoda pro vložení prvku do stromu
     *
     * @param element prvek
     */
    public void insert(IBounds element) {
        root.insert(element);
        root.checkChildrenAndBalanceInsert();
    }

    /**
     * metoda pro smazání prvků ze stromu
     *
     * @param element prvek
     */
    public void remove(IBounds element) {
        root.remove(element);
    }

    /**
     * metoda pro získání prvku na x a y souřadnici
     *
     * @param x x souřadnice
     * @param y y souřadnice
     * @return Optional<DrawableBase> instance s nalezeným prvkem, prázdná pokud nebyl nalezen
     */
    public Optional<IBounds> search(double x, double y) {
        return root.search(x, y);
    }

    /**
     * metoda pro vymazání obsahu stromu
     * pro reset stromu
     */
    public void clear() {
        createNodesOfQuadTree();
    }

    /**
     * třída reprezentující uzel stromu
     */
    public static class QuadTreeNode {
        final static int MAX_DEPTH = 3;
        private final static int MAXIMUM_ELEMENTS_IN_NODE = 8;
        final int depth;
        private final double topLeftX, topLeftY, height, width;
        private final UUID idOfQuadrant;
        private final ArrayList<IBounds> quadNodeElementList = new ArrayList<>();
        private final RectangleBounds rectangleBounds;
        final private QuadTreeNode parent;
        private final QuadTreeNode[] childrenNodes;
        private QuadTreeNode root;
        private boolean isLeaf = false;
        private boolean active = false;

        /**
         * konstruktor pro uzel čtyřstromu
         *
         * @param topLeftX x souřadnice levého horní hrany uzlu
         * @param topLeftY y souřadnice levého horní hrany uzlu
         * @param height   výška uzlu
         * @param width    šířka uzlu
         * @param depth    hloubka uzlu
         * @param root     kořen stromu
         * @param parent   rodičovský uzel
         */
        public QuadTreeNode(double topLeftX, double topLeftY, double height, double width, int depth, QuadTreeNode root, QuadTreeNode parent) {
            this.idOfQuadrant = UUID.randomUUID();
            this.topLeftX = topLeftX;
            this.topLeftY = topLeftY;
            this.height = height;
            this.width = width;
            this.rectangleBounds = new RectangleBounds(topLeftX, topLeftY, width, height);
            this.childrenNodes = new QuadTreeNode[4];
            this.depth = depth;
            this.root = root;
            this.parent = parent;
        }

        public double getTopLeftX() {
            return topLeftX;
        }

        public double getTopLeftY() {
            return topLeftY;
        }

        public QuadTreeNode[] getChildrenNodes() {
            return childrenNodes;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        /**
         * metoda pro vložení prvků do potomků, je-li překročena kapacita
         */
        private void insertElementsIntoChildren() {
            this.setLeaf(false);

            for (var node : this.getChildrenNodes()) {
                node.setActive(true);
                node.setLeaf(true);
            }

            if (this.getQuadNodeElementList().isEmpty()) return;

            for (IBounds element : this.getQuadNodeElementList()) {
                this.insert(element);
            }
            this.getQuadNodeElementList().clear();
        }


        /**
         * metoda, která vytvoří všechny uzly stromu
         */
        private void createAllNodesInTree() {
            if (depth < MAX_DEPTH) {
                createChildren();
                for (var node : childrenNodes) {
                    node.createAllNodesInTree();
                }
            }
        }

        /**
         * vytvoří potomky uzlu
         */
        private void createChildren() {
            double heightOfQuadrant = this.getHeight() / 2;
            double widthOfQuadrant = this.getWidth() / 2;

            double[][] positions = {
                    {topLeftX, topLeftY},
                    {topLeftX + widthOfQuadrant, topLeftY},
                    {topLeftX, topLeftY + heightOfQuadrant},
                    {topLeftX + widthOfQuadrant, topLeftY + heightOfQuadrant}
            };

            for (int i = 0; i < 4; i++) {
                childrenNodes[i] = new QuadTreeNode(positions[i][0], positions[i][1],
                        heightOfQuadrant, widthOfQuadrant, depth + 1, root, this);
            }
        }


        public void printLeafNodeElements() {
            printLeafNodeElements(root);
        }


        private synchronized void printLeafNodeElements(QuadTreeNode node) {
            if (node.isLeaf()) {
                System.out.println("Leaf Node ID: " + node.getIdOfQuadrant());

                for (var element : node.getQuadNodeElementList()) {
                    System.out.println("    Element: " + ((SudokuCell) element).getRow() + " " + ((SudokuCell) element).getCol());
                }
            } else {
                for (QuadTreeNode child : node.getChildrenNodes()) {
                    if (child != null) {
                        printLeafNodeElements(child);
                    }
                }
            }
        }



        /**
         * vloží prvky potomků do rodičovského uzlu
         */
        private void insertElementsToParentNode() {
            var elementsFromChildren = new HashSet<IBounds>();

            this.setLeaf(true);
            for (var childNode : this.getChildrenNodes()) {
                var elementsInChild = childNode.getQuadNodeElementList();
                elementsFromChildren.addAll(elementsInChild);
                elementsInChild.clear();
                childNode.setActive(false);
                childNode.setLeaf(false);
            }
            insertElementsFromChildNodes(elementsFromChildren);

        }

        /**
         * metoda vloží prvky z potomků
         *
         * @param elements vloží prvky z potomků
         */
        private void insertElementsFromChildNodes(HashSet<IBounds> elements) {
            for (var element : elements) {
                this.getQuadNodeElementList().add(element);
            }
        }

        /**
         * metoda pro vložení prvku do uzlu
         */
        public void insert(IBounds element) {
            if (isLeaf()) {
                insertIntoNode(element);
            } else {
                insertIntoChildren(element);
            }
        }

        /**
         * metoda pro vložení prvku do listu
         *
         * @param element prvek
         */
        private void insertIntoNode(IBounds element) {
            if (this.getQuadNodeElementList().contains(element)) return;
            this.getQuadNodeElementList().add(element);
        }

        /**
         * metoda pro vkládání prvku do potomků
         *
         * @param element prvek
         */
        private void insertIntoChildren(IBounds element) {
            for (var child : this.getChildrenNodes()) {
                if (child.intersects(element)) {
                    child.insert(element);
                }
            }
        }

        /**
         * metoda pro smazání prvku ze stromu
         *
         * @param element prvek pro smazání
         */
        public void remove(IBounds element) {
            if (isLeaf()) {
                removeInNode(element);
            } else {
                removeInChildren(element);
            }
        }

        /**
         * metoda pro smazání prvku z uzlu
         *
         * @param element prvek
         */
        private void removeInNode(IBounds element) {
            this.getQuadNodeElementList().removeIf(element::equals);
        }

        /**
         * metoda pro smazání prvku z potomka
         *
         * @param element prvek
         */
        private void removeInChildren(IBounds element) {
            for (var child : this.getChildrenNodes()) {
                if (child.intersects(element)) {
                    child.remove(element);
                }
            }
        }

        /**
         * metoda pro získání prvku na x a y souřadnici
         *
         * @param x x souřadnice
         * @param y y souřadnice
         * @return Optional<DrawableBase> instance s nalezeným prvkem, prázdná pokud nebyl nalezen
         */
        public Optional<IBounds> search(double x, double y) {
            if (isLeaf()) {
                return clickedElementInLeaf(x, y);
            } else {
                for (var child : this.getChildrenNodes()) {
                    if (child.isInNode(x, y)) {
                        return child.search(x, y);
                    }
                }
            }
            return Optional.empty();
        }

        /**
         * metoda pro balancování stromu po mazání
         *
         * @return zda byly potomci uzlu spojeni
         */
        public boolean checkChildrenAndBalanceDelete() {
            int notLeafNodes = 0;
            for (var child : childrenNodes) {  // procházím potomky uzlu
                if (child == null || !child.isActive() || child.isLeaf()) {
                    continue;
                }
                notLeafNodes++;
                if (child.checkChildrenAndBalanceDelete()) notLeafNodes--;
            }
            if (notLeafNodes == 0 && depth >= 1 && this.checkCountMerge()) {
                this.insertElementsToParentNode();
                return true;
            }
            return false;
        }

        /**
         * metoda pro balancování stromu po vkládání
         */
        public void checkChildrenAndBalanceInsert() {
            if (isLeaf) {
                var inserted = false;
                if (depth < MAX_DEPTH && quadNodeElementList.size() >= MAXIMUM_ELEMENTS_IN_NODE) {
                    insertElementsIntoChildren();
                    inserted = true;
                }
                if (inserted) {
                    for (var child : childrenNodes) {
                        if (child == null) return;
                        child.checkChildrenAndBalanceInsert();
                    }
                }
            } else {
                for (var child : childrenNodes) {
                    if (child == null) return;
                    child.checkChildrenAndBalanceInsert();
                }
            }
        }

        /**
         * dotaz zda byl kliknut prvek
         *
         * @param x x souřadnice
         * @param y y souřadnice
         * @return Optional<DrawableBase> instance s nalezeným prvkem, prázdná pokud nebyl nalezen
         */
        private synchronized Optional<IBounds> clickedElementInLeaf(double x, double y) {
            IBounds clickedElement = null;
            loop:
            for (var element : this.getQuadNodeElementList()) {
                switch (element) {
                    case IRectangleBounds rectangleBounds -> {
                        if (rectangleBounds.pointInside(x, y)) {
                            clickedElement = rectangleBounds;
                            System.out.println(clickedElement);
                            break loop;
                        }
                    }
                    case ILineBounds lineBounds -> {
                        if (checkLineClick(x, y, lineBounds)) {
                            clickedElement = lineBounds;
                        }
                    }

                    case null, default -> {
                    }
                }
            }

            return Optional.ofNullable(clickedElement);
        }

        /**
         * dotaz zda byl kliknuta rovná šipka
         *
         * @param x    x souřadnice
         * @param y    y souřadnice
         * @param line
         * @return {@code true} pokud ano, jinak {@code false}
         */
        private boolean checkLineClick(double x, double y, ILineBounds line) {
            double width = 20, height = 20;
            double xMin = topLeftXFromCenter(x, width);
            double yMin = topLeftYFromCenter(y, height);
            double xMax = bottomRightXFromCenter(x, width);
            double yMax = bottomRightYFromCenter(y, height);
            //return CohenSutherlandAlgorithm.isIntersecting(xMin, yMin, xMax, yMax, drawableStraightLine, drawableStraightLine);
            return false;
        }


        public boolean pointInsideRectangle(double topLeftX, double topLeftY, Point point, double width, double height) {
            return (topLeftX <= point.getX()) && (point.getX() <= topLeftX + width) &&
                    (topLeftY <= point.getY()) && (point.getY() <= topLeftY + height);
        }

        /**
         * dotaz zda se souřadnice nachází v uzlu
         *
         * @param x x souřadnice
         * @param y y souřadnice
         * @return true pokud ano, false pokud ne
         */
        public synchronized boolean isInNode(double x, double y) {
            return this.getBounds().pointInBoundsOfElement(x, y);
        }

        public synchronized boolean isInNode(Point point) {
            return isInNode(point.getX(), point.getY());
        }


        public RectangleBounds getBounds() {
            return rectangleBounds;
        }

        /**
         * kontrola zda má dojít k vložení prvků do rodiče
         */
        public synchronized boolean checkCountMerge() {
            HashSet<IBounds> elements = new HashSet<>();
            for (var children : this.getChildrenNodes()) {
                elements.addAll(children.getQuadNodeElementList());
            }
            return elements.size() < MAXIMUM_ELEMENTS_IN_NODE;
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public void setLeaf(boolean leafStatus) {
            isLeaf = leafStatus;
        }

        /**
         * dotaz zda se prvky protínají
         *
         * @return true pokud ano, false pokud ne
         */
        public boolean intersects(IBounds element) {
            return this.getBounds().doElementsIntersect(element);
        }


        public ArrayList<IBounds> getQuadNodeElementList() {
            return quadNodeElementList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuadTreeNode that)) return false;
            return this.hashCode() == that.hashCode();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getIdOfQuadrant());
        }

        public QuadTreeNode getRoot() {
            return root;
        }

        public void setRoot(QuadTreeNode root) {
            this.root = root;
        }

        public UUID getIdOfQuadrant() {
            return idOfQuadrant;
        }

        public QuadTreeNode getParent() {
            return parent;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
