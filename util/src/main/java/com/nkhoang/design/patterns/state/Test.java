package com.nkhoang.design.patterns.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static Logger LOG = LoggerFactory.getLogger(Test.class.getCanonicalName());

    public static void main(String args[]) {
        BulbController controller = new BulbController();
        LOG.info("Current bulb state: " + controller.getBulbState());
        // change state
        controller.pressSwitch();
        LOG.info("Current bulb state: " + controller.getBulbState());
        controller.pressSwitch();
        LOG.info("Current bulb state: " + controller.getBulbState());
    }
}
