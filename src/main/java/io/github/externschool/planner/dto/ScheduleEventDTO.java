package io.github.externschool.planner.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduleEventDTO {
    private Long id;
    @NotNull private LocalDate date;
    @NotNull private LocalTime startTime;
    private String description;
    @NotNull private Boolean isOpen;
    @NotNull private String eventType;
    @NotNull private String title;
    @NotNull private LocalDateTime created;

    public ScheduleEventDTO(final Long id,
                            @NotNull final LocalDate date,
                            @NotNull final LocalTime startTime,
                            final String description,
                            @NotNull final Boolean isOpen,
                            @NotNull final String eventType,
                            @NotNull final String title,
                            @NotNull final LocalDateTime created) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.description = description;
        this.isOpen = isOpen;
        this.eventType = eventType;
        this.title = title;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(final Boolean open) {
        isOpen = open;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "ScheduleEventDTO{" +
                "id=" + id +
                ", date=" + date +
                ", startTime=" + startTime +
                ", description='" + description + '\'' +
                ", isOpen=" + isOpen +
                ", eventType='" + eventType + '\'' +
                ", title='" + title + '\'' +
                ", created=" + created +
                '}';
    }
}
