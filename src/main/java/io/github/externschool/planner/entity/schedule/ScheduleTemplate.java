package io.github.externschool.planner.entity.schedule;

import io.github.externschool.planner.entity.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.StringJoiner;

@Entity
@Table(name = "schedule_template")
public class ScheduleTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "week_day", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_event", nullable = false)
    private LocalTime startOfEvent;

    @Column(name = "end_event", nullable = false)
    private LocalTime endOfEvent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_type_id")
    private ScheduleEventType type;

    private ScheduleTemplate() {
    }

    public ScheduleTemplate(final String title,
                            final String description,
                            final String location,
                            final DayOfWeek dayOfWeek,
                            final LocalTime startOfEvent,
                            final LocalTime endOfEvent,
                            final LocalDateTime createdAt,
                            final LocalDateTime modifiedAt,
                            final User owner,
                            final ScheduleEventType type) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.dayOfWeek = dayOfWeek;
        this.startOfEvent = startOfEvent;
        this.endOfEvent = endOfEvent;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.owner = owner;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(final DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartOfEvent() {
        return startOfEvent;
    }

    public void setStartOfEvent(final LocalTime startOfEvent) {
        this.startOfEvent = startOfEvent;
    }

    public LocalTime getEndOfEvent() {
        return endOfEvent;
    }

    public void setEndOfEvent(final LocalTime endOfEvent) {
        this.endOfEvent = endOfEvent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(final LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }

    public ScheduleEventType getType() {
        return type;
    }

    public void setType(final ScheduleEventType type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ScheduleTemplate)) return false;

        ScheduleTemplate template = (ScheduleTemplate) o;

        return new EqualsBuilder()
                .append(getId(), template.getId())
                .append(getTitle(), template.getTitle())
                .append(getDescription(), template.getDescription())
                .append(getLocation(), template.getLocation())
                .append(getDayOfWeek(), template.getDayOfWeek())
                .append(getStartOfEvent(), template.getStartOfEvent())
                .append(getEndOfEvent(), template.getEndOfEvent())
                .append(getCreatedAt(), template.getCreatedAt())
                .append(getModifiedAt(), template.getModifiedAt())
                .append(getOwner(), template.getOwner())
                .append(getType(), template.getType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getTitle())
                .append(getDescription())
                .append(getLocation())
                .append(getDayOfWeek())
                .append(getStartOfEvent())
                .append(getEndOfEvent())
                .append(getCreatedAt())
                .append(getModifiedAt())
                .append(getOwner())
                .append(getType())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScheduleTemplate.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("title='" + title + "'")
                .add("description='" + description + "'")
                .add("location='" + location + "'")
                .add("dayOfWeek=" + dayOfWeek)
                .add("startOfEvent=" + startOfEvent)
                .add("endOfEvent=" + endOfEvent)
                .add("createdAt=" + createdAt)
                .add("modifiedAt=" + modifiedAt)
                .add("owner=" + owner)
                .add("type=" + type)
                .toString();
    }

    public static ScheduleTemplateBuilder builder() {
        return new ScheduleTemplateBuilder();
    }

    public static final class ScheduleTemplateBuilder {
        private Long id;
        private String title;
        private String description;
        private String location;
        private DayOfWeek dayOfWeek;
        private LocalTime startOfEvent;
        private LocalTime endOfEvent;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private User owner;
        private ScheduleEventType type;

        private ScheduleTemplateBuilder() {
        }

        public ScheduleTemplateBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ScheduleTemplateBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ScheduleTemplateBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ScheduleTemplateBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public ScheduleTemplateBuilder withDayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public ScheduleTemplateBuilder withStartOfEvent(LocalTime startOfEvent) {
            this.startOfEvent = startOfEvent;
            return this;
        }

        public ScheduleTemplateBuilder withEndOfEvent(LocalTime endOfEvent) {
            this.endOfEvent = endOfEvent;
            return this;
        }

        public ScheduleTemplateBuilder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ScheduleTemplateBuilder withModifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public ScheduleTemplateBuilder withOwner(User owner) {
            this.owner = owner;
            return this;
        }

        public ScheduleTemplateBuilder withType(ScheduleEventType type) {
            this.type = type;
            return this;
        }

        public ScheduleTemplate build() {
            ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
            scheduleTemplate.setId(id);
            scheduleTemplate.setTitle(title);
            scheduleTemplate.setDescription(description);
            scheduleTemplate.setLocation(location);
            scheduleTemplate.setDayOfWeek(dayOfWeek);
            scheduleTemplate.setStartOfEvent(startOfEvent);
            scheduleTemplate.setEndOfEvent(endOfEvent);
            scheduleTemplate.setCreatedAt(createdAt);
            scheduleTemplate.setModifiedAt(modifiedAt);
            scheduleTemplate.setOwner(owner);
            scheduleTemplate.setType(type);
            return scheduleTemplate;
        }
    }
}
