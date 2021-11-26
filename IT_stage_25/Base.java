package com.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.*;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.*;

@Document("webApiDBs")
public class Base {
    private ArrayList<Table> tables;
    private String baseName;
    Base(){}
    Base(String baseName, ArrayList<Table> tables){
        this.baseName = baseName;
        this.tables = new ArrayList<>();
        this.tables.addAll(tables);
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables.clear();
        this.tables.addAll(tables);
    }

    public boolean tableExists(String tableName){
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        for(var t: tables)
            if(t.getName().equals(tableName))
                return true;
        return false;
    }
    public void addTable(Table table){
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(!tableExists(table.getName()))
            tables.add(table); // new table name is unique
    }

    public void addTable(String tableName, String attrTypes, ArrayList<String> attrNames) {
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if (attrNames.size() != attrTypes.length()) throw new IllegalArgumentException("attrTypes.size != attrNames");
        if (tableName.isEmpty() || attrTypes.isEmpty() || attrNames.isEmpty()) return;
        if(tableExists(tableName)) return;
        attrTypes.toLowerCase();
        System.out.println(attrTypes.length() + " " + attrNames.size());
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < attrTypes.length(); i++){
            Attribute attr;
            switch (attrTypes.charAt(i)){
                case 'i':
                    attr = new Attribute(attrNames.get(i), AttributeType.INT);
                    attributes.add(attr);
                    break;
                case 'r':
                    attr = new Attribute(attrNames.get(i), AttributeType.REAL);
                    attributes.add(attr);
                    break;
                case 'c':
                    attr = new Attribute(attrNames.get(i), AttributeType.CHAR);
                    attributes.add(attr);
                    break;
                case 's':
                    attr = new Attribute(attrNames.get(i), AttributeType.STRING);
                    attributes.add(attr);
                    break;
                case 'm':
                    attr = new Attribute(attrNames.get(i), AttributeType.MONEY);
                    attributes.add(attr);
                    break;
                case 'v':
                    attr = new Attribute(attrNames.get(i), AttributeType.MONEY_IVL);
                    attributes.add(attr);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + attrTypes.charAt(i));
            }
            attributes.add(attr);
        }
        ArrayList<Attribute> cleanedAttributes = new ArrayList<>();
        for(int k = 1; k < attributes.size(); k = k + 2)
            cleanedAttributes.add(new Attribute(attributes.get(k).getName(), attributes.get(k).getType()));
        Table newTable = new Table(tableName, cleanedAttributes, new ArrayList<>());
        tables.add(newTable);
        System.out.println("ADDED NEW TABLE " + tables.get(0).getAttributes().size());
        for (Attribute attr : tables.get(0).getAttributes()){
            System.out.println(attr.getName() + " " + attr.getType().toString());
        }
    }

    public boolean deleteTable(String tableName){
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(tableName.isEmpty()) return false;
        if(!tableExists(tableName)) return false;
        tables.removeIf(table -> table.getName().equals(tableName));
        return true;
    }

    public void addRow(String tableName, String rowEntries) throws InvalidPropertiesFormatException {
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(tableName.isEmpty() || rowEntries.isEmpty()) return;
        if(!tableExists(tableName)) return;
        String[] entries = rowEntries.replaceAll("\\s+","").split(",");
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).getName().equals(tableName)) {
                tables.get(i).addEntry(entries);
                return;
            }
        return;
    }

    public void addRow(String tableName, ArrayList<String> rowEntries) throws InvalidPropertiesFormatException {
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(tableName.isEmpty() || rowEntries.isEmpty()) return;
        if(!tableExists(tableName)) return;
        String[] entries = new String[rowEntries.size()];
        for(int i = 0; i < entries.length; i++)
            entries[i] = rowEntries.get(i);
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).getName().equals(tableName)) {
                tables.get(i).addEntry(entries);
                return;
            }
        return;
    }

    public String getRow(String tableName, int rowIdx) throws RemoteException {
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(tableName.isEmpty() || rowIdx < 0 ) return "";
        if(!tableExists(tableName)) return "";
        for(int i = 0; i < tables.size(); i++){
            if(tables.get(i).getName().equals(tableName)) {
                if (tables.get(i).getEntries().size() == 0)
                    return "";
                else
                    return tables.get(i).getRow(rowIdx);
            }
        }
        return "";
    }

    public void updateRow(String tableName, int rowIdx, String rowEntries) throws RemoteException, InvalidPropertiesFormatException {
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(tableName.isEmpty() || rowIdx < 0 ) return;
        if(!tableExists(tableName)) return;
        String[] entries = rowEntries.replaceAll("\\s+","").split(",");
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).getName().equals(tableName)){
                tables.get(i).updateRow(entries, rowIdx);
                return;
            }
        return;
    }

    public void updateRow(String tableName, int rowIdx, ArrayList<String> rowEntries) throws RemoteException, InvalidPropertiesFormatException {
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(tableName.isEmpty() || rowIdx < 0 ) return;
        if(!tableExists(tableName)) return;
        String[] entries = new String[rowEntries.size()];
        for(int i = 0; i < rowEntries.size(); i++)
            entries[i] = rowEntries.get(i);
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).getName().equals(tableName)){
                tables.get(i).updateRow(entries, rowIdx);
                return;
            }
        return;
    }

    public boolean deleteRow(String tableName, int rowIdx) throws RemoteException {
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(tableName.isEmpty() || rowIdx < 0 ) return false;
        if(!tableExists(tableName)) return false;
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).getName().equals(tableName))
                return tables.get(i).delRow(rowIdx);
        return false;
    }

    public int removeDuplicates(String tableName) throws RemoteException {
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        if(!tableExists(tableName)) return -1;
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).getName().equals(tableName))
                return tables.get(i).removeDuplicates();
        return 0;
    }

    public String toJson(){
        if(baseName.isEmpty()) throw new IllegalStateException("com.it.Base name is not assigned");
        JSONObject obj = new JSONObject();
        JSONArray tableArr = new JSONArray();
        for(var t : tables) {
            tableArr.add(t.toJson());
        }
        obj.put("tables", tableArr);
        return obj.toJSONString();
    }
    Table getTable(String tableName){
        if(!tableExists(tableName)) return null;
        for(var t: tables)
            if(t.getName().equals(tableName))
                return t;
        return null;
    }

    public String inputBaseName() throws IOException {
        Scanner sc = new Scanner(System.in); //System.in is a standard input stream
        System.out.print("Enter a unique base name: ");
        String str = sc.nextLine();              //reads string
        return str;
    }
}