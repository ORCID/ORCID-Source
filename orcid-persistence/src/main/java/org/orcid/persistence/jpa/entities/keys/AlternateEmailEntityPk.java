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

import javax.persistence.Embeddable;

/**
 * orcid-entities - Dec 6, 2011 - AlternateEmailEntityPk
 * 
 * @author Declan Newman (declan)
 */
@Embeddable
public class AlternateEmailEntityPk implements Serializable {

    private static final long serialVersionUID = -6018201528061665166L;

    private String profile;
    private String alternateEmail;

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

    public String getAlternateEmail() {
        return alternateEmail;
    }

    public void setAlternateEmail(String alternateEmail) {
        this.alternateEmail = alternateEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlternateEmailEntityPk)) {
            return false;
        }

        AlternateEmailEntityPk that = (AlternateEmailEntityPk) o;

        if (!alternateEmail.equals(that.alternateEmail)) {
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
        result = 31 * result + alternateEmail.hashCode();
        return result;
    }
}
