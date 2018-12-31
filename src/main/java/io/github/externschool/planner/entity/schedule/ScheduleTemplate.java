package io.github.externschool.planner.entity.schedule;

import io.github.externschool.planner.entity.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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

    @Column(name = "week_day", nullable = false)
    private Integer dayOfWeek;

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
                            final Integer dayOfWeek,
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

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(final Integer dayOfWeek) {
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
}
