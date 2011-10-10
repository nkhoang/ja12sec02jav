package com.nkhoang.design.patterns.adapter;

/**
 * Using Object Adapter which extends the target subclass and delegates to the existing class.
 */
public class CollectionObjectAdapter extends ClsCollectionBase {
    ClsStack stack = new ClsStack();

    public void add(String s) {
        stack.push(s);
    }
}
