package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role", referencedColumnName = "name"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "owner")
    private Set<ScheduleEvent> ownEvents = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Participant> participants = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id", unique = true)
    private VerificationKey verificationKey;

    private Boolean enabled;

    public User() {
        super();
        enabled = false;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        enabled = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public VerificationKey getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(final VerificationKey verificationKey) {
        this.verificationKey = verificationKey;
    }

    public void addVerificationKey(VerificationKey verificationKey) {
        this.verificationKey = verificationKey;
        if (verificationKey != null) {
            verificationKey.setUser(this);
        }
    }

    public void removeVerificationKey() {
        if (verificationKey != null) {
            verificationKey.setUser(null);
        }
        this.verificationKey = null;
    }

    public void addOwnEvent(ScheduleEvent event) {
        if (event != null && !ownEvents.contains(event)) {
            this.ownEvents.add(event);
            event.setOwner(this);
        }
    }

    public void removeOwnEvent(ScheduleEvent event) {
        if (event != null && !ownEvents.isEmpty()) {
            this.ownEvents = ownEvents.stream()
                    .filter(e -> !e.getId().equals(event.getId()))
                    .collect(Collectors.toSet());
            event.setOwner(null);
        }
    }

    public Set<ScheduleEvent> getOwnEvents() {
        return Collections.unmodifiableSet(ownEvents);
    }

    public Set<Participant> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setUser(this);
    }

    public void removeParticipant(Participant participant) {
        if (participant != null) {
            participants.remove(participant);
            participant.setUser(null);
        }
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    // private getters to Sets instead of public immutable collection
    private Set<Role> getRolesForHash() {
        return roles;
    }

    private Set<ScheduleEvent> getOwnEventsForHash() {
        return ownEvents;
    }

    private Set<Participant> getParticipantsForHash() {
        return participants;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof User)) return false;

        final User user = (User) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(getId(), user.getId())
                .append(getEmail(), user.getEmail())
                .append(getPassword(), user.getPassword())
                .append(getRolesForHash(), user.getRolesForHash())
                .append(getOwnEventsForHash().size(), user.getOwnEventsForHash().size())
                .append(getParticipantsForHash(), user.getParticipantsForHash())
                .append(getVerificationKey(), user.getVerificationKey())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(getId())
                .append(getEmail())
                .append(getPassword())
                .append(getRolesForHash())
                .append(getOwnEventsForHash().size())
                .append(getParticipantsForHash())
                .append(getVerificationKey())
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", email='").append(email).append('\'');
        sb.append(", roles=").append(getRoles().stream().map(Role::getName).collect(Collectors.joining(",")));
        sb.append(", verificationKey=").append(verificationKey);
        sb.append('}');
        return sb.toString();
    }
}
