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
@Table(name = "event")
public class EventEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String clientId;
    private String ip;
    private String eventType;
    private String label;
    private Date dateCreated;

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

    @Column(name = "label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date date) {
        this.dateCreated = date;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, dateCreated, eventType, id, label);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventEntity other = (EventEntity) obj;
        return Objects.equals(clientId, other.clientId) && Objects.equals(dateCreated, other.dateCreated) && Objects.equals(eventType, other.eventType)
                && Objects.equals(id, other.id) && Objects.equals(label, other.label);
    }

}
