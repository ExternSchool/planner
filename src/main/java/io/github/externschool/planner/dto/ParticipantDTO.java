package io.github.externschool.planner.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.StringJoiner;

public class ParticipantDTO {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String personName;
    private String ownerName;
    private String eventTitle;
    private String eventDescription;
    private long personId;
    private long ownerId;
    private long eventId;
    private Long planOneId;
    private boolean planOneSemesterOne;
    private boolean planOneSemesterTwo;
    private Long planTwoId;
    private boolean planTwoSemesterOne;
    private boolean planTwoSemesterTwo;
    private String planOneTitle;
    private String planTwoTitle;

    private ParticipantDTO() {
    }

    public ParticipantDTO(final Long id) {
        this.id = id;
        planOneSemesterOne = false;
        planOneSemesterTwo = false;
        planTwoSemesterOne = false;
        planTwoSemesterTwo = false;
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

    public Long getPlanOneId() {
        return planOneId;
    }

    public void setPlanOneId(final Long planOneId) {
        this.planOneId = planOneId;
    }

    public boolean isPlanOneSemesterOne() {
        return planOneSemesterOne;
    }

    public void setPlanOneSemesterOne(final boolean planOneSemesterOne) {
        this.planOneSemesterOne = planOneSemesterOne;
    }

    public boolean isPlanOneSemesterTwo() {
        return planOneSemesterTwo;
    }

    public void setPlanOneSemesterTwo(final boolean planOneSemesterTwo) {
        this.planOneSemesterTwo = planOneSemesterTwo;
    }

    public Long getPlanTwoId() {
        return planTwoId;
    }

    public void setPlanTwoId(final Long planTwoId) {
        this.planTwoId = planTwoId;
    }

    public boolean isPlanTwoSemesterOne() {
        return planTwoSemesterOne;
    }

    public void setPlanTwoSemesterOne(final boolean planTwoSemesterOne) {
        this.planTwoSemesterOne = planTwoSemesterOne;
    }

    public boolean isPlanTwoSemesterTwo() {
        return planTwoSemesterTwo;
    }

    public void setPlanTwoSemesterTwo(final boolean planTwoSemesterTwo) {
        this.planTwoSemesterTwo = planTwoSemesterTwo;
    }

    public String getPlanOneTitle() {
        return planOneTitle;
    }

    public void setPlanOneTitle(final String planOneTitle) {
        this.planOneTitle = planOneTitle;
    }

    public String getPlanTwoTitle() {
        return planTwoTitle;
    }

    public void setPlanTwoTitle(final String planTwoTitle) {
        this.planTwoTitle = planTwoTitle;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ParticipantDTO)) return false;

        ParticipantDTO that = (ParticipantDTO) o;

        return new EqualsBuilder()
                .append(getPersonId(), that.getPersonId())
                .append(getOwnerId(), that.getOwnerId())
                .append(getEventId(), that.getEventId())
                .append(isPlanOneSemesterOne(), that.isPlanOneSemesterOne())
                .append(isPlanOneSemesterTwo(), that.isPlanOneSemesterTwo())
                .append(isPlanTwoSemesterOne(), that.isPlanTwoSemesterOne())
                .append(isPlanTwoSemesterTwo(), that.isPlanTwoSemesterTwo())
                .append(getId(), that.getId())
                .append(getDate(), that.getDate())
                .append(getTime(), that.getTime())
                .append(getPersonName(), that.getPersonName())
                .append(getOwnerName(), that.getOwnerName())
                .append(getEventTitle(), that.getEventTitle())
                .append(getEventDescription(), that.getEventDescription())
                .append(getPlanOneId(), that.getPlanOneId())
                .append(getPlanTwoId(), that.getPlanTwoId())
                .append(getPlanOneTitle(), that.getPlanOneTitle())
                .append(getPlanTwoTitle(), that.getPlanTwoTitle())
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
                .append(getPlanOneId())
                .append(isPlanOneSemesterOne())
                .append(isPlanOneSemesterTwo())
                .append(getPlanTwoId())
                .append(isPlanTwoSemesterOne())
                .append(isPlanTwoSemesterTwo())
                .append(getPlanOneTitle())
                .append(getPlanTwoTitle())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParticipantDTO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("date=" + date)
                .add("time=" + time)
                .add("personName='" + personName + "'")
                .add("ownerName='" + ownerName + "'")
                .add("eventTitle='" + eventTitle + "'")
                .add("eventDescription='" + eventDescription + "'")
                .add("personId=" + personId)
                .add("ownerId=" + ownerId)
                .add("eventId=" + eventId)
                .add("planOneId=" + planOneId)
                .add("planOneSemesterOne=" + planOneSemesterOne)
                .add("planOneSemesterTwo=" + planOneSemesterTwo)
                .add("planTwoId=" + planTwoId)
                .add("planTwoSemesterOne=" + planTwoSemesterOne)
                .add("planTwoSemesterTwo=" + planTwoSemesterTwo)
                .add("planOneTitle='" + planOneTitle + "'")
                .add("planTwoTitle='" + planTwoTitle + "'")
                .toString();
    }
}
