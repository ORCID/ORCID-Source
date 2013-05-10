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

import java.util.Comparator;
import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.keys.ProfileWorkEntityPk;

/**
 * orcid-entities - Dec 6, 2011 - ProfileInstitutionEntity
 * 
 * @author Declan Newman (declan)
 */

@Entity
@Table(name = "profile_work")
@IdClass(ProfileWorkEntityPk.class)
public class ProfileWorkEntity extends BaseEntity<ProfileWorkEntityPk> implements Comparable<ProfileWorkEntity> {

    private static final long serialVersionUID = -3187757614938904392L;

    private ProfileEntity profile;
    private ProfileEntity sourceProfile;
    private WorkEntity work;
    private Date addedToProfileDate;    
    private Visibility visibility;

    @Override
    @Transient
    public ProfileWorkEntityPk getId() {
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
     * return the source profile
     * */
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id", nullable = true, updatable=false)
    public ProfileEntity getSourceProfile(){
        return sourceProfile;
    }
    
    /**
     * Set the source to the profile work
     * @param sourceProfile
     * */
    public void setSourceProfile(ProfileEntity sourceProfile){
        this.sourceProfile = sourceProfile;
    }
    
    /**
     * @return the work
     */
    @Id
    @ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_id", nullable = false)
    public WorkEntity getWork() {
        return work;
    }

    /**
     * @param work
     *            the work to set
     */
    public void setWork(WorkEntity work) {
        this.work = work;
    }

    @Column(name = "added_to_profile_date")
    public Date getAddedToProfileDate() {
        return addedToProfileDate;
    }

    public void setAddedToProfileDate(Date addedToProfileDate) {
        this.addedToProfileDate = addedToProfileDate;
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
    public int compareTo(ProfileWorkEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }
        if (other.getWork() == null) {
            if (work == null) {
                return 0;
            } else {
                return 1;
            }
        }
        if (work == null) {
            return -1;
        }
        return work.compareTo(other.getWork());
    }

    public static class ChronologicallyOrderedProfileWorkEntityComparator implements Comparator<ProfileWorkEntity> {
        public int compare(ProfileWorkEntity profileWork1, ProfileWorkEntity profileWork2) {
            if (profileWork2 == null) {
                throw new NullPointerException("Can't compare with null");
            }

            if (profileWork2.getWork() == null) {
                if (profileWork1.getWork() == null) {
                    return 0;
                } else {
                    return 1;
                }
            } else if (profileWork1.getWork() == null) {
                return -1;
            }

            WorkEntity work1 = profileWork1.getWork();
            WorkEntity work2 = profileWork2.getWork();

            WorkEntity.ChronologicallyOrderedWorkEntityComparator workEntityComparator = new WorkEntity.ChronologicallyOrderedWorkEntityComparator();

            return workEntityComparator.compare(work1, work2);
        }
    }

}
