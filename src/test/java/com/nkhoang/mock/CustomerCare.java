package com.nkhoang.mock;

public class CustomerCare {
    public boolean processCustomerOrder(Customer c) {
        boolean result = false;
        if (LMSUtil.canStandardize(c.getAddress())) {
            // do something here.
            LMSUtil.standardizeAddress(c.getAddress());
            // do something here.
            result = true;
        }

        return result;
    }
}
