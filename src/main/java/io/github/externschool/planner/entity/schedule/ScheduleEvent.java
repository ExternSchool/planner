package io.github.externschool.planner.entity.schedule;

import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Entity
@Table(name = "schedule_event")
public class ScheduleEvent {

    //TODO need to extract to base entity class and change strategy to sequence
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "is_open")
    private Boolean isOpen;

    @Column(name = "is_cancelled")
    private Boolean isCancelled;

    @Column(name = "is_accomplished")
    private Boolean isAccomplished;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "event_type_id")
    private ScheduleEventType type;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private Set<Participant> participants = new HashSet<>();

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

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isAccomplished() {
        return isAccomplished;
    }

    public void setAccomplished(boolean accomplished) {
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
        ToStringBuilder sb = new ToStringBuilder(this)
                .append("id", getId())
                .append("title", getTitle())
                .append("description", getDescription())
                .append("location", getLocation())
                .append("startOfEvent", getStartOfEvent())
                .append("endOfEvent", getEndOfEvent())
                .append("createdAt", getCreatedAt())
                .append("modifiedAt", getModifiedAt())
                .append("isOpen", isOpen())
                .append("isCancelled", isCancelled())
                .append("isAccomplished", isAccomplished())
                .append("owner", Optional.ofNullable(getOwner()).map(User::getEmail).orElse(""))
                .append("type", Optional.ofNullable(getType()).map(ScheduleEventType::getName).orElse("Not defined"))
                .append("participants", getParticipants().size());

        return sb.toString();
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
