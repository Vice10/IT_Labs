import org.json.simple.parser.ParseException;

import java.io.IOError;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.InvalidPropertiesFormatException;

public interface IBase extends Remote {
    boolean tableExists(String tableName) throws RemoteException;
    //void addTable(Table table) throws RemoteException;
    void addTable(String tableName, String attrTypes, String attrNames) throws RemoteException;
    void addRow(String tableName, String rowEntries) throws RemoteException, InvalidPropertiesFormatException;
    String getRow(String tableName, int rowIdx) throws RemoteException;
    void updateRow(String tableName, int rowIdx, String rowEntries) throws RemoteException, InvalidPropertiesFormatException;
    boolean deleteRow(String tableName, int rowIdx) throws RemoteException;
    int removeDuplicates(String tableName) throws RemoteException;
    String toJson() throws RemoteException;
    void writeTables() throws IOException;
    boolean switchContext(String baseName) throws IOException, ParseException;
}
