package io.github.externschool.planner.entity.schedule;

import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Entity
@Table(name = "schedule_event")
public class ScheduleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column
    private String location;

    @Column(name = "start_event", nullable = false)
    private LocalDateTime startOfEvent;

    @Column(name = "end_event", nullable = false)
    private LocalDateTime endOfEvent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_open")
    private Boolean isOpen;

    @Column(name = "is_cancelled")
    private Boolean isCancelled;

    @Column(name = "is_accomplished")
    private Boolean isAccomplished;

    @ManyToOne
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_owner"))
    private User owner;

    @ManyToOne
    @JoinColumn(name = "event_type_id", foreignKey = @ForeignKey(name = "fk_event_type"))
    private ScheduleEventType type;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private Set<Participant> participants = Collections.synchronizedSet(new HashSet<>());

    public ScheduleEvent() {}

    private ScheduleEvent(final Long id,
                          final String title,
                          final String description,
                          final String location,
                          final User owner,
                          final ScheduleEventType type,
                          final LocalDateTime startOfEvent,
                          final LocalDateTime endOfEvent,
                          final LocalDateTime createdAt,
                          final LocalDateTime modifiedAt,
                          final boolean isOpen,
                          final boolean isCancelled,
                          final boolean isAccomplished) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.owner = owner;
        this.type = type;
        this.startOfEvent = startOfEvent;
        this.endOfEvent = endOfEvent;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.isOpen = isOpen;
        this.isCancelled = isCancelled;
        this.isAccomplished = isAccomplished;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ScheduleEventType getType() {
        return type;
    }

    public void setType(ScheduleEventType type) {
        this.type = type;
    }

    public Set<Participant> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setEvent(this);
    }

    public void removeParticipant(Participant participant) {
        participants.remove(participant);
        participant.setEvent(null);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public Boolean isOpen() {
        return isOpen;
    }

    public void setOpen(final Boolean open) {
        isOpen = open;
    }

    public Boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(final Boolean cancelled) {
        isCancelled = cancelled;
    }

    public Boolean isAccomplished() {
        return isAccomplished;
    }

    public void setAccomplished(final Boolean accomplished) {
        isAccomplished = accomplished;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ScheduleEvent)) return false;

        final ScheduleEvent event = (ScheduleEvent) o;

        return new EqualsBuilder()
                .append(getId(), event.getId())
                .append(getTitle(), event.getTitle())
                .append(getDescription(), event.getDescription())
                .append(getLocation(), event.getLocation())
                .append(getStartOfEvent(), event.getStartOfEvent())
                .append(getEndOfEvent(), event.getEndOfEvent())
                .append(getCreatedAt(), event.getCreatedAt())
                .append(getModifiedAt(), event.getModifiedAt())
                .append(isOpen, event.isOpen)
                .append(isCancelled, event.isCancelled)
                .append(isAccomplished, event.isAccomplished)
                .append(getOwner(), event.getOwner())
                .append(getType(), event.getType())
                .append(getParticipants(), event.getParticipants())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getTitle())
                .append(getDescription())
                .append(getLocation())
                .append(getStartOfEvent())
                .append(getEndOfEvent())
                .append(getCreatedAt())
                .append(getModifiedAt())
                .append(isOpen())
                .append(isCancelled())
                .append(isAccomplished())
                .append(getOwner() != null ? 31 : 0)
                .append(getType())
                .append(getParticipants().size())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScheduleEvent.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("modifiedAt=" + modifiedAt)
                .add("title='" + title + "'")
                .add("description='" + description + "'")
                .add("location='" + location + "'")
                .add("startOfEvent=" + startOfEvent)
                .add("endOfEvent=" + endOfEvent)
                .add("createdAt=" + createdAt)
                .add("isOpen=" + isOpen)
                .add("isCancelled=" + isCancelled)
                .add("isAccomplished=" + isAccomplished)
                .add("owner=" + Optional.ofNullable(getOwner()).map(User::getEmail).orElse(""))
                .add("type=" + Optional.ofNullable(getType()).map(ScheduleEventType::getName).orElse("Not defined"))
                .add("participants=" + getParticipants().size())
                .toString();
    }

    public static ScheduleEventBuilder builder() {
        return new ScheduleEventBuilder();
    }

    public static class ScheduleEventBuilder {
        private Long id;
        private String title;
        private String description;
        private String location;
        private User owner;
        private ScheduleEventType type;
        private LocalDateTime startOfEvent;
        private LocalDateTime endOfEvent;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime modifiedAt;
        private boolean isOpen;
        private boolean isCancelled;
        private boolean isAccomplished;

        private ScheduleEventBuilder() {}

        public ScheduleEventBuilder withId(final Long id) {
            this.id = id;
            return this;
        }

        public ScheduleEventBuilder withTitle(final String title) {
            this.title = title;
            return this;
        }

        public ScheduleEventBuilder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public ScheduleEventBuilder withLocation(final String location) {
            this.location = location;
            return this;
        }

        public ScheduleEventBuilder withOwner(final User user) {
            this.owner = user;
            return this;
        }

        public ScheduleEventBuilder withType(final ScheduleEventType eventType) {
            this.type = eventType;
            return this;
        }

        public ScheduleEventBuilder withStartDateTime(final LocalDateTime dateTime) {
            this.startOfEvent = dateTime;
            return this;
        }

        public ScheduleEventBuilder withEndDateTime(final LocalDateTime dateTime) {
            this.endOfEvent = dateTime;
            return this;
        }

        public ScheduleEventBuilder withOpenStatus(final boolean status) {
            this.isOpen = status;
            return this;
        }

        public ScheduleEventBuilder withCancelledStatus(final boolean status) {
            this.isCancelled = status;
            return this;
        }

        public ScheduleEventBuilder withAccomplishedStatus(final boolean status) {
            this.isAccomplished = status;
            return this;
        }

        public ScheduleEvent build() {
            ScheduleEvent event = new ScheduleEvent(
                    this.id,
                    this.title,
                    this.description,
                    this.location,
                    this.owner,
                    this.type,
                    this.startOfEvent,
                    this.endOfEvent,
                    this.createdAt,
                    this.modifiedAt,
                    this.isOpen,
                    this.isCancelled,
                    this.isAccomplished
            );
            Optional.ofNullable(this.owner).ifPresent(o -> o.addOwnEvent(event));

            return event;
        }
    }
}
