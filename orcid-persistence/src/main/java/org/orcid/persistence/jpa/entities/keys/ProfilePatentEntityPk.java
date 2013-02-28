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
 * orcid-entities - Dec 6, 2011 - ProfilePatentEntityPk
 * 
 * @author Declan Newman (declan)
 */
public class ProfilePatentEntityPk implements Serializable {

    private static final long serialVersionUID = -6483017414290915958L;

    private String profile;
    private Long patent;

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
     * @return the work
     */
    public Long getPatent() {
        return patent;
    }

    /**
     * @param patent
     *            the patent to set
     */
    public void setPatent(Long patent) {
        this.patent = patent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProfilePatentEntityPk that = (ProfilePatentEntityPk) o;

        if (!profile.equals(that.profile))
            return false;
        if (!patent.equals(that.patent))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = profile.hashCode();
        result = 31 * result + patent.hashCode();
        return result;
    }
}
