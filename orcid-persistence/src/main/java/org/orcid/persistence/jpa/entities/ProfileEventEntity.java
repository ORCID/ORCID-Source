package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 
 * @author Will Simpson
 * 
 */
@Entity
@Table(name = "profile_event")
public class ProfileEventEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String orcid;
    private ProfileEventType type;
    private String comment;

    public ProfileEventEntity() {
    }

    public ProfileEventEntity(String orcid, ProfileEventType type) {
        this.orcid = orcid;
        this.type = type;
    }

    public ProfileEventEntity(String orcid, ProfileEventType type, String comment) {
        this.orcid = orcid;
        this.type = type;
        this.comment = comment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "profile_event_seq")
    @SequenceGenerator(name = "profile_event_seq", sequenceName = "profile_event_seq", allocationSize = 1)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_event_type")
    public ProfileEventType getType() {
        return type;
    }

    public void setType(ProfileEventType type) {
        this.type = type;
    }

    @Basic
    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
