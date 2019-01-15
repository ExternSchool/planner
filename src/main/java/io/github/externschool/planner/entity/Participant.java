package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Schedule Event Details
 */
@Entity
@Table(name = "participant")
public class Participant implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    @ManyToOne
    @JoinTable(
            name = "user_participant",
            joinColumns = {@JoinColumn(name = "participant_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    private User user;

    @ManyToOne
    @JoinTable(
            name = "schedule_event_participant",
            joinColumns = {@JoinColumn(name = "participant_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "id")})
    private ScheduleEvent event;

    @Column(name = "plan1_id")
    private Long planOneId;

    @Column(name = "plan1_sem1")
    private boolean planOneSemesterOne = false;

    @Column(name = "plan1_sem2")
    private boolean planOneSemesterTwo = false;

    @Column(name = "plan2_id")
    private Long planTwoId;

    @Column(name = "plan2_sem1")
    private boolean planTwoSemesterOne = false;

    @Column(name = "plan2_sem2")
    private boolean planTwoSemesterTwo = false;

    private Participant() {
    }

    public Participant(User user, ScheduleEvent event) {
        user.addParticipant(this);
        event.addParticipant(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    // use User class addParticipant() method
    void setUser(User user) {
        this.user = user;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    // use ScheduleEvent class addParticipant() method
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public Long getPlanOneId() {
        return planOneId;
    }

    public void setPlanOneId(final Long planOneId) {
        this.planOneId = planOneId;
    }

    public Boolean getPlanOneSemesterOne() {
        return planOneSemesterOne;
    }

    public void setPlanOneSemesterOne(final Boolean planOneSemesterOne) {
        this.planOneSemesterOne = planOneSemesterOne;
    }

    public Boolean getPlanOneSemesterTwo() {
        return planOneSemesterTwo;
    }

    public void setPlanOneSemesterTwo(final Boolean planOneSemesterTwo) {
        this.planOneSemesterTwo = planOneSemesterTwo;
    }

    public Long getPlanTwoId() {
        return planTwoId;
    }

    public void setPlanTwoId(final Long planTwoId) {
        this.planTwoId = planTwoId;
    }

    public Boolean getPlanTwoSemesterOne() {
        return planTwoSemesterOne;
    }

    public void setPlanTwoSemesterOne(final Boolean planTwoSemesterOne) {
        this.planTwoSemesterOne = planTwoSemesterOne;
    }

    public Boolean getPlanTwoSemesterTwo() {
        return planTwoSemesterTwo;
    }

    public void setPlanTwoSemesterTwo(final Boolean planTwoSemesterTwo) {
        this.planTwoSemesterTwo = planTwoSemesterTwo;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Participant)) return false;

        Participant that = (Participant) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getPlanOneId(), that.getPlanOneId())
                .append(getPlanOneSemesterOne(), that.getPlanOneSemesterOne())
                .append(getPlanOneSemesterTwo(), that.getPlanOneSemesterTwo())
                .append(getPlanTwoId(), that.getPlanTwoId())
                .append(getPlanTwoSemesterOne(), that.getPlanTwoSemesterOne())
                .append(getPlanTwoSemesterTwo(), that.getPlanTwoSemesterTwo())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getPlanOneId())
                .append(getPlanOneSemesterOne())
                .append(getPlanOneSemesterTwo())
                .append(getPlanTwoId())
                .append(getPlanTwoSemesterOne())
                .append(getPlanTwoSemesterTwo())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Participant.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("planOneId=" + planOneId)
                .add("planOneSemesterOne=" + planOneSemesterOne)
                .add("planOneSemesterTwo=" + planOneSemesterTwo)
                .add("planTwoId=" + planTwoId)
                .add("planTwoSemesterOne=" + planTwoSemesterOne)
                .add("planTwoSemesterTwo=" + planTwoSemesterTwo)
                .toString();
    }
}
