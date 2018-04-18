package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "profile_history_event")
public class ProfileHistoryEventEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    private String eventType;
    
    private String comment;
    
    private String orcid;
    
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "profile_history_event_seq")
    @SequenceGenerator(name = "profile_history_event_seq", sequenceName = "profile_history_event_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "event_type", length = 50)
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Column
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
}
