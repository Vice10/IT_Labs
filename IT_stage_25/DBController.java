package com.it;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import ch.qos.logback.classic.db.names.TableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;

@RestController
public class DBController {
    @Autowired
    private final IWebApiRepo repository;

    private final BaseDAL baseDAL;

    public DBController(IWebApiRepo repository, BaseDAL baseDAL) {
        this.repository = repository;
        this.baseDAL = baseDAL;
    }
    // com.it.Base-level operations
    @GetMapping("/dbs")
    public List<Base> getAllBases(){
        return baseDAL.getAllBasesDAL();
    }
    @GetMapping("/dbs/{dbName}")
    public String getBase(@PathVariable String dbName){
        return baseDAL.getBaseDAL(dbName);
    }
    @PostMapping("/dbs")
    public String createBase(@RequestParam(value = "dbName") String dbName){
        return baseDAL.addBaseDAL(dbName);
    }
    @DeleteMapping("/dbs/{dbName}")
    public List<Base> deleteBase(@PathVariable String dbName){
        return baseDAL.deleteBaseDAL(dbName);
    }
    @PostMapping("/dbs/{dbName}")
    public String addTable(@PathVariable String dbName,
                           @RequestParam(value = "tableName") String tableName,
                           @RequestBody TableScheme scheme){
        return baseDAL.addTableDAL(dbName, tableName, scheme);
    }
    @GetMapping("/dbs/{dbName}/{tableName}")
    public String getTable(@PathVariable String dbName, @PathVariable String tableName){
        return baseDAL.getTableDAL(dbName, tableName);
    }
    @DeleteMapping("/dbs/d/{dbName}") // unless "../d/.." method will be ambiguous
    public String deleteTable(@PathVariable String dbName,
                            @RequestParam(value = "tableName") String tableName){
        return baseDAL.deleteTableDAL(dbName, tableName);
    }
    // com.it.Table-level operations
    @GetMapping("/dbs/{dbName}/{tableName}/{id}")
    public String getRow(@PathVariable String dbName, @PathVariable String tableName, @PathVariable int id) throws RemoteException {
        return baseDAL.getRowDAL(dbName, tableName, id);
    }
    @PostMapping("/dbs/{dbName}/{tableName}")
    public TableRow addRow(@PathVariable String dbName, @PathVariable String tableName,
                         @RequestBody TableRow entry) throws InvalidPropertiesFormatException {
        return baseDAL.addRowDAL(dbName, tableName, entry);
    }
    @PutMapping("/dbs/{dbName}/{tableName}/{id}")
    public TableRow editRow(@PathVariable String dbName, @PathVariable String tableName,
                             @PathVariable int id, @RequestBody TableRow entry) throws RemoteException, InvalidPropertiesFormatException {
        return baseDAL.updateRowDAL(dbName, tableName, entry, id);
    }
    @DeleteMapping("/dbs/{dbName}/{tableName}/{id}")
    public String deleteRow(@PathVariable String dbName, @PathVariable String tableName,
                             @PathVariable int id) throws RemoteException {
        return baseDAL.deleteRowDAL(dbName, tableName, id);
    }
    @PutMapping("/dbs/{dbName}/{tableName}/rmdp")
    public String removeDuplicates(@PathVariable String dbName, @PathVariable String tableName) throws RemoteException {
        return baseDAL.rmdpDAL(dbName, tableName);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final String exceptionHandlerIllegalArgumentException(final IllegalArgumentException e) {
        return '"' + e.getMessage() + '"';
    }
}
