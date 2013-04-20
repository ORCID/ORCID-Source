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

import org.orcid.persistence.jpa.entities.keys.PatentSourceEntityPk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 2011-2012 - ORCID.
 *
 * @author Declan Newman (declan)
 *         Date: 07/08/2012
 */
@Entity
@Table(name = "patent_source")
@IdClass(PatentSourceEntityPk.class)
public class PatentSourceEntity extends BaseEntity<PatentSourceEntityPk> implements Comparable<PatentSourceEntity> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ProfilePatentEntity profilePatent;
    private ProfileEntity sponsorOrcid;
    private Date depositedDate;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns( { @JoinColumn(name = "patent_id", nullable = false), @JoinColumn(name = "orcid", nullable = false) })
    public ProfilePatentEntity getProfilePatent() {
        return profilePatent;
    }

    public void setProfilePatent(ProfilePatentEntity profilePatent) {
        this.profilePatent = profilePatent;
    }

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_orcid", nullable = true)
    public ProfileEntity getSponsorOrcid() {
        return sponsorOrcid;
    }

    public void setSponsorOrcid(ProfileEntity sponsorOrcid) {
        this.sponsorOrcid = sponsorOrcid;
    }

    @Column(name = "deposited_date")
    public Date getDepositedDate() {
        return depositedDate;
    }

    public void setDepositedDate(Date depositedDate) {
        this.depositedDate = depositedDate;
    }

    @Override
    public int compareTo(PatentSourceEntity sponsorEntity) {
        //TODO: implement
        return 0; //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @return always null as we're using a composite key
     */
    @Transient
    @Override
    public PatentSourceEntityPk getId() {
        return null;
    }
}
