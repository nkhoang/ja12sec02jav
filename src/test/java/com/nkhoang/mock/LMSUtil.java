package com.nkhoang.mock;

import com.nkhoang.model.Address;

public class LMSUtil {
    public static String standardizeAddress(Address address) {
        String result = null;
        // do some proces here
        result = address.getAddress() + " " + address.getCity();

        return result;
    }

    public static boolean canStandardize(Address address) {
        boolean result = false;
        // do something here.
        if (address != null) {
            // do something here
            result = true;
        }

        return result;
    }
}