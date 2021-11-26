package com.itWebApi;

import org.json.simple.JSONObject;

import java.math.BigDecimal;

public class MoneyInterval {
    private BigDecimal left;
    private BigDecimal right;
    MoneyInterval(){
        left = new BigDecimal(0.0);
        right = new BigDecimal(1.0);
    }

    public BigDecimal getLeft() {
        return left;
    }

    public String getLeftString() { return left.toString(); }

    public BigDecimal getRight() {
        return right;
    }

    public String getRightString() { return right.toString(); }

    public boolean set(BigDecimal left, BigDecimal right){
        if(left.compareTo(right) == -1) {
            this.left = left;
            this.right = right;
            return true;
        }
        return false;
    }

    public boolean setLeft(BigDecimal left) {
        if(left.compareTo(right) == -1) { // -1 less than
            this.left = left;
            return true;
        }
        return false;
    }

    public boolean setRight(BigDecimal right) {
        if(right.compareTo(left) == 1) { // 1 greater than
            this.right = right;
            return true;
        }
        return false;
    }

    public boolean isIdentical(MoneyInterval mi){
        if(left == mi.left && right == mi.right)
            return true;
        return false;
    }

    public boolean isInInterval(BigDecimal x){
        if(x.compareTo(left) == 1 && x.compareTo(right) == -1)
            return true;
        return false;
    }

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("left", left);
        obj.put("right", right);
        return obj;
    }
}
