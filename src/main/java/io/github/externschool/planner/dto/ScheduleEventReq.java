package io.github.externschool.planner.dto;

import java.time.LocalDateTime;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class ScheduleEventReq {

    private String title;
    private String description;
    private String eventType;
    private String location;
    private LocalDateTime startOfEvent;
    private LocalDateTime endOfEvent;

    public ScheduleEventReq() {}

    public ScheduleEventReq(
            final String title,
            final String description,
            final String location,
            final LocalDateTime startOfEvent,
            final LocalDateTime endOfEvent,
            final String eventType

    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startOfEvent = startOfEvent;
        this.endOfEvent = endOfEvent;
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartOfEvent() {
        return startOfEvent;
    }

    public void setStartOfEvent(LocalDateTime startOfEvent) {
        this.startOfEvent = startOfEvent;
    }

    public LocalDateTime getEndOfEvent() {
        return endOfEvent;
    }

    public void setEndOfEvent(LocalDateTime endOfEvent) {
        this.endOfEvent = endOfEvent;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
