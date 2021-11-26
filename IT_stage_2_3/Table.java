import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;

public class Table {

    final String name;
    final ArrayList<Attribute> attributes;
    ArrayList<TableEntry> entries;
    Table(String tableName, ArrayList<Attribute> attributes){ // tableName is good
        name = tableName;
        this.attributes = attributes;
        entries = new ArrayList<>();
    }
    public void addEntry(){
        TableEntry entry = new TableEntry(attributes);
        entries.add(entry);
    }
    public void addEntry(TableEntry entry){
        entries.add(entry);
    }
    protected boolean attrNameGood(String newName){
        if(newName.isEmpty() || newName.length() < 1)
            return false;
        for (var attr : attributes)
            if(attr.name == newName)
                return false;
        return true;
    }
    public void removeDuplicates(){
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
    }
    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        JSONArray attrArray = new JSONArray();
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
}
