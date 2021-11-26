package com.it;

import java.math.BigDecimal;
import org.json.simple.JSONObject;

public class Attribute {
    final String name;
    private Object val;
    final AttributeType type;
    Attribute(String attrName, AttributeType type){ // attrName != null or ""
        this.type = type;
        name = attrName;
        switch (type){
            case INT:
                val = new Integer(0);
                break;
            case REAL:
                val = new Double(0.0);
                break;
            case CHAR:
                val = new Character('\u0000');
                break;
            case STRING:
                val = new String("");
                break;
            case MONEY:
                val = new BigDecimal(0.0);
                break;
            case MONEY_IVL:
                val = new MoneyInterval();
                break;
            default:
                break;
        }
    }

    Attribute(String attrName, AttributeType type, Object val){
        this.type = type;
        name = attrName;
        switch (type){
            case INT:
                val = new Integer(0);
                break;
            case REAL:
                val = new Double(0.0);
                break;
            case CHAR:
                val = new Character('\u0000');
                break;
            case STRING:
                val = new String("");
                break;
            case MONEY:
                val = new BigDecimal(0.0);
                break;
            case MONEY_IVL:
                val = new MoneyInterval();
                break;
            default:
                break;
        }
        setVal(val);
    }

    public Attribute(Attribute attribute) {
        this.name = attribute.name;
        this.type = attribute.type;
        this.val = attribute.val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public void setAsMoneyIv(BigDecimal left, BigDecimal right){
        MoneyInterval mi = new MoneyInterval();
        mi.set(left, right);
        this.val = mi;
    }

    public Object getVal() {
        return val;
    }

    public Object getJsonFormatVal(){
        switch (type){
            case MONEY_IVL:
                MoneyInterval mi = (MoneyInterval) val;
                return mi.toJson();
            case CHAR:
                return val.toString();
            default:
                return val;
        }
    }

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("type", type);
        obj.put("val", val);
        return obj;
    }

    public JSONObject toJsonNoValue(){
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("type", type.toString());
        return obj;
    }
}
enum AttributeType{
    INT,
    REAL,
    CHAR,
    STRING,
    MONEY,
    MONEY_IVL
}