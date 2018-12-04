package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.Role;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class ScheduleEventTypeDTO {
    private Long id;
    @NotBlank private String name;
    @Min(1) @Max(50) private Integer amountOfParticipants;
    @NotEmpty private List<Role> owners = new ArrayList<>();
    @NotEmpty private List<Role> participants = new ArrayList<>();

    public ScheduleEventTypeDTO() {}

    public ScheduleEventTypeDTO(final Long id,
                                final String name,
                                final Integer amountOfParticipants,
                                final List<Role> owners,
                                final List<Role> participants) {
        this.id = id;
        this.name = name;
        this.amountOfParticipants = amountOfParticipants;
        this.owners = owners;
        this.participants = participants;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getAmountOfParticipants() {
        return amountOfParticipants;
    }

    public void setAmountOfParticipants(final Integer amountOfParticipants) {
        this.amountOfParticipants = amountOfParticipants;
    }

    public List<Role> getOwners() {
        return owners;
    }

    public void setOwners(final List<Role> owners) {
        this.owners = owners;
    }

    public List<Role> getParticipants() {
        return participants;
    }

    public void setParticipants(final List<Role> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ScheduleEventTypeDTO)) return false;

        ScheduleEventTypeDTO typeDTO = (ScheduleEventTypeDTO) o;

        return new EqualsBuilder()
                .append(getId(), typeDTO.getId())
                .append(getName(), typeDTO.getName())
                .append(getAmountOfParticipants(), typeDTO.getAmountOfParticipants())
                .append(getOwners(), typeDTO.getOwners())
                .append(getParticipants(), typeDTO.getParticipants())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getName())
                .append(getAmountOfParticipants())
                .append(getOwners())
                .append(getParticipants())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("amountOfParticipants", amountOfParticipants)
                .append("owners", owners)
                .append("participants", participants)
                .toString();
    }
}
