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

import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.keys.AffiliationEntityPk;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * orcid-entities - Dec 6, 2011 - ProfileInstitutionEntity
 *
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "affiliation")
@IdClass(AffiliationEntityPk.class)
public class AffiliationEntity extends BaseEntity<AffiliationEntityPk> implements Comparable<AffiliationEntity> {

    private static final long serialVersionUID = -3187757614938904392L;

    private AffiliationEntityPk id;

    private ProfileEntity profile;
    private InstitutionEntity institutionEntity;
    private String roleTitle;
    private String departmentName;
    private Date startDate;
    private Date endDate;
    private AffiliationType affiliationType;
    private Visibility affiliationVisibility;
    private Visibility affiliationAddressVisibility;

    @Transient
    public AffiliationEntityPk getId() {
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
     *         the profile to set
     */
    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    /**
     * @return the institutionEntity
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "institution_id", nullable = false)
    public InstitutionEntity getInstitutionEntity() {
        return institutionEntity;
    }

    /**
     * @param institutionEntity
     *         the institutionEntity to set
     */
    public void setInstitutionEntity(InstitutionEntity institutionEntity) {
        this.institutionEntity = institutionEntity;
    }

    /**
     * @return the roleTitle
     */
    @Column(name = "role_title")
    public String getRoleTitle() {
        return roleTitle;
    }

    /**
     * @param roleTitle
     *         the roleTitle to set
     */
    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    @Column(name = "department_name", length = 400)
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * @return the startDate
     */
    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *         the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    @Column(name = "end_date")
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *         the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "affiliation_type", length = 100)
    public AffiliationType getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(AffiliationType affiliationType) {
        this.affiliationType = affiliationType;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "affiliation_details_visibility", length = 20)
    public Visibility getAffiliationVisibility() {
        return affiliationVisibility;
    }

    public void setAffiliationVisibility(Visibility affiliationVisibility) {
        this.affiliationVisibility = affiliationVisibility;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "affiliation_address_visibility", length = 20)
    public Visibility getAffiliationAddressVisibility() {
        return affiliationAddressVisibility;
    }

    public void setAffiliationAddressVisibility(Visibility affiliationAddressVisibility) {
        this.affiliationAddressVisibility = affiliationAddressVisibility;
    }

    @Override
    public int compareTo(AffiliationEntity other) {
        if (other == null) {
            return 1;
        }
        int compareEnds = compareEnds(endDate, other.getEndDate());
        if (compareEnds != 0) {
            return compareEnds;
        }
        int compareStarts = compareStarts(startDate, other.getStartDate());
        if (compareStarts != 0) {
            return compareStarts;
        }
        return compareNames(institutionEntity.getName(), other.getInstitutionEntity().getName());
    }

    private int compareEnds(Date endDate, Date otherEndDate) {
        if (NullUtils.anyNull(endDate, otherEndDate)) {
            return -NullUtils.compareNulls(endDate, otherEndDate);
        }
        return -endDate.compareTo(otherEndDate);
    }

    private int compareStarts(Date startDate, Date otherStartDate) {
        if (NullUtils.anyNull(startDate, otherStartDate)) {
            return NullUtils.compareNulls(startDate, otherStartDate);
        }
        return -startDate.compareTo(otherStartDate);
    }

    private int compareNames(String name, String otherName) {
        if (NullUtils.anyNull(name, otherName)) {
            return NullUtils.compareNulls(name, otherName);
        }
        return name.compareTo(otherName);
    }

}