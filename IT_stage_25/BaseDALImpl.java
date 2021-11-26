package com.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.rmi.RemoteException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
@Repository
public class BaseDALImpl implements BaseDAL{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Base> getAllBasesDAL() {
        return mongoTemplate.findAll(Base.class);
    }

    @Override
    public String getBaseDAL(String dbName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        return mongoTemplate.findOne(query,Base.class).toJson();
    }

    @Override
    public String addBaseDAL(String dbName) {
        Base newBase = new Base(dbName, new ArrayList<>());
        mongoTemplate.save(newBase);
        return newBase.toJson();
    }

    @Override
    public List<Base> deleteBaseDAL(String dbName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        mongoTemplate.findAndRemove(query, Base.class);
        return mongoTemplate.findAll(Base.class);
    }

    @Override
    public String getTableDAL(String dbName, String tableName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        if(!mongoTemplate.exists(query,Base.class))
            throw new InvalidParameterException("Base " + dbName + " doesn't exist.");
        Base base = mongoTemplate.findOne(query, Base.class);
        if(base.tableExists(tableName))
            return base.getTable(tableName).toJsonStr();
        throw new InvalidParameterException("Table "+ tableName + "doesn't exist.");
    }

    @Override
    public String addTableDAL(String dbName, String tableName, TableScheme scheme) {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        if(!mongoTemplate.exists(query,Base.class))
            throw new InvalidParameterException("Base " + dbName + " doesn't exist.");
        Base base = mongoTemplate.findOne(query, Base.class);
        if(base.tableExists(tableName))
            return base.getTable(tableName).toJsonStr();
        base.addTable(tableName, scheme.getAttrTypes(), scheme.getAttrNames());
        mongoTemplate.updateFirst(query, Update.update("tables", base.getTables()), Base.class);
        return base.getTable(tableName).toJsonStr();
    }

    @Override
    public String deleteTableDAL(String dbName, String tableName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        if(!mongoTemplate.exists(query,Base.class))
            throw new InvalidParameterException("Base " + dbName + " doesn't exist.");
        Base base = mongoTemplate.findOne(query, Base.class);
        if(base.tableExists(tableName)) {
            base.deleteTable(tableName);
            mongoTemplate.updateFirst(query, Update.update("tables", base.getTables()), Base.class);
            return mongoTemplate.findOne(query, Base.class).toJson();
        }
        throw new InvalidParameterException("Table "+ tableName + "doesn't exist.");
    }

    @Override
    public String getRowDAL(String dbName, String tableName, int idx) throws RemoteException {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        if(!mongoTemplate.exists(query,Base.class))
            throw new InvalidParameterException("Base " + dbName + " doesn't exist.");
        Base base = mongoTemplate.findOne(query, Base.class);
        if(base.tableExists(tableName)) {
            return base.getRow(tableName,idx);
        }
        throw new InvalidParameterException("Table "+ tableName + "doesn't exist.");
    }

    @Override
    public TableRow addRowDAL(String dbName, String tableName, TableRow entry) throws InvalidPropertiesFormatException {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        if(!mongoTemplate.exists(query,Base.class))
            throw new InvalidParameterException("Base " + dbName + " doesn't exist.");
        Base base = mongoTemplate.findOne(query, Base.class);
        if(base.tableExists(tableName)) {
            base.addRow(tableName, entry.getRow());
            mongoTemplate.updateFirst(query, Update.update("tables", base.getTables()), Base.class);
            return entry;
        }
        throw new InvalidParameterException("Table "+ tableName + "doesn't exist.");
    }

    @Override
    public TableRow updateRowDAL(String dbName, String tableName, TableRow entry, int idx) throws RemoteException, InvalidPropertiesFormatException {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        if(!mongoTemplate.exists(query,Base.class))
            throw new InvalidParameterException("Base " + dbName + " doesn't exist.");
        Base base = mongoTemplate.findOne(query, Base.class);
        if(base.tableExists(tableName)) {
            base.updateRow(tableName, idx, entry.getRow());
            mongoTemplate.updateFirst(query, Update.update("tables", base.getTables()), Base.class);
            return entry;
        }
        throw new InvalidParameterException("Table "+ tableName + "doesn't exist.");
    }

    @Override
    public String deleteRowDAL(String dbName, String tableName, int idx) throws RemoteException {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        if(!mongoTemplate.exists(query,Base.class))
            throw new InvalidParameterException("Base " + dbName + " doesn't exist.");
        Base base = mongoTemplate.findOne(query, Base.class);
        if(base.tableExists(tableName)) {
            base.deleteRow(tableName, idx);
            mongoTemplate.updateFirst(query, Update.update("tables", base.getTables()), Base.class);
            return base.getTable(tableName).toJsonStr();
        }
        throw new InvalidParameterException("Table "+ tableName + "doesn't exist.");
    }

    @Override
    public String rmdpDAL(String dbName, String tableName) throws RemoteException {
        Query query = new Query();
        query.addCriteria(Criteria.where("baseName").is(dbName));
        if(!mongoTemplate.exists(query,Base.class))
            throw new InvalidParameterException("Base " + dbName + " doesn't exist.");
        Base base = mongoTemplate.findOne(query, Base.class);
        if(base.tableExists(tableName)) {
            base.removeDuplicates(tableName);
            mongoTemplate.updateFirst(query, Update.update("tables", base.getTables()), Base.class);
            return base.getTable(tableName).toJsonStr();
        }
        throw new InvalidParameterException("Table "+ tableName + "doesn't exist.");
    }
}
