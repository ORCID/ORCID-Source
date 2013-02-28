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
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.keys.ProfilePatentEntityPk;

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
import java.util.Date;
import java.util.Set;

/**
 * orcid-entities - Dec 6, 2011 - ProfileInstitutionEntity
 * 
 * @author Declan Newman (declan)
 */

@Entity
@Table(name = "profile_patent")
@IdClass(ProfilePatentEntityPk.class)
public class ProfilePatentEntity extends BaseEntity<ProfilePatentEntityPk> implements Comparable<ProfilePatentEntity> {

    private static final long serialVersionUID = -3187757614938904392L;

    private ProfileEntity profile;
    private PatentEntity patent;
    private Date addedToProfileDate;
    private Set<PatentSourceEntity> sources;
    private Visibility visibility;

    @Override
    @Transient
    public ProfilePatentEntityPk getId() {
        return null;
    }

    /**
     * @return the profile
     */
    @Id
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false)
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

    /**
     * @return the work
     */
    @Id
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "patent_id", nullable = false)
    public PatentEntity getPatent() {
        return patent;
    }

    /**
     * @param patent
     *            the patent to set
     */
    public void setPatent(PatentEntity patent) {
        this.patent = patent;
    }

    @Column(name = "added_to_profile_date")
    public Date getAddedToProfileDate() {
        return addedToProfileDate;
    }

    public void setAddedToProfileDate(Date addedToProfileDate) {
        this.addedToProfileDate = addedToProfileDate;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "profilePatent")
    @Fetch(FetchMode.SUBSELECT)
    public Set<PatentSourceEntity> getSources() {
        return sources;
    }

    public void setSources(Set<PatentSourceEntity> sources) {
        this.sources = sources;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public int compareTo(ProfilePatentEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }
        if (other.getPatent() == null) {
            if (patent == null) {
                return 0;
            } else {
                return 1;
            }
        }
        if (patent == null) {
            return -1;
        }
        return patent.compareTo(other.getPatent());
    }

}
