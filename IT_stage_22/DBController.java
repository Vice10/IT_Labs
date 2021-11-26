package com.it;
import java.io.IOException;
import java.text.ParseException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Databases and Tables", description = "Endpoints for managing DBs")
@RestController
public class DBController {
    Base base = new Base("");

    public DBController() throws ParseException, org.json.simple.parser.ParseException, IOException {
    }
    // Base-level operations
    @GetMapping("/dbs/{dbName}")
    @Operation(
            summary = "Finds a database",
            description = "Finds a database by its name. If it doesn't exist, creates an empty one",
            tags = { "DB" }
    )
    public String getBase(@PathVariable String dbName) throws IOException, org.json.simple.parser.ParseException {
        if(!base.baseName.isEmpty())
            base.writeTablesCloud();
        if(dbName.isEmpty())
            throw new IllegalArgumentException("GET: Base name cannot be empty. Try again...");
        if(base.baseName.equals(dbName))
            return base.toJson();
        else
            base.switchContext(dbName);
        return base.toJson();
    }
    @PostMapping("/dbs")
    @Operation(
            summary = "Creates a database",
            description = "Creates a new database",
            tags = { "DB" }
    )
    public String createBase(@RequestParam(value = "dbName") String dbName) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty())
            throw new IllegalArgumentException("POST: Base name cannot be empty. Try again...");
        if(base.baseName.isEmpty())
            base.switchContext(dbName);
        else
            if(base.baseName.equals(dbName))
                return base.baseName;
            else
                base.switchContext(dbName);
        return base.baseName;
    }
    @PostMapping("/dbs/{dbName}")
    @Operation(
            summary = "Creates a new table in the DB",
            description = "If a DB doesn't contain a table with given name, a new table is created",
            tags = { "DB" }
    )
    public String addTable(@PathVariable String dbName,
                           @RequestParam(value = "tableName") String tableName,
                           @RequestBody TableScheme scheme) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty())
            throw new IllegalArgumentException("POST addTable: Base name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        System.out.println(scheme.getAttrTypes());
        System.out.println(scheme.getAttrNames());
        try{
            base.addTable(tableName, scheme.getAttrTypes(), scheme.getAttrNames());
        } catch(Exception ex){
            return ex.getMessage();
        }
        return tableName;
    }
    @DeleteMapping("/dbs/{dbName}")
    @Operation(
            summary = "Deletes a table in the database",
            description = "Deletes a table in the DB by name",
            tags = { "DB" }
    )
    public boolean deleteTable(@PathVariable String dbName,
                               @RequestParam(value = "tableName") String tableName) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty())
            throw new IllegalArgumentException("POST addTable: Base name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        return base.deleteTable(tableName);
    }
    // Table-level operations
    @GetMapping("/dbs/{dbName}/{tableName}/{id}")
    @Operation(
            summary = "Find a row in a table",
            description = "Given table name and row index return the row",
            tags = { "TABLE" }
    )
    public String getRow(@PathVariable String dbName, @PathVariable String tableName, @PathVariable int id) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        String row = "[]";
        try{
            row = base.getRow(tableName, id);
        } catch (Exception ex){
            return ex.getMessage();
        }
        return row;
    }

    @PostMapping("/dbs/{dbName}/{tableName}")
    @Operation(
            summary = "Add a row to the table",
            description = "Given row contents add it to the table",
            tags = { "TABLE" }
    )
    public String addRow(@PathVariable String dbName, @PathVariable String tableName,
                         @RequestBody TableRow entry) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        base.addRow(tableName, entry.getRow());
        return tableName;
    }
    @PutMapping("/dbs/{dbName}/{tableName}/{id}")
    @Operation(
            summary = "Edit a row in the table",
            description = "Given row contents and its index update it",
            tags = { "TABLE" }
    )
    public String editRow(@PathVariable String dbName, @PathVariable String tableName,
                          @PathVariable int id, @RequestBody TableRow entry) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        base.updateRow(tableName, id, entry.getRow());
        return tableName;
    }
    @DeleteMapping("/dbs/{dbName}/{tableName}/{id}")
    @Operation(
            summary = "Delete a row in the table",
            description = "Given row index and table name, delete the row",
            tags = { "TABLE" }
    )
    public boolean deleteRow(@PathVariable String dbName, @PathVariable String tableName,
                             @PathVariable int id) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        return base.deleteRow(tableName, id);
    }
    @PutMapping("/dbs/{dbName}/{tableName}/rmdp")
    @Operation(
            summary = "Remove duplicates from the table",
            description = "Given table name, remove all the duplicates",
            tags = { "TABLE" }
    )
    public int removeDuplicates(@PathVariable String dbName, @PathVariable String tableName) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        return base.removeDuplicates(tableName);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final String exceptionHandlerIllegalArgumentException(final IllegalArgumentException e) {
        return '"' + e.getMessage() + '"';
    }
}
