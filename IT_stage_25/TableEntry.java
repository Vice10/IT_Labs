package com.it;

import org.json.simple.JSONArray;

import java.util.ArrayList;

public class TableEntry {
    private ArrayList<Attribute> cells;
    TableEntry(){}
    TableEntry(ArrayList<Attribute> schema){
        cells = new ArrayList<>();
        for(var attr : schema)
            cells.add(new Attribute(attr));
    }

    public ArrayList<Attribute> getCells() {
        return cells;
    }

    public void setCells(ArrayList<Attribute> cells) {
        this.cells.clear();
        this.cells.addAll(cells);
    }

    public boolean setVal(Attribute newVal){
        for (var cell : cells)
            if(cell.getName() == newVal.getName() && cell.getType() == newVal.getType()) {
                cell.setVal(newVal.getVal());
                return true;
            }
        return false;
    }
    public boolean setVal(String attrName, AttributeType attrType, Object val){
        for (var cell : cells)
            if(cell.getName() == attrName && cell.getType() == attrType) {
                cell.setVal(val);
                return true;
            }
        return false;
    }
    public JSONArray toJson(){
        JSONArray myCells = new JSONArray();
        for(var cell : cells)
            myCells.add(cell.getJsonFormatVal());
        return myCells;
    }
    public String toJsonStr(){
        JSONArray myCells = new JSONArray();
        for(var cell : cells)
            myCells.add(cell.getJsonFormatVal());
        return myCells.toJSONString();
    }
}
