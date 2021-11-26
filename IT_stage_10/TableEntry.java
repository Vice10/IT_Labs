package com.itWebApi;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Attr;

import java.util.ArrayList;

public class TableEntry {
    private ArrayList<Attribute> cells;
    TableEntry(ArrayList<Attribute> schema){
        cells = new ArrayList<>();
        for(var attr : schema)
            cells.add(new Attribute(attr));
    }
    public boolean setVal(Attribute newVal){
        for (var cell : cells)
            if(cell.name == newVal.name && cell.type == newVal.type) {
                cell.setVal(newVal.getVal());
                return true;
            }
        return false;
    }
    public boolean setVal(String attrName, AttributeType attrType, Object val){
        for (var cell : cells)
            if(cell.name == attrName && cell.type == attrType) {
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
