package com.itWebApi;

import java.util.ArrayList;
public class TableScheme {
    private String attrTypes;
    private ArrayList<String> attrNames;
    TableScheme(){}
    TableScheme(String attrTypes, ArrayList<String> attrNames){
        this.attrTypes = attrTypes;
        this.attrNames = new ArrayList<>();
        this.attrNames.addAll(attrNames);
    }

    public String getAttrTypes() {
        return attrTypes;
    }

    public void setAttrTypes(String attrTypes) {
        this.attrTypes = attrTypes;
    }

    public ArrayList<String> getAttrNames() {
        return attrNames;
    }

    public void setAttrNames(ArrayList<String> attrNames) {
        this.attrNames = new ArrayList<>();
        this.attrNames.addAll(attrNames);
    }
}
