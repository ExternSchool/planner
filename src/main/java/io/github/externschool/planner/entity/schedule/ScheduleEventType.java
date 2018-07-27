package io.github.externschool.planner.entity.schedule;

import io.github.externschool.planner.entity.Role;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Entity
@Table(name = "schedule_event_type")
public class ScheduleEventType {

    //TODO need to extract to base entity class and change strategy to sequence
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String name;

    @Column(name = "count_participant")
    private Integer countOfParticipant;

    @ManyToMany
    @JoinTable(
            name = "event_type_creator_role",
            joinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_name", referencedColumnName = "name")})
    private Set<Role> creators = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "event_type_participant_role",
            joinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_name", referencedColumnName = "name")})
    private Set<Role> participants = new HashSet<>();

    public ScheduleEventType() {}

    public ScheduleEventType(final String name, final Integer countOfParticipant) {
        this.name = name;
        this.countOfParticipant = countOfParticipant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCountOfParticipant() {
        return countOfParticipant;
    }

    public void setCountOfParticipant(Integer countOfParticipant) {
        this.countOfParticipant = countOfParticipant;
    }

    public Set<Role> getCreators() {
        return creators;
    }

    public Set<Role> getParticipants() {
        return participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleEventType that = (ScheduleEventType) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "ScheduleEventType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creators=" + creators +
                ", participants=" + participants +
                ", countOfParticipant=" + countOfParticipant +
                '}';
    }
}
