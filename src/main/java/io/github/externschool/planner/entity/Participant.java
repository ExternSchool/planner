package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinTable(
            name = "user_participant",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "participant_id", referencedColumnName = "id")})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinTable(
            name = "event_participant",
            joinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "participant_id", referencedColumnName = "id")})
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Participant)) return false;

        final Participant that = (Participant) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Participant{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
