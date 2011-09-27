package com.nkhoang.gae.model;

import java.util.Comparator;

public class GoldPriceSortByTime implements Comparator<GoldPrice> {
    public int compare(GoldPrice g1, GoldPrice g2) {
        int result = -1;
        if (g1.getTime() > g2.getTime()) {
            result = 1;
        } else if (g1.getTime() == g2.getTime()) {
            result = 0;
        }
        return result;
    }
}
