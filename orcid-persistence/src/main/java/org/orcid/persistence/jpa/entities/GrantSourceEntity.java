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

import org.orcid.persistence.jpa.entities.keys.GrantSourceEntityPk;

import javax.persistence.CascadeType;
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
@Table(name = "grant_source")
@IdClass(GrantSourceEntityPk.class)
public class GrantSourceEntity extends BaseEntity<GrantSourceEntityPk> implements Comparable<GrantSourceEntity> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ProfileGrantEntity profileGrant;
    private ProfileEntity sponsorOrcid;
    private Date depositedDate;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns( { @JoinColumn(name = "grant_id", nullable = false), @JoinColumn(name = "orcid", nullable = false) })
    public ProfileGrantEntity getProfileGrant() {
        return profileGrant;
    }

    public void setProfileGrant(ProfileGrantEntity profileGrant) {
        this.profileGrant = profileGrant;
    }

    @Id
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
    @JoinColumn(name = "source_orcid", nullable = false, updatable = false, insertable = false)
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
    public int compareTo(GrantSourceEntity sponsorEntity) {
        if (sponsorEntity == null) {
            throw new NullPointerException("Can't compare with null");
        }
        if (sponsorEntity.getProfileGrant() == null) {
            if (profileGrant == null) {
                return 0;
            } else {
                return 1;
            }
        }
        if (profileGrant == null) {
            return -1;
        }
        return profileGrant.compareTo(sponsorEntity.getProfileGrant());
    }

    /**
     * @return always null as we're using a composite key
     */
    @Transient
    @Override
    public GrantSourceEntityPk getId() {
        return null;
    }
}
