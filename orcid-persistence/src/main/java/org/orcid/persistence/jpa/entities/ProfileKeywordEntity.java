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

import org.orcid.persistence.jpa.entities.keys.ProfileKeywordEntityPk;

import javax.persistence.CascadeType;
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
 * orcid-entities - Dec 6, 2011 - ProfileInstitutionEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "profile_keyword")
@IdClass(ProfileKeywordEntityPk.class)
public class ProfileKeywordEntity extends BaseEntity<ProfileKeywordEntityPk> implements Comparable<ProfileKeywordEntity> {

    private static final long serialVersionUID = -3187757614938904392L;

    private ProfileKeywordEntityPk id;

    private ProfileEntity profile;
    private String keywordName;

    public ProfileKeywordEntity() {

    }

    public ProfileKeywordEntity(ProfileEntity profile, String keywordName) {
        this.profile = profile;
        this.keywordName = keywordName;
    }

    @Transient
    public ProfileKeywordEntityPk getId() {
        return id;
    }

    /**
     * @return the profile
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "profile_orcid", nullable = false)
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
     * @return the institutionEntity
     */
    @Id
    @Column(name = "keywords_name", length = 255)
    public String getKeyword() {
        return keywordName;
    }

    /**
     * @param keywordName
     *            the institutionEntity to set
     */
    public void setKeyword(String keywordName) {
        this.keywordName = keywordName;
    }

    @Override
    public int compareTo(ProfileKeywordEntity profileKeywordEntity) {
        if (keywordName != null && profileKeywordEntity != null) {
            return keywordName.compareTo(profileKeywordEntity.getKeyword());
        } else {
            return 0;
        }
    }
}
