import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.ArrayList;

public class Dispatcher {
    public static void main(String[] argv) throws java.text.ParseException, ParseException, IOException {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("name", AttributeType.STRING));
        attributes.add(new Attribute("age", AttributeType.INT));
        attributes.add(new Attribute("gender", AttributeType.CHAR));
        attributes.add(new Attribute("salaryRange", AttributeType.MONEY_IVL));
        attributes.get(0).setVal("John");
        attributes.get(1).setVal(23);
        attributes.get(2).setVal('M');
        MoneyInterval mi = new MoneyInterval();
        attributes.get(3).setVal(mi);

        TableEntry te = new TableEntry(attributes);
        Table t = new Table("myTable", attributes);
        Table t2 = new Table("myTable2", attributes);
        t.addEntry(te);
        t2.addEntry(te);
        Base base = new Base("");
        base.addTable(t);
        base.addTable(t2);
        String serializedJson = base.toJson();
        System.out.println(serializedJson);
        String fileName = "C:\\JavaProjects\\IT\\dbs\\myDB";
        try{
            base.writeTables(fileName);
        } catch (IOException ex){
            System.out.println("Exception at json export. " + ex.getMessage());
        }
        try{
            base.readTables(fileName);
        } catch (Exception ex){
            System.out.println("Exception at json import. " + ex.getMessage());
        }
    }
}