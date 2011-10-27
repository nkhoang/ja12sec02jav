package com.nkhoang.design.patterns.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotification implements INotification{
    private static Logger LOG = LoggerFactory.getLogger(EmailNotification.class.getCanonicalName());
    public void sendNotification() {
        LOG.info("Email Notification Executed.");
    }
}
