package org.orcid.persistence.jpa.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author Daniel Palafox
 *
 */
@Entity
@Table(name = "event_stats")
public class EventStatsEntity extends BaseEntity<Long>{
    private Long id;
    private String eventType;
    private String clientId;
    private String ip;
    private Integer count;
    private Date date;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "event_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "event_type")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Column(name = "client_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String client_id) {
        this.clientId = client_id;
    }

    @Column(name = "ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column(name = "count")
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStatsEntity that = (EventStatsEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getEventType(), that.getEventType()) &&
                Objects.equals(getClientId(), that.getClientId()) &&
                Objects.equals(getCount(), that.getCount()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEventType(), getClientId(), getCount(), getDate());
    }
}
