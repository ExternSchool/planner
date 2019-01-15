package io.github.externschool.planner.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.StringJoiner;

public class ParticipantDTO {
    private Long id;

    private Long planOneId;
    private boolean planOneSemesterOne;
    private boolean planOneSemesterTwo;
    private Long planTwoId;
    private boolean planTwoSemesterOne;
    private boolean planTwoSemesterTwo;

    private ParticipantDTO() {
    }

    public ParticipantDTO(final Long id) {
        this.id = id;
        planOneSemesterOne = false;
        planOneSemesterTwo = false;
        planTwoSemesterOne = false;
        planTwoSemesterTwo = false;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
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

        if (!(o instanceof ParticipantDTO)) return false;

        ParticipantDTO that = (ParticipantDTO) o;

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
        return new StringJoiner(", ", ParticipantDTO.class.getSimpleName() + "[", "]")
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
