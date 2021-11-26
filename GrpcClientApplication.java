package com.it.grpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GrpcClientApplication {
    public static void main(String[] args) {
        GRPCClientService clientService = new GRPCClientService();
        boolean proceed = true;
        String response;
        System.out.println("Client started");
        while (proceed){
            try {
                String[] command = getInput();
                String baseName = command[0];
                String operation = command[1];
                String tableName;
                switch (operation) {
                    // base-level commands
                    case "addTable":
                        // myDB2 addTable table3 sicr name age gender height
                        String attrTypes = command[3];
                        List<String> attrNames = Arrays.asList(Arrays.copyOfRange(command, 4, command.length));
                        response = clientService.postTable(baseName, command[2], TableScheme.newBuilder()
                                .setAttrTypes(attrTypes)
                                .addAllAttrNames(attrNames)
                                .build());
                        System.out.println(response);
                        break;
                    case "getBase":
                        // myDB getBase
                        response = clientService.getBase(baseName);
                        System.out.println(response);
                        break;
                    // table-level commands
                    case "addRow":
                        // myDB2 addRow table1 Lee 40 M 180.5
                        List<String> entry = Arrays.asList(Arrays.copyOfRange(command, 3, command.length));
                        tableName = command[2];
                        response = clientService.postRow(baseName, tableName, TableRow.newBuilder()
                                .addAllRow(entry)
                                .build());
                        System.out.println(response);
                        break;
                    case "rmdp": // remove duplicates
                        tableName = command[2];
                        response = clientService.rmdp(baseName, tableName);
                        System.out.println(response);
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
        Scanner sc = new Scanner(System.in);
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