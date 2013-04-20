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
 * orcid-entities - Dec 6, 2011 - ProfileWorkEntityPk
 * 
 * @author Declan Newman (declan)
 */
public class ProfileWorkEntityPk implements Serializable {

    private static final long serialVersionUID = -6483017414290915958L;

    private String profile;
    private Long work;

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
    public Long getWork() {
        return work;
    }

    /**
     * @param work
     *            the work to set
     */
    public void setWork(Long work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProfileWorkEntityPk that = (ProfileWorkEntityPk) o;

        if (!profile.equals(that.profile))
            return false;
        if (!work.equals(that.work))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = profile.hashCode();
        result = 31 * result + work.hashCode();
        return result;
    }
}
