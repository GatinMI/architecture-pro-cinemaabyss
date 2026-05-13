package com.cinemaabyss.events.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Event {
    private String id;
    private String type;
    private String timestamp;
    private Object payload;

    public Event() {
    }

    public Event(String type, Object payload) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.timestamp = Instant.now().toString();
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
