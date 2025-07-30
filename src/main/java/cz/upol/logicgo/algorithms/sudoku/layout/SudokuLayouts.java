package cz.upol.logicgo.algorithms.sudoku.layout;

import java.util.List;

public class SudokuLayouts {
    private List<SizeLayouts> sizes;

    public List<SizeLayouts> getLayouts() {
        return sizes;
    }

    public SudokuLayouts(List<SizeLayouts> sizes) {
        this.sizes = sizes;
    }

    public SudokuLayouts() {
    }

    public List<SizeLayouts> getSizes() {
        return sizes;
    }

    public void setSizes(List<SizeLayouts> sizes) {
        this.sizes = sizes;
    }

    public static class SizeLayouts {
        private int size;
        private List<RegionLayout> layouts;

        public SizeLayouts(int size, List<RegionLayout> layouts) {
            this.size = size;
            this.layouts = layouts;
        }

        public SizeLayouts() {
        }

        public int getSize() {
            return size;
        }

        public List<RegionLayout> getRegionLayouts() {
            return layouts;
        }

        public void setLayouts(List<RegionLayout> layouts) {
            this.layouts = layouts;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
