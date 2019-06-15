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

    public Boolean isOpen() {
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

    public static final class ScheduleEventDTOBuilder {
        private Long id;
        private LocalDate date;
        private LocalTime startTime;
        private String description;
        private Boolean isOpen;
        private String eventType;
        private String title;
        private LocalDateTime created;

        private ScheduleEventDTOBuilder() {
        }

        public static ScheduleEventDTOBuilder aScheduleEventDTO() {
            return new ScheduleEventDTOBuilder();
        }

        public ScheduleEventDTOBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ScheduleEventDTOBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public ScheduleEventDTOBuilder withStartTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ScheduleEventDTOBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ScheduleEventDTOBuilder withIsOpen(Boolean isOpen) {
            this.isOpen = isOpen;
            return this;
        }

        public ScheduleEventDTOBuilder withEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public ScheduleEventDTOBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ScheduleEventDTOBuilder withCreated(LocalDateTime created) {
            this.created = created;
            return this;
        }

        public ScheduleEventDTO build() {
            return new ScheduleEventDTO(id, date, startTime, description, isOpen, eventType, title, created);
        }
    }
}
