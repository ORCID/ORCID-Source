/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.audit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 24/07/2012
 */
@Entity
@Table(name = "audit_event")
public class AuditEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String recordModifiedOrcid;
    private String recordModifierOrcid;
    private String recordModifierType;
    private String recordModifierIp;
    private String recordModifierIso2Country;
    private AuditEventType eventType;
    private String eventMethod;
    private String eventDescription;
    private Date dateCreated;
    private Date lastModified;

    public AuditEvent() {
    }

    public AuditEvent(String recordModifiedOrcid, String recordModifierOrcid, String recordModifierType, String recordModifierIp, String recordModifierIso2Country,
            AuditEventType eventType, String eventMethod, String eventDescription) {
        this.recordModifiedOrcid = recordModifiedOrcid;
        this.recordModifierOrcid = recordModifierOrcid;
        this.recordModifierType = recordModifierType;
        this.recordModifierIp = recordModifierIp;
        this.recordModifierIso2Country = recordModifierIso2Country;
        this.eventType = eventType;
        this.eventMethod = eventMethod;
        this.eventDescription = eventDescription;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "audit_event_id_seq")
    @SequenceGenerator(name = "audit_event_id_seq", sequenceName = "audit_event_id_seq")
    @Column(name = "audit_event_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "modified_orcid", length = 19)
    public String getRecordModifiedOrcid() {
        return recordModifiedOrcid;
    }

    public void setRecordModifiedOrcid(String recordModifiedOrcid) {
        this.recordModifiedOrcid = recordModifiedOrcid;
    }

    @Column(name = "modifier_orcid", length = 19)
    public String getRecordModifierOrcid() {
        return recordModifierOrcid;
    }

    public void setRecordModifierOrcid(String recordModifierOrcid) {
        this.recordModifierOrcid = recordModifierOrcid;
    }

    @Column(name = "modifier_type", length = 75)
    public String getRecordModifierType() {
        return recordModifierType;
    }

    public void setRecordModifierType(String recordModifierType) {
        this.recordModifierType = recordModifierType;
    }

    @Column(name = "modifier_ip", length = 39)
    public String getRecordModifierIp() {
        return recordModifierIp;
    }

    public void setRecordModifierIp(String recordModifierIp) {
        this.recordModifierIp = recordModifierIp;
    }

    @Column(name = "modifier_iso2_country", length = 2)
    public String getRecordModifierIso2Country() {
        return recordModifierIso2Country;
    }

    public void setRecordModifierIso2Country(String recordModifierIso2Country) {
        this.recordModifierIso2Country = recordModifierIso2Country;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 75)
    public AuditEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuditEventType eventType) {
        this.eventType = eventType;
    }

    @Column(name = "event_method", length = 75)
    public String getEventMethod() {
        return eventMethod;
    }

    public void setEventMethod(String eventMethod) {
        this.eventMethod = eventMethod;
    }

    @Column(name = "event_description", length = 255)
    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * The date that this entity was created.
     *
     * @return the dateCreated
     */
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * This should not be called explicitly as the {@link #updateTimeStamps()}
     * method will be called whenever an update or persist is called
     *
     * @param dateCreated
     *         the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * The date that this entity was last updated. This will be the same as the
     * {@link #getDateCreated} only on the initial creation
     *
     * @return the lastModified
     */
    @Column(name = "last_modified")
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * This should not be called explicitly as the {@link #updateTimeStamps()}
     * method will be called whenever an update or persist is called
     *
     * @param lastModified
     *         the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @PreUpdate
    @PrePersist
    void updateTimeStamps() {
        lastModified = new Date();
        if (dateCreated == null) {
            dateCreated = new Date();
        }
    }
}
