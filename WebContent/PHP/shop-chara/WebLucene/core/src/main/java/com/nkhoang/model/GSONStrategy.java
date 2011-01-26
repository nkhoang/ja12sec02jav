package com.nkhoang.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangknguyen
 * Date: 1/26/11
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class GSONStrategy implements ExclusionStrategy {
    private final List<String> attributes;

    public GSONStrategy(List<String> attrs) {
        this.attributes = attrs;
    }

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        boolean result = false;
        for (String attr : attributes) {
            if (attr.equals(f.getName())) {
                result = true;
                break;
            }
        }

        return result;
    }

}