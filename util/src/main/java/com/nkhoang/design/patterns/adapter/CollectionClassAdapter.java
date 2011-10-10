package com.nkhoang.design.patterns.adapter;

/**
 * Using Class Adapter which subclass the existing class and implements the target interface.
 */
public class CollectionClassAdapter extends ClsStack implements CollectionBase {
    public void add(String s) {
        this.push(s);
    }
}
