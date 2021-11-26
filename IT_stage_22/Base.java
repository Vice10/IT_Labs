package com.it;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Attr;

import java.io.*;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.*;
import com.google.cloud.storage.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Base {
    ArrayList<Table> tables;
    String baseName = "";
    private String dbsDir = "C:\\JavaProjects\\IT_WebApi_GoogleStorage\\dbs\\";
    private String projectId = "webapi-bucket";
    private String serviceAccCredPath = "C:\\JavaProjects\\IT_WebApi_GoogleStorage\\gcloudKeys\\webapi-bucket-66dccd58b47b.json";
    private String bucketName = "webapi_db";
    private String bucketUrl = "https://storage.googleapis.com/" + bucketName + "/";
    Base(String contextName) throws IOException, ParseException, org.json.simple.parser.ParseException {
        //import tables from file. If it doesn't exist, init a new tables array
        if(contextName.isEmpty()) {
            baseName = "";
            tables = new ArrayList<>();
        }
        else {
            baseName = contextName;
            if(cloudBlobExists(baseName)) {
                tables = new ArrayList<>();
                readTablesCloud(baseName);
            } else  {
                System.out.println("Blob does not exist");
                tables = new ArrayList<>();
            }
        }
    }

    private boolean cloudBlobExists(String blobName) throws IOException {
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(serviceAccCredPath)))
                .build()
                .getService();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll())
            if (bucket.getName().equals(bucketName)){
                Page<Blob> blobs = bucket.list();
                for(Blob blob : blobs.iterateAll())
                    if (blob.getName().equals(blobName)){
                        return true;
                    }
            }
        return false;
    }

    public boolean switchContext(String baseName) throws IOException, org.json.simple.parser.ParseException {
        if(baseName.isEmpty()) return false;
        this.baseName = baseName;
        if (!cloudBlobExists(baseName)){
            tables = new ArrayList<>();
        }
        else {
            try {
                readTablesCloud(baseName);
            } catch (Exception ex){
                System.out.println("Base " + baseName + " is empty or contains damaged data. Creating a new base.");
                tables = new ArrayList<>();
            }
        }
        return true;
    }

    public boolean tableExists(String tableName){
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        for(var t: tables)
            if(t.name.equals(tableName))
                return true;
        return false;
    }
    public void addTable(Table table){
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(!tableExists(table.name))
            tables.add(table); // new table name is unique
    }


    public void addTable(String tableName, String attrTypes, String attrNames) {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        String[] names = attrNames.replaceAll("\\s+","").split(",");
        if (names.length != attrTypes.length()) return;
        if (tableName.isEmpty() || attrTypes.isEmpty() || attrNames.isEmpty()) return;
        if(tableExists(tableName)) return;
        attrTypes.toLowerCase();
        System.out.println(attrTypes.length() + " " + names.length);
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < attrTypes.length(); i++){
            Attribute attr;
            switch (attrTypes.charAt(i)){
                case 'i':
                    attr = new Attribute(names[i], AttributeType.INT);
                    attributes.add(attr);
                    break;
                case 'r':
                    attr = new Attribute(names[i], AttributeType.REAL);
                    attributes.add(attr);
                    break;
                case 'c':
                    attr = new Attribute(names[i], AttributeType.CHAR);
                    attributes.add(attr);
                    break;
                case 's':
                    attr = new Attribute(names[i], AttributeType.STRING);
                    attributes.add(attr);
                    break;
                case 'm':
                    attr = new Attribute(names[i], AttributeType.MONEY);
                    attributes.add(attr);
                    break;
                case 'v':
                    attr = new Attribute(names[i], AttributeType.MONEY_IVL);
                    attributes.add(attr);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + attrTypes.charAt(i));
            }
            attributes.add(attr);
        }
        ArrayList<Attribute> cleanedAttributes = new ArrayList<>();
        for(int k = 1; k < attributes.size(); k = k + 2)
            cleanedAttributes.add(new Attribute(attributes.get(k).name, attributes.get(k).type));
        Table newTable = new Table(tableName, cleanedAttributes);
        tables.add(newTable);
        System.out.println("ADDED NEW TABLE " + tables.get(0).attributes.size());
        for (Attribute attr : tables.get(0).attributes){
            System.out.println(attr.name + " " + attr.type.toString());
        }
    }

    public void addTable(String tableName, String attrTypes, ArrayList<String> attrNames) {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
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
            cleanedAttributes.add(new Attribute(attributes.get(k).name, attributes.get(k).type));
        Table newTable = new Table(tableName, cleanedAttributes);
        tables.add(newTable);
        System.out.println("ADDED NEW TABLE " + tables.get(0).attributes.size());
        for (Attribute attr : tables.get(0).attributes){
            System.out.println(attr.name + " " + attr.type.toString());
        }
    }

    public boolean deleteTable(String tableName){
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(tableName.isEmpty()) return false;
        if(!tableExists(tableName)) return false;
        tables.removeIf(table -> table.name.equals(tableName));
        return true;
    }

    public void addRow(String tableName, String rowEntries) throws InvalidPropertiesFormatException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(tableName.isEmpty() || rowEntries.isEmpty()) return;
        if(!tableExists(tableName)) return;
        String[] entries = rowEntries.replaceAll("\\s+","").split(",");
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).name.equals(tableName)) {
                tables.get(i).addEntry(entries);
                return;
            }
        return;
    }

    public void addRow(String tableName, ArrayList<String> rowEntries) throws InvalidPropertiesFormatException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(tableName.isEmpty() || rowEntries.isEmpty()) return;
        if(!tableExists(tableName)) return;
        String[] entries = new String[rowEntries.size()];
        for(int i = 0; i < entries.length; i++)
            entries[i] = rowEntries.get(i);
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).name.equals(tableName)) {
                tables.get(i).addEntry(entries);
                return;
            }
        return;
    }

    public String getRow(String tableName, int rowIdx) throws RemoteException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(tableName.isEmpty() || rowIdx < 0 ) return "";
        if(!tableExists(tableName)) return "";
        for(int i = 0; i < tables.size(); i++){
            if(tables.get(i).name.equals(tableName)) {
                if (tables.get(i).entries.size() == 0)
                    return "";
                else
                    return tables.get(i).getRow(rowIdx);
            }
        }
        return "";
    }

    public void updateRow(String tableName, int rowIdx, String rowEntries) throws RemoteException, InvalidPropertiesFormatException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(tableName.isEmpty() || rowIdx < 0 ) return;
        if(!tableExists(tableName)) return;
        String[] entries = rowEntries.replaceAll("\\s+","").split(",");
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).name.equals(tableName)){
                tables.get(i).updateRow(entries, rowIdx);
                return;
            }
        return;
    }

    public void updateRow(String tableName, int rowIdx, ArrayList<String> rowEntries) throws RemoteException, InvalidPropertiesFormatException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(tableName.isEmpty() || rowIdx < 0 ) return;
        if(!tableExists(tableName)) return;
        String[] entries = new String[rowEntries.size()];
        for(int i = 0; i < rowEntries.size(); i++)
            entries[i] = rowEntries.get(i);
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).name.equals(tableName)){
                tables.get(i).updateRow(entries, rowIdx);
                return;
            }
        return;
    }

    public boolean deleteRow(String tableName, int rowIdx) throws RemoteException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(tableName.isEmpty() || rowIdx < 0 ) return false;
        if(!tableExists(tableName)) return false;
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).name.equals(tableName))
                return tables.get(i).delRow(rowIdx);
        return false;
    }


    public int removeDuplicates(String tableName) throws RemoteException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        if(!tableExists(tableName)) return -1;
        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).name.equals(tableName))
                return tables.get(i).removeDuplicates();
        return 0;
    }


    public String toJson(){
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
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
            if(t.name == tableName)
                return t;
        return null;
    }

    public void writeTablesCloud() throws IOException {
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(serviceAccCredPath)))
                .build()
                .getService();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll())
            if (bucket.getName().equals(bucketName)){
                bucket.create(baseName, toJson().getBytes(UTF_8));
                return;
            }
        return;
    }
    /*public void writeTables() throws IOException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        FileWriter fr = new FileWriter(dbsDir + baseName, false);
        fr.write(toJson());
        fr.flush();
        fr.close();
    }*/
    public void readTablesCloud(String fileName) throws IOException, org.json.simple.parser.ParseException {
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(serviceAccCredPath)))
                .build()
                .getService();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll())
            if (bucket.getName().equals(bucketName)){
                Page<Blob> blobs = bucket.list();
                for(Blob blob : blobs.iterateAll())
                    if (blob.getName().equals(fileName)){
                        String fileContent = new String(blob.getContent());
                        readTables(fileContent);
                    }
            }
    }
    public void readTables(String dbJson) throws IOException, org.json.simple.parser.ParseException {
        if(baseName.isEmpty()) throw new IllegalStateException("Base name is not assigned");
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(dbJson);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray jtables = (JSONArray) jsonObject.get("tables");
        Iterator iterator = jtables.iterator();
        tables = new ArrayList<>();
        while (iterator.hasNext()) { // iterator is table
            String tableJson = iterator.next().toString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(tableJson);
            JsonNode attrJson = rootNode.get("attributes");
            JsonNode entriesJson = rootNode.get("entries");
            String tableName = rootNode.get("name").asText();
            ArrayList<Attribute> attributes = new ArrayList<>();
            // parse attributes
            for(int i = 0; i < attrJson.size(); i++){
                String attrName = attrJson.get(i).get("name").asText();
                AttributeType attrType = AttributeType.valueOf(attrJson.get(i).get("type").asText());
                Attribute attr = new Attribute(attrName, attrType);
                attributes.add(attr);
            }
            ArrayList<TableEntry> entries = new ArrayList<>();
            // parse entries
            for(int i = 0; i < entriesJson.size(); i++){
                TableEntry te = new TableEntry(attributes);
                for(int j = 0; j < attributes.size(); j++){
                    Attribute curAttr = new Attribute(attributes.get(j));
                    switch (curAttr.type){
                        case INT:
                            curAttr.setVal(entriesJson.get(i).get(j).asInt());
                            break;
                        case REAL:
                            curAttr.setVal(entriesJson.get(i).get(j).asDouble());
                            break;
                        case CHAR:
                            curAttr.setVal(entriesJson.get(i).get(j).asText().charAt(0));
                            break;
                        case STRING:
                            curAttr.setVal(entriesJson.get(i).get(j).asText());
                            break;
                        case MONEY:
                            curAttr.setVal(new BigDecimal(entriesJson.get(i).get(j).asText()));
                            break;
                        case MONEY_IVL:
                            BigDecimal left = new BigDecimal(entriesJson.get(i).get(j).get("left").asText());
                            BigDecimal right = new BigDecimal(entriesJson.get(i).get(j).get("right").asText());
                            curAttr.setAsMoneyIv(left, right);
                            break;
                        default:
                            break;
                    }
                    te.setVal(curAttr);
                }
                entries.add(te);
            }
            System.out.println("Inside Base.readTables: entries -> " + entries.size());
            // add attributes and entries to the table
            Table table = new Table(tableName, attributes);
            for(var entry : entries)
                table.addEntry(entry);
            // add table to base
            tables.add(table);
        }
    }
    public String inputBaseName() throws IOException {
        Scanner sc = new Scanner(System.in); //System.in is a standard input stream
        System.out.print("Enter a unique base name: ");
        String str = sc.nextLine();              //reads string
        return str;
    }
}