package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.Role;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class ScheduleEventTypeDTO {
    private Long id;
    @NotBlank private String name;
    @Min(1) @Max(50) private Integer countOfParticipant;
    @NotEmpty private List<Role> owners = new ArrayList<>();
    @NotEmpty private List<Role> participants = new ArrayList<>();

    public ScheduleEventTypeDTO() {}

    public ScheduleEventTypeDTO(final Long id,
                                final String name,
                                final Integer countOfParticipant,
                                final List<Role> owners,
                                final List<Role> participants) {
        this.id = id;
        this.name = name;
        this.countOfParticipant = countOfParticipant;
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

    public Integer getCountOfParticipant() {
        return countOfParticipant;
    }

    public void setCountOfParticipant(final Integer countOfParticipant) {
        this.countOfParticipant = countOfParticipant;
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
}
