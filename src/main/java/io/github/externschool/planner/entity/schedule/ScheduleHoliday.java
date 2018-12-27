package io.github.externschool.planner.entity.schedule;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "schedule_holiday")
public class ScheduleHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    @Column(name = "substitution_date")
    private LocalDate substitutionDate;

    private ScheduleHoliday() {
    }

    public ScheduleHoliday(final LocalDate holidayDate, final LocalDate substitutionDate) {
        this.holidayDate = holidayDate;
        this.substitutionDate = substitutionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(final LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    public LocalDate getSubstitutionDate() {
        return substitutionDate;
    }

    public void setSubstitutionDate(final LocalDate substitutionDate) {
        this.substitutionDate = substitutionDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ScheduleHoliday)) return false;

        ScheduleHoliday holiday = (ScheduleHoliday) o;

        return new EqualsBuilder()
                .append(getId(), holiday.getId())
                .append(getHolidayDate(), holiday.getHolidayDate())
                .append(getSubstitutionDate(), holiday.getSubstitutionDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getHolidayDate())
                .append(getSubstitutionDate())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("holidayDate", holidayDate)
                .append("substitutionDate", substitutionDate)
                .toString();
    }
}
