package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "participant")
public class Participant implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event")
    private ScheduleEvent event;

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
    public void setUser(User user) {
        this.user = user;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    // use ScheduleEvent class addParticipant() method
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
}
