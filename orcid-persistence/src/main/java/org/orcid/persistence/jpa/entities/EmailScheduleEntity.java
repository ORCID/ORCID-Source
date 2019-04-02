package org.orcid.persistence.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "email_schedule")
public class EmailScheduleEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    private Date scheduleStart;
    
    private Date scheduleEnd;
    
    private Long scheduleInterval;
    
    private Date latestSent;
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "email_schedule_seq")
    @SequenceGenerator(name = "email_schedule_seq", sequenceName = "email_schedule_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "schedule_start")
    public Date getScheduleStart() {
        return scheduleStart;
    }

    public void setScheduleStart(Date scheduleStart) {
        this.scheduleStart = scheduleStart;
    }

    @Column(name = "schedule_end")
    public Date getScheduleEnd() {
        return scheduleEnd;
    }

    public void setScheduleEnd(Date scheduleEnd) {
        this.scheduleEnd = scheduleEnd;
    }

    @Column(name = "schedule_interval")
    public Long getScheduleInterval() {
        return scheduleInterval;
    }

    public void setScheduleInterval(Long scheduleInterval) {
        this.scheduleInterval = scheduleInterval;
    }
    
    @Column(name = "latest_sent")
    public Date getLatestSent() {
        return latestSent;
    }

    public void setLatestSent(Date latestSent) {
        this.latestSent = latestSent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        EmailScheduleEntity that = (EmailScheduleEntity) o;

        if (!id.equals(that.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 * id.hashCode();
        return result;
    }

}
