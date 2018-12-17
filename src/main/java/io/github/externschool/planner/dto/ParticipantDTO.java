package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

public class ParticipantDTO {
    private long id;
    private LocalDate date;
    private LocalTime time;
    private String personName;
    private String ownerName;
    private String eventTitle;
    private String eventDescription;
    private long personId;
    private long ownerId;
    private long eventId;
    private Person person;
    private Person owner;
    private ScheduleEvent event;

    public ParticipantDTO() {
    }

    public ParticipantDTO(final Long id, final Person person, final ScheduleEvent event) {
        this.id = id;
        this.person = person;
        this.event = event;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(final LocalTime time) {
        this.time = time;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(final String personName) {
        this.personName = personName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(final String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(final String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(final long personId) {
        this.personId = personId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(final long ownerId) {
        this.ownerId = ownerId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(final long eventId) {
        this.eventId = eventId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(final Person owner) {
        this.owner = owner;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(final ScheduleEvent event) {
        this.event = event;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ParticipantDTO)) return false;

        ParticipantDTO that = (ParticipantDTO) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getPersonId(), that.getPersonId())
                .append(getOwnerId(), that.getOwnerId())
                .append(getEventId(), that.getEventId())
                .append(getDate(), that.getDate())
                .append(getTime(), that.getTime())
                .append(getPersonName(), that.getPersonName())
                .append(getOwnerName(), that.getOwnerName())
                .append(getEventTitle(), that.getEventTitle())
                .append(getEventDescription(), that.getEventDescription())
                .append(getPerson(), that.getPerson())
                .append(getOwner(), that.getOwner())
                .append(getEvent(), that.getEvent())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getDate())
                .append(getTime())
                .append(getPersonName())
                .append(getOwnerName())
                .append(getEventTitle())
                .append(getEventDescription())
                .append(getPersonId())
                .append(getOwnerId())
                .append(getEventId())
                .append(getPerson())
                .append(getOwner())
                .append(getEvent())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id: ", id)
                .append("person id: ", person.getId())
                .append("event id: ", event.getId())
                .toString();
    }
}
