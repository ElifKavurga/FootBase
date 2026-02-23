package com.footbase.patterns.observer;

import com.footbase.entity.Mac;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class MacApprovalSubject implements Subject {

    private final List<Observer> observers = new ArrayList<>();
    private Mac mac;
    private String eventType;

    @Override
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(eventType, mac);
        }
    }

    public void macEklendi(Mac mac) {
        this.mac = mac;
        this.eventType = "MAC_EKLENDI";
        notifyObservers();
    }

    public void macOnaylandi(Mac mac) {
        this.mac = mac;
        this.eventType = "MAC_ONAYLANDI";
        notifyObservers();
    }

    public void macReddedildi(Mac mac) {
        this.mac = mac;
        this.eventType = "MAC_REDDEDILDI";
        notifyObservers();
    }

    public int getObserverCount() {
        return observers.size();
    }
}
