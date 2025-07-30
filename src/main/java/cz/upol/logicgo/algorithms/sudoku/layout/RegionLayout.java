package cz.upol.logicgo.algorithms.sudoku.layout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RegionLayout {
    private String name;
    private Integer[][] regions;
    private String translatedName;
    private int type;

    public RegionLayout() {
    }

    public ArrayList<SubGridCells> getCellsBelongToSubgrid(int index){
        var list = new ArrayList<SubGridCells>();
        for (int i = 0; i < regions.length; i++) {
            for (int j = 0; j < regions[0].length; j++) {
                if (regions[i][j] == index){
                    list.add(new SubGridCells(i, j));
                }
            }
        }
        return list;
    }

    public RegionLayout(String name, int type, Integer[][] regions) {
        this.name = name;
        this.type = type;
        this.regions = regions;
    }

    public String getName() {
        return name;
    }

    public Integer[][] getRegions() {
        return regions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegions(Integer[][] regions) {
        this.regions = regions;
    }

    public String getTranslatedName() {
        return translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    @Override
    public String toString() {
        return "RegionLayout{name='" + name + "'}";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public record SubGridCells(int row, int col) {}
}
