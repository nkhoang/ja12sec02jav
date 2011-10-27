package com.nkhoang.design.patterns.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventNotification implements INotification{
    private static Logger LOG = LoggerFactory.getLogger(EventNotification.class.getCanonicalName());
    public void sendNotification() {
        LOG.info("Event Notification Executed.");
    }
}
