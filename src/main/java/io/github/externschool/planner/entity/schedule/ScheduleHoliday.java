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

    @Column(name = "working_date")
    private LocalDate workingDate;

    private ScheduleHoliday() {
    }

    public ScheduleHoliday(final LocalDate holidayDate, final LocalDate workingDate) {
        this.holidayDate = holidayDate;
        this.workingDate = workingDate;
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

    public LocalDate getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(final LocalDate workingDate) {
        this.workingDate = workingDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ScheduleHoliday)) return false;

        ScheduleHoliday holiday = (ScheduleHoliday) o;

        return new EqualsBuilder()
                .append(getId(), holiday.getId())
                .append(getHolidayDate(), holiday.getHolidayDate())
                .append(getWorkingDate(), holiday.getWorkingDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getHolidayDate())
                .append(getWorkingDate())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("holidayDate", holidayDate)
                .append("workingDate", workingDate)
                .toString();
    }
}
