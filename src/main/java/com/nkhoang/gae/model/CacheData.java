package com.nkhoang.gae.model;

/**
 * 
 * @author hnguyen93
 * 
 */
public class CacheData {
    public CacheData(Object data) {
        this.data = data;
    }

    private boolean isModified = false;
    private Object data;

    public boolean isModified() {
        return isModified;
    }

    public void setModified() {
        this.isModified = true;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}