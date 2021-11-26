package com.it;

import java.io.IOException;
import java.text.ParseException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DBController {
    Base base = new Base("");

    public DBController() throws ParseException, org.json.simple.parser.ParseException, IOException {
    }
    // com.it.Base-level operations
    @GetMapping("/dbs/{dbName}")
    public HttpEntity<RespBody> getBase(@PathVariable String dbName) throws IOException, org.json.simple.parser.ParseException {
        if(!base.baseName.isEmpty())
            base.writeTables();
        if(dbName.isEmpty())
            throw new IllegalArgumentException("GET: com.it.Base name cannot be empty. Try again...");
        if(base.baseName.equals(dbName)) {
            RespBody rp = new RespBody(base.toJson());
            rp.add(linkTo(methodOn(DBController.class).getBase(dbName)).withSelfRel());
            return new ResponseEntity<>(rp, HttpStatus.OK);
        }
        else
            base.switchContext(dbName);
        RespBody rp = new RespBody(base.toJson());
        rp.add(linkTo(methodOn(DBController.class).getBase(base.toJson())).withSelfRel());
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @PostMapping("/dbs")
    public HttpEntity<RespBody> createBase(@RequestParam(value = "dbName") String dbName) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty())
            throw new IllegalArgumentException("POST: com.it.Base name cannot be empty. Try again...");
        if(base.baseName.isEmpty())
            base.switchContext(dbName);
        else
            if(base.baseName.equals(dbName)) {
                RespBody rp = new RespBody("Created: " + base.baseName);
                rp.add(linkTo(methodOn(DBController.class).createBase(dbName)).withSelfRel());
                rp.add(linkTo(methodOn(DBController.class).getBase(dbName)).withRel("getBase"));
                return new ResponseEntity<>(rp, HttpStatus.OK);
            }
            else
                base.switchContext(dbName);
        RespBody rp = new RespBody("Created: " + base.baseName);
        rp.add(linkTo(methodOn(DBController.class).createBase(dbName)).withSelfRel());
        rp.add(linkTo(methodOn(DBController.class).getBase(dbName)).withRel("getBase"));
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @GetMapping("/dbs/{dbName}/{tableName}")
    public HttpEntity<RespBody> getTable(@PathVariable String dbName, @PathVariable String tableName) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getTable: com.it.Base and(or) table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        RespBody rp = new RespBody(base.getTable(tableName));
        rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withSelfRel());
        rp.add(linkTo(methodOn(DBController.class).deleteTable(dbName, tableName)).withRel("deleteTable"));
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @PostMapping("/dbs/{dbName}")
    public HttpEntity<RespBody> addTable(@PathVariable String dbName,
                           @RequestParam(value = "tableName") String tableName,
                           @RequestBody TableScheme scheme) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty())
            throw new IllegalArgumentException("POST addTable: com.it.Base name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        try{
            base.addTable(tableName, scheme.getAttrTypes(), scheme.getAttrNames());
        } catch(Exception ex){
            RespBody rp = new RespBody("Exception: " + ex.getMessage());
            rp.add(linkTo(methodOn(DBController.class).addTable(dbName, tableName, scheme)).withSelfRel());
            rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withRel("getTable"));
            rp.add(linkTo(methodOn(DBController.class).deleteTable(dbName, tableName)).withRel("deleteTable"));
            return new ResponseEntity<>(rp, HttpStatus.BAD_REQUEST);
        }
        RespBody rp = new RespBody("Added table: " + tableName);
        rp.add(linkTo(methodOn(DBController.class).addTable(dbName, tableName, scheme)).withSelfRel());
        rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withRel("getTable"));
        rp.add(linkTo(methodOn(DBController.class).deleteTable(dbName, tableName)).withRel("deleteTable"));
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @DeleteMapping("/dbs/{dbName}")
    public HttpEntity<RespBody> deleteTable(@PathVariable String dbName,
                               @RequestParam(value = "tableName") String tableName) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty())
            throw new IllegalArgumentException("POST addTable: com.it.Base name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        base.deleteTable(tableName);
        RespBody rp = new RespBody("Deleted table: " + tableName);
        rp.add(linkTo(methodOn(DBController.class).deleteTable(dbName, tableName)).withSelfRel());
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    // com.it.Table-level operations
    @GetMapping("/dbs/{dbName}/{tableName}/{id}")
    public HttpEntity<RespBody> getRow(@PathVariable String dbName, @PathVariable String tableName, @PathVariable int id) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: com.it.Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        String row = "[]";
        try{
            row = base.getRow(tableName, id);
        } catch (Exception ex){
            RespBody rp = new RespBody("Exception: " + ex.getMessage());
            rp.add(linkTo(methodOn(DBController.class).getRow(dbName, tableName, id)).withSelfRel());
            rp.add(linkTo(methodOn(DBController.class).deleteRow(dbName, tableName, id)).withRel("deleteRow"));
            rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withRel("getTable"));
            return new ResponseEntity<>(rp, HttpStatus.BAD_REQUEST);
        }
        RespBody rp = new RespBody("Requested row: " + row);
        rp.add(linkTo(methodOn(DBController.class).getRow(dbName, tableName, id)).withSelfRel());
        rp.add(linkTo(methodOn(DBController.class).deleteRow(dbName, tableName, id)).withRel("deleteRow"));
        rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withRel("getTable"));
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @PostMapping("/dbs/{dbName}/{tableName}")
    public HttpEntity<RespBody> addRow(@PathVariable String dbName, @PathVariable String tableName,
                         @RequestBody TableRow entry) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: com.it.Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        base.addRow(tableName, entry.getRow());
        RespBody rp = new RespBody("Added row: " + tableName);
        rp.add(linkTo(methodOn(DBController.class).addRow(dbName, tableName, entry)).withSelfRel());
        rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withRel("getTable"));
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @PutMapping("/dbs/{dbName}/{tableName}/{id}")
    public HttpEntity<RespBody> editRow(@PathVariable String dbName, @PathVariable String tableName,
                          @PathVariable int id, @RequestBody TableRow entry) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: com.it.Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        base.updateRow(tableName, id, entry.getRow());
        RespBody rp = new RespBody("Edited row: " + tableName);
        rp.add(linkTo(methodOn(DBController.class).editRow(dbName, tableName, id, entry)).withSelfRel());
        rp.add(linkTo(methodOn(DBController.class).deleteRow(dbName, tableName, id)).withRel("deleteRow"));
        rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withRel("getTable"));
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @DeleteMapping("/dbs/{dbName}/{tableName}/{id}")
    public HttpEntity<RespBody> deleteRow(@PathVariable String dbName, @PathVariable String tableName,
                             @PathVariable int id) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: com.it.Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        base.deleteRow(tableName, id);
        RespBody rp = new RespBody("Deleted row: " + tableName + " with id " + id);
        rp.add(linkTo(methodOn(DBController.class).deleteRow(dbName, tableName, id)).withSelfRel());
        rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withRel("getTable"));
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @PutMapping("/dbs/{dbName}/{tableName}/rmdp")
    public HttpEntity<RespBody> removeDuplicates(@PathVariable String dbName, @PathVariable String tableName) throws IOException, org.json.simple.parser.ParseException {
        if(dbName.isEmpty() || tableName.isEmpty())
            throw new IllegalArgumentException("GET getRow: com.it.Base or table name cannot be empty. Try again...");
        if(!base.baseName.equals(dbName))
            base.switchContext(dbName);
        base.removeDuplicates(tableName);
        RespBody rp = new RespBody("Removed duplicates: " + tableName);
        rp.add(linkTo(methodOn(DBController.class).removeDuplicates(dbName, tableName)).withSelfRel());
        rp.add(linkTo(methodOn(DBController.class).getTable(dbName, tableName)).withRel("getTable"));
        return new ResponseEntity<>(rp, HttpStatus.OK);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final String exceptionHandlerIllegalArgumentException(final IllegalArgumentException e) {
        return '"' + e.getMessage() + '"';
    }
}
