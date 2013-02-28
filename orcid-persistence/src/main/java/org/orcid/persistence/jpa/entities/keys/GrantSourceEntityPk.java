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
 * 2011-2012 - ORCID.
 *
 * @author Declan Newman (declan)
 *         Date: 08/08/2012
 */
public class GrantSourceEntityPk implements Serializable {

    private ProfileGrantEntityPk profileGrant;
    private String sponsorOrcid;

    public ProfileGrantEntityPk getProfileGrant() {
        return profileGrant;
    }

    public void setProfileGrant(ProfileGrantEntityPk profileGrant) {
        this.profileGrant = profileGrant;
    }

    public String getSponsorOrcid() {
        return sponsorOrcid;
    }

    public void setSponsorOrcid(String sponsorOrcid) {
        this.sponsorOrcid = sponsorOrcid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GrantSourceEntityPk)) {
            return false;
        }

        GrantSourceEntityPk that = (GrantSourceEntityPk) o;

        if (!profileGrant.equals(that.profileGrant)) {
            return false;
        }
        if (!sponsorOrcid.equals(that.sponsorOrcid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = profileGrant.hashCode();
        result = 31 * result + sponsorOrcid.hashCode();
        return result;
    }
}
