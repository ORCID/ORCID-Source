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
package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * orcid-entities - Dec 6, 2011 - ProfileInstitutionEntityPk
 * 
 * @author Declan Newman (declan)
 */
@Embeddable
public class AffiliationEntityPk implements Serializable {

    private static final long serialVersionUID = -6018201528061665166L;

    private String profile;
    private Long institutionEntity;

    /**
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile
     *            the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * @return the institutionEntity
     */
    public Long getInstitutionEntity() {
        return institutionEntity;
    }

    /**
     * @param institutionEntity
     *            the institutionEntity to set
     */
    public void setInstitutionEntity(Long institutionEntity) {
        this.institutionEntity = institutionEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AffiliationEntityPk)) {
            return false;
        }

        AffiliationEntityPk that = (AffiliationEntityPk) o;

        if (!institutionEntity.equals(that.institutionEntity)) {
            return false;
        }
        if (!profile.equals(that.profile)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = profile.hashCode();
        result = 31 * result + institutionEntity.hashCode();
        return result;
    }
}
