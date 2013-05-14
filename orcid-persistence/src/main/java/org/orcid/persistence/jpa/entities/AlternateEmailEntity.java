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

import org.orcid.persistence.jpa.entities.keys.AlternateEmailEntityPk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * orcid-entities - Dec 6, 2011 - AlternateEmailEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "alternate_email")
@IdClass(AlternateEmailEntityPk.class)
public class AlternateEmailEntity extends BaseEntity<AlternateEmailEntityPk> implements ProfileAware {

    private static final long serialVersionUID = -3187757614938904392L;

    private AlternateEmailEntityPk id;

    private ProfileEntity profile;
    private String alternateEmail;

    @Transient
    public AlternateEmailEntityPk getId() {
        return id;
    }

    /**
     * @return the profile
     */
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false, updatable = false, insertable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    /**
     * @param profile
     *            the profile to set
     */
    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Id
    @Column(name = "alternate_email", length = 350)
    public String getAlternateEmail() {
        return alternateEmail;
    }

    public void setAlternateEmail(String alternateEmail) {
        this.alternateEmail = alternateEmail;
    }
}