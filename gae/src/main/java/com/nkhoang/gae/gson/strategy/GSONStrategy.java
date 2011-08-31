package com.nkhoang.gae.gson.strategy;

import java.util.List;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * GSON strategy is implementation of ExclusionStrategy which helps GSON to know
 * which configured properties will be omitted from JSON content.
 * 
 * @author hnguyen93
 * 
 */
public class GSONStrategy implements ExclusionStrategy {
    public static final String EXCLUDE_ATTRIBUTES = "exclude_attributes";
    public static final String DATA = "data";

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
