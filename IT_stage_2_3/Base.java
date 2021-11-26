import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Base {
    ArrayList<Table> tables;
    final String baseName;
    Base(String contextName) throws IOException, ParseException, org.json.simple.parser.ParseException {
        //import tables from file. If it doesn't exist, init a new tables array
        if(contextName.isEmpty()) {
            tables = new ArrayList<>();
            baseName = inputBaseName();
        }
        else {
            String dbsDir = "C:\\JavaProjects\\IT\\dbs\\";
            File f = new File(dbsDir + contextName);
            baseName = contextName;
            if(f.exists() && !f.isDirectory()) {
                tables = new ArrayList<>();
                readTables(dbsDir + contextName);
            } else if (f.isDirectory()) {
                System.out.println("Cannot file specified is a directory");
                throw new FileNotFoundException();
            } else if(!f.exists()) {
                System.out.println("File does not exist");
                throw new FileNotFoundException();
            }
        }
    }
    public boolean tableExists(String tableName){
        for(var t: tables)
            if(t.name == tableName)
                return true;
        return false;
    }
    public void addTable(Table table){
        if(!tableExists(table.name))
            tables.add(table); // new table name is unique
    }
    public String toJson(){
        JSONObject obj = new JSONObject();
        JSONArray tableArr = new JSONArray();
        for(var t : tables) {
            tableArr.add(t.toJson());
        }
        obj.put("tables", tableArr);
        return obj.toJSONString();
    }
    public void writeTables(String fileName) throws IOException {
        FileWriter fr = new FileWriter(fileName, true);
        fr.write(toJson());
        fr.flush();
        fr.close();
    }
    public void readTables(String fileName) throws IOException, org.json.simple.parser.ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(fileName));
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
                    entries.add(te);
                }
            }
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