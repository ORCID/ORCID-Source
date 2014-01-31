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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.orcid.jaxb.model.message.Visibility;

/**
 * 
 * @author Will Simpson
 * 
 */
@Entity
@Table(name = "profile")
public class ProfileSummaryEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;
    private String orcid;
    private String givenNames;
    private String familyName;
    private String creditName;
    private Visibility creditNameVisibility;

    public ProfileSummaryEntity() {
        super();
    }

    public ProfileSummaryEntity(String orcid) {
        super();
        this.orcid = orcid;
    }

    @Id
    @Column(name = "orcid", length = 19)
    public String getId() {
        return orcid;
    }

    public void setId(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "given_names", length = 150)
    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    @Column(name = "family_name", length = 150)
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Column(name = "credit_name", length = 150)
    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_name_visibility")
    public Visibility getCreditNameVisibility() {
        return creditNameVisibility;
    }

    public void setCreditNameVisibility(Visibility creditNameVisibility) {
        this.creditNameVisibility = creditNameVisibility;
    }

    @Transient
    public String getDisplayName() {
        if (StringUtils.isNotBlank(creditName) && Visibility.PUBLIC.equals(creditNameVisibility)) {
            return creditName;
        }
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(givenNames)) {
            builder.append(givenNames);
        }
        if (StringUtils.isNotBlank(familyName)) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(familyName);
        }
        return builder.length() > 0 ? builder.toString() : null;
    }

}
