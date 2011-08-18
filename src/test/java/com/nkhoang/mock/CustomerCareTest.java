package com.nkhoang.mock;

import mockit.Expectations;
import mockit.NonStrict;
import mockit.internal.expectations.Expectation;
import org.junit.Assert;
import org.junit.Test;

public class CustomerCareTest {
    @Test
    public void testProcessCustomerOrder() {
        new Expectations() {
            @NonStrict
            LMSUtil lmsUtil;

            {
                LMSUtil.standardizeAddress((Address) any);
                result = "New address";
                LMSUtil.canStandardize((Address) any);
                result = true;
            }
        };

        CustomerCare cc = new CustomerCare();
        boolean isProccessed = cc.processCustomerOrder(new Customer());
        System.out.println(isProccessed);
        Assert.assertTrue(isProccessed);
    }
}
