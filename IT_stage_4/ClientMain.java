import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

public class ClientMain {
    public static final String UNIQUE_BINDING_NAME = "server.base";

    public static void main(String[] args) throws IOException, NotBoundException, ParseException {
        final Registry registry = LocateRegistry.getRegistry(2732);
        IBase base = (IBase) registry.lookup(UNIQUE_BINDING_NAME);
        boolean proceed = true;
        while (proceed){
            try {
                String[] command = getInput();
                String baseName = command[0];
                String operation = command[1];
                switch (operation) {
                    // base-level commands
                    case "open":
                        base.switchContext(baseName);
                        break;
                    case "add":
                        // table1 sicr name age gender height
                        String attrNames = arrayToComma(Arrays.copyOfRange(command, 4, command.length));
                        base.addTable(command[2], command[3], attrNames);
                        break;
                    case "save":
                        base.writeTables();
                        break;
                    case "display":
                        System.out.println(base.toJson());
                        break;
                    // table-level commands
                    case "create":
                        String entry = arrayToComma(Arrays.copyOfRange(command, 3, command.length));
                        base.addRow(command[2], entry);
                        break;
                    case "read":
                        int rowIdx = Integer.parseInt(command[3]);
                        String row = base.getRow(command[2], rowIdx);
                        System.out.println(row);
                        break;
                    case "update":
                        rowIdx = Integer.parseInt(command[3]);
                        entry = arrayToComma(Arrays.copyOfRange(command, 4, command.length));
                        base.updateRow(command[2], rowIdx, entry);
                        break;
                    case "delete":
                        rowIdx = Integer.parseInt(command[3]);
                        base.deleteRow(command[2], rowIdx);
                        break;
                    case "rmdp": // remove duplicates
                        base.removeDuplicates(command[2]);
                        break;
                    case "stop":
                        proceed = false;
                        break;
                    default:
                        System.out.println("Unknown operation. Try again...");
                        break;
                }
            }
            catch (Exception ex){
                System.out.println("Hoopla! " + ex.getMessage() + ". Try again...");
            }
        }
    }
    public static String[] getInput(){ // return baseName, operation, arguments
        Scanner sc = new Scanner (System.in);
        String input = sc.nextLine();
        return input.split("\\s+");
    }
    public static String arrayToComma(String[] args){
        String res = "";
        for(int i = 0; i < args.length; i++){
            res += args[i];
            if(i != args.length - 1)
                res += ",";
        }
        return res;
    }
}
