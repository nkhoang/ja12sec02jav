package com.nkhoang.design.patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class NotificationNotifier {
    private List<INotification> subscribers = new ArrayList<INotification>();

    public void addSubscriber(INotification subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(INotification subscriber) {
        subscribers.remove(subscriber);
    }

    public void sendNotification() {
        for (INotification subscriber : subscribers) {
            subscriber.sendNotification();
        }
    }
}
