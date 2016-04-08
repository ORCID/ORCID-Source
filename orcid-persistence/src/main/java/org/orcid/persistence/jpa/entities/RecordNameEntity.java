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
@Table(name = "record_name")
public class RecordNameEntity extends BaseEntity<Long> implements ProfileAware {
    private static final long serialVersionUID = -219497844494612167L;
    private Long id;
    private String creditName;
    private String givenNames;
    private String familyName;
    private ProfileEntity profile;
    private Visibility visibility;
    
    /**
     * @return the id of the name
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "record_name_seq")
    @SequenceGenerator(name = "record_name_seq", sequenceName = "record_name_seq")
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
     * @return the creditName
     */
    @Column(name = "credit_name")
    public String getCreditName() {
        return creditName;
    }

    /**
     * @param creditName the creditName to set
     */
    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }
    
    /**
     * @return the givenName
     */
    @Column(name = "given_names")
    public String getGivenNames() {
        return givenNames;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }
    
    /**
     * @return the familyName
     */
    @Column(name = "family_name")
    public String getFamilyName() {
        return familyName;
    }

    /**
     * @param familyName the familyName to set
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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
    
    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
