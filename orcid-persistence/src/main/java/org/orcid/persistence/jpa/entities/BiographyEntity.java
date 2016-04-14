/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.common_rc2.Visibility;

/**
 * The persistent class for the name database table.
 * 
 */
@Entity
@Table(name = "biography")
public class BiographyEntity extends BaseEntity<Long> implements ProfileAware {
    /**
     * 
     */
    private static final long serialVersionUID = -7348260374645942620L;
    private Long id;
    private String biography;
    private ProfileEntity profile;
    private Visibility visibility;
    
    /**
     * @return the id of the name
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "biography_seq")
    @SequenceGenerator(name = "biography_seq", sequenceName = "biography_seq")
    public Long getId() {
        return id;
    }

    /**
     * @param id the id of the name
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the profile
     */
    @OneToOne 
    @JoinColumn(name = "orcid")
    public ProfileEntity getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }
    
    @Column(name = "biography")    
    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
