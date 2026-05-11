package com.cinemaabyss.events.model;

public class EventResponse {
    private String status;
    private Integer partition;
    private Long offset;
    private Event event;

    public EventResponse() {
    }

    public EventResponse(String status, Integer partition, Long offset, Event event) {
        this.status = status;
        this.partition = partition;
        this.offset = offset;
        this.event = event;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
