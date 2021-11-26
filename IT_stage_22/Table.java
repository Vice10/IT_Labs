package com.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;

public class Table {

    final String name;
    final ArrayList<Attribute> attributes;
    ArrayList<TableEntry> entries;
    Table(String tableName, ArrayList<Attribute> attributes){ // tableName is good
        name = tableName;
        this.attributes = attributes;
        entries = new ArrayList<>();
    }
    public void addEntry(String[] entryValues) throws InvalidPropertiesFormatException {
        TableEntry entry = new TableEntry(attributes);
        for(int j = 0; j < attributes.size(); j++){
            Attribute curAttr = new Attribute(attributes.get(j));
            switch (curAttr.type){
                case INT:
                    curAttr.setVal(Integer.parseInt(entryValues[j]));
                    break;
                case REAL:
                    curAttr.setVal(Float.parseFloat(entryValues[j]));
                    break;
                case CHAR:
                    curAttr.setVal(entryValues[j].charAt(0));
                    break;
                case STRING:
                    curAttr.setVal(entryValues[j]);
                    break;
                case MONEY:
                    curAttr.setVal(new BigDecimal(entryValues[j]));
                    break;
                case MONEY_IVL:
                    String leftNum = "", rightNum= "";
                    String moneyString = entryValues[j];
                    if(!moneyString.contains("|")) throw new InvalidPropertiesFormatException("MONEY_IVL parsing error. ");
                    boolean splitterReached = false;
                    for(int k = 0; k < moneyString.length(); k++) {
                        if(moneyString.charAt(k) == '|'){
                            splitterReached = true;
                            continue;
                        }
                        if(!splitterReached)
                            leftNum += moneyString.charAt(k);
                        else
                            rightNum += moneyString.charAt(k);
                    }
                    BigDecimal left = new BigDecimal(leftNum);
                    BigDecimal right = new BigDecimal(rightNum);
                    curAttr.setAsMoneyIv(left, right);
                    break;
                default:
                    break;
            }
            entry.setVal(curAttr);
        }
        entries.add(entry);
    }
    public void updateRow(String[] entryValues, int idx) throws InvalidPropertiesFormatException {
        if(idx < 0 || idx > entries.size()) return;
        TableEntry entry = new TableEntry(attributes);
        for(int j = 0; j < attributes.size(); j++){
            Attribute curAttr = new Attribute(attributes.get(j));
            switch (curAttr.type){
                case INT:
                    curAttr.setVal(Integer.parseInt(entryValues[j]));
                    break;
                case REAL:
                    curAttr.setVal(Float.parseFloat(entryValues[j]));
                    break;
                case CHAR:
                    curAttr.setVal(entryValues[j].charAt(0));
                    break;
                case STRING:
                    curAttr.setVal(entryValues[j]);
                    break;
                case MONEY:
                    curAttr.setVal(new BigDecimal(entryValues[j]));
                    break;
                case MONEY_IVL:
                    String leftNum = "", rightNum= "";
                    String moneyString = entryValues[j];
                    if(!moneyString.contains("|")) throw new InvalidPropertiesFormatException("MONEY_IVL parsing error. ");
                    boolean splitterReached = false;
                    for(int k = 0; k < moneyString.length(); k++) {
                        if(moneyString.charAt(k) == '|'){
                            splitterReached = true;
                            continue;
                        }
                        if(!splitterReached)
                            leftNum += moneyString.charAt(k);
                        else
                            rightNum += moneyString.charAt(k);
                    }
                    BigDecimal left = new BigDecimal(leftNum);
                    BigDecimal right = new BigDecimal(rightNum);
                    curAttr.setAsMoneyIv(left, right);
                    break;
                default:
                    break;
            }
            entry.setVal(curAttr);
        }
        entries.set(idx, entry);
    }
    public String getRow(int idx){
        if(idx < 0 || idx > entries.size()) return "";
        return entries.get(idx).toJsonStr();
    }
    public boolean delRow(int idx){
        if(idx < 0 || idx > entries.size()) return false;
        TableEntry deleted = entries.remove(idx);
        return deleted != null;
    }
    public void addEntry(String jentry) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode entriesJson = mapper.readTree(jentry);
        TableEntry te = new TableEntry(attributes);
        for(int j = 0; j < attributes.size(); j++){
            Attribute curAttr = new Attribute(attributes.get(j));
            switch (curAttr.type){
                case INT:
                    curAttr.setVal(entriesJson.get(j).asInt());
                    break;
                case REAL:
                    curAttr.setVal(entriesJson.get(j).asDouble());
                    break;
                case CHAR:
                    curAttr.setVal(entriesJson.get(j).asText().charAt(0));
                    break;
                case STRING:
                    curAttr.setVal(entriesJson.get(j).asText());
                    break;
                case MONEY:
                    curAttr.setVal(new BigDecimal(entriesJson.get(j).asText()));
                    break;
                case MONEY_IVL:
                    BigDecimal left = new BigDecimal(entriesJson.get(j).get("left").asText());
                    BigDecimal right = new BigDecimal(entriesJson.get(j).get("right").asText());
                    curAttr.setAsMoneyIv(left, right);
                    break;
                default:
                    break;
            }
            te.setVal(curAttr);
        }
        entries.add(te);
    }
    public void addEntry(TableEntry entry){
        entries.add(entry);
    }
    protected boolean attrNameGood(String newName){
        if(newName.isEmpty() || newName.length() < 1)
            return false;
        for (var attr : attributes)
            if(attr.name.equals(newName))
                return false;
        return true;
    }
    public int removeDuplicates(){
        ArrayList<String> entriesStr = new ArrayList<>();
        ArrayList<TableEntry> newEntries = new ArrayList<>();
        for(TableEntry te : entries){
            String entryStr = te.toJsonStr();
            if(!entriesStr.contains(entryStr)){
                entriesStr.add(te.toJsonStr());
                newEntries.add(te);
            }
        }
        entries = newEntries;
        return newEntries.size() - entries.size();
    }
    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        JSONArray attrArray = new JSONArray();
        System.out.println("In Table.toJson: attrSize-> " + attributes.size());
        for (var attr : attributes)
            attrArray.add(attr.toJsonNoValue());
        JSONArray entryArray = new JSONArray();
        for (var entry : entries)
            entryArray.add(entry.toJson());
        obj.put("name", name);
        obj.put("entries", entryArray);
        obj.put("attributes", attrArray);
        return obj;
    }
    public String toJsonStr() {
        return toJson().toJSONString();
    }
}
