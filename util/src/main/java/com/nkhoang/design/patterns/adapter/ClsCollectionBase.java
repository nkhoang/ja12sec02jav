package com.nkhoang.design.patterns.adapter;


public class ClsCollectionBase implements CollectionBase {
    public void add(String s) {
        System.out.println("Added element named : " + s);
    }
}
