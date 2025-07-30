package cz.upol.logicgo.algorithms.sudoku;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;

import java.util.ArrayList;
import java.util.BitSet;

public class SudokuValidator {
    private final Sudoku sudoku;
    private final ArrayList<BitSet> rows;
    private final ArrayList<BitSet> columns;
    private final ArrayList<BitSet> subgrids;

    public SudokuValidator(Sudoku sudoku) {
        this.sudoku = sudoku;

        int size = sudoku.getType().getGridSize();
        this.rows = initBitSetList(size);
        this.columns = initBitSetList(size);
        this.subgrids = initBitSetList(size);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int val = sudoku.getNumber(row, col);
                if (val != 0) update(row, col, val);
            }
        }
    }

    private SudokuValidator(Sudoku sudoku, SudokuValidator other) {
        this.sudoku = sudoku;
        this.rows = copyBitSetLists(other.rows);
        this.columns = copyBitSetLists(other.columns);
        this.subgrids = copyBitSetLists(other.subgrids);
    }

    public SudokuValidator copy(Sudoku sudoku) {
        return new SudokuValidator(sudoku, this);
    }

    private ArrayList<BitSet> copyBitSetLists(ArrayList<BitSet> original) {
        ArrayList<BitSet> copy = new ArrayList<>(original.size());
        for (BitSet bitSet : original) {
            copy.add((BitSet) bitSet.clone());
        }
        return copy;
    }


    public boolean isValidMove(int row, int col, int num) {
        if (num < 1 || num > sudoku.getType().getGridSize()) return false;

        return !exists(row, col, num);
    }


    public ArrayList<TargetedCell> getEmptyCells() {
        var gridSize = sudoku.getType().getGridSize();
        ArrayList<TargetedCell> emptyCells = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (sudoku.isZero(i, j)) {
                    emptyCells.add(new TargetedCell(i, j));
                }
            }
        }
        return emptyCells;
    }

    public ArrayList<Integer> getCellCandidates(int row, int col) {
        ArrayList<Integer> candidates = new ArrayList<>();
        BitSet usedInRow = rows.get(row);
        BitSet usedInCol = columns.get(col);
        BitSet usedInSubgrid = subgrids.get(SudokuUtils.calcSubGridIndex(row, col, sudoku.getRegionLayout()));

        for (int num : sudoku.getType().getPossibleNumbers()) {
            if (usedInRow.get(num) || usedInCol.get(num) || usedInSubgrid.get(num)) {
                continue;
            }
            candidates.add(num);
        }
        return candidates;
    }

    private ArrayList<BitSet> initBitSetList(int size) {
        ArrayList<BitSet> list = new ArrayList<>();
        for (int i = 0; i < size; i++) list.add(new BitSet(size + 1));
        return list;
    }

    public void update(int row, int col, int num) {
        RegionLayout reg = sudoku.getRegionLayout();
        int idx = SudokuUtils.calcSubGridIndex(row, col, reg);
        rows.get(row).set(num);
        columns.get(col).set(num);
        subgrids.get(idx).set(num);
    }

    public void unset(int row, int col, int num) {
        RegionLayout reg = sudoku.getRegionLayout();
        int idx = SudokuUtils.calcSubGridIndex(row, col, reg);
        rows.get(row).clear(num);
        columns.get(col).clear(num);
        subgrids.get(idx).clear(num);
    }

    public void remove(int row, int col, int num) {
        RegionLayout reg = sudoku.getRegionLayout();
        int idx = SudokuUtils.calcSubGridIndex(row, col, reg);
        rows.get(row).clear(num);
        columns.get(col).clear(num);
        subgrids.get(idx).clear(num);
    }

    public boolean exists(int row, int col, int num) {
        RegionLayout reg = sudoku.getRegionLayout();
        int idx = SudokuUtils.calcSubGridIndex(row, col, reg);
        return rows.get(row).get(num) || columns.get(col).get(num) || subgrids.get(idx).get(num);
    }

}


