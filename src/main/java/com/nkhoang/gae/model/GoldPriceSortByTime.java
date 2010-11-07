package com.nkhoang.gae.model;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: Nov 7, 2010
 * Time: 8:31:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoldPriceSortByTime implements Comparator<GoldPrice> {
    @Override
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
