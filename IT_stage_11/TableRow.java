package com.it;

import java.util.ArrayList;

public class TableRow {
    private ArrayList<String> row;
    TableRow(){}
    TableRow(ArrayList<String> row){
        this.row = new ArrayList<>();
        this.row.addAll(row);
    }

    public ArrayList<String> getRow() {
        return row;
    }

    public void setRow(ArrayList<String> row) {
        this.row = new ArrayList<>();
        this.row.addAll(row);
    }
}
