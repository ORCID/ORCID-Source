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

import java.io.Serializable;

/**
 * orcid-entities - Dec 6, 2011 - ProfileGrantEntityPk
 * 
 * @author Declan Newman (declan)
 */
public class ProfileGrantEntityPk implements Serializable {

    private static final long serialVersionUID = -6483017414290915958L;

    private String profile;
    private Long grant;

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
    public Long getGrant() {
        return grant;
    }

    /**
     * @param grant
     *            the grant to set
     */
    public void setGrant(Long grant) {
        this.grant = grant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProfileGrantEntityPk that = (ProfileGrantEntityPk) o;

        if (!profile.equals(that.profile))
            return false;
        if (!grant.equals(that.grant))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = profile.hashCode();
        result = 31 * result + grant.hashCode();
        return result;
    }
}
