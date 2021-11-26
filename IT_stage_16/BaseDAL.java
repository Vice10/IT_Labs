package com.it.grpc;

import java.rmi.RemoteException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

public interface BaseDAL {
    List<Base> getAllBasesDAL();
    String getBaseDAL(String dbName);
    String addBaseDAL(String dbName);
    List<Base> deleteBaseDAL(String dbName);
    String getTableDAL(String dbName, String tableName);
    String addTableDAL(String dbName, String tableName, TableScheme scheme);
    String deleteTableDAL(String dbName, String tableName);
    String getRowDAL(String dbName, String tableName, int idx) throws RemoteException;
    String addRowDAL(String dbName, String tableName, TableRow row) throws InvalidPropertiesFormatException;
    TableRow updateRowDAL(String dbName, String tableName, TableRow row, int idx) throws RemoteException, InvalidPropertiesFormatException;
    String deleteRowDAL(String dbName, String tableName, int idx) throws RemoteException;
    String rmdpDAL(String dbName, String tableName) throws RemoteException;
}
