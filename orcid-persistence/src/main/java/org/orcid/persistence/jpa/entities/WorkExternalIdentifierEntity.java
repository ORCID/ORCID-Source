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
package org.orcid.persistence.jpa.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.persistence.jpa.entities.keys.PatentExternalIdentifierEntityPk;
import org.orcid.persistence.jpa.entities.keys.WorkExternalIdentifierEntityPk;
import org.orcid.utils.NullUtils;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

/**
 * 2011-2012 - ORCID.
 * 
 * @author Declan Newman (declan) Date: 07/08/2012
 */
@Entity
@Table(name = "work_external_identifier")
@IdClass(WorkExternalIdentifierEntityPk.class)
public class WorkExternalIdentifierEntity extends BaseEntity<WorkExternalIdentifierEntityPk> implements Comparable<WorkExternalIdentifierEntity> {

    private String identifier;
    private WorkExternalIdentifierType identifierType;
    private WorkEntity work;

    /**
     * @return always returns null as this is using the composite key held in
     *         the
     *         {@link org.orcid.persistence.jpa.entities.keys.WorkExternalIdentifierEntityPk}
     */
    @Override
    @Transient
    public WorkExternalIdentifierEntityPk getId() {
        return null;
    }

    @Id
    @Column(name = "identifier", length = 100)
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * This is the identifier type e.g. CrossRef, etc.
     * 
     * @return
     */
    @Id
    @Column(name = "identifier_type", length = 100)
    @Enumerated(EnumType.STRING)
    public WorkExternalIdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(WorkExternalIdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    @Id
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_id")
    public WorkEntity getWork() {
        return work;
    }

    public void setWork(WorkEntity work) {
        this.work = work;
    }

    @Override
    public int compareTo(WorkExternalIdentifierEntity other) {
        if (other == null) {
            return -1;
        }
        return compareTypes(identifierType, other.getIdentifierType());
    }

    private int compareTypes(WorkExternalIdentifierType thisIdentifierType, WorkExternalIdentifierType otherIdentifierType) {
        if (NullUtils.anyNull(thisIdentifierType, otherIdentifierType)) {
            return NullUtils.compareNulls(thisIdentifierType, otherIdentifierType);
        }
        return thisIdentifierType.value().compareTo(otherIdentifierType.value());
    }

}
