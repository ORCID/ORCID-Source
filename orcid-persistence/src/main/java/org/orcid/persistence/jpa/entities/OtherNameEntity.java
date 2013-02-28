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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * <p>
 * Entity to represent the other names a user may wish to store. This is used
 * extensively to attempt to identify works that may be affiliated to this user
 * 
 * orcid-entities - Dec 6, 2011 - OtherNameEntity
 * 
 * @author Declan Newman (declan)
 **/
@Entity
@Table(name = "other_name")
public class OtherNameEntity extends BaseEntity<Long> implements Comparable<OtherNameEntity> {

    private static final long serialVersionUID = -3227122865862310024L;

    private Long id;
    private String displayName;
    private ProfileEntity profile;

    /**
     * @return the id of the other_name
     */
    @Id
    @Column(name = "other_name_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "other_name_seq")
    @SequenceGenerator(name = "other_name_seq", sequenceName = "other_name_seq")
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id of the other_name
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the displayName
     */
    @Column(name = "display_name")
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the profile
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "orcid", nullable = false, updatable = false)
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

    @Override
    public int compareTo(OtherNameEntity otherNameEntity) {
        if (displayName != null && otherNameEntity != null) {
            return displayName.compareTo(otherNameEntity.getDisplayName());
        } else {
            return 0;
        }
    }
}
