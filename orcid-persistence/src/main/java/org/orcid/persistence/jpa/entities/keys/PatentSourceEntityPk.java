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
public class PatentSourceEntityPk implements Serializable {

    private ProfilePatentEntityPk profilePatent;
    private String sponsorOrcid;

    public ProfilePatentEntityPk getProfilePatent() {
        return profilePatent;
    }

    public void setProfilePatent(ProfilePatentEntityPk profilePatent) {
        this.profilePatent = profilePatent;
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
        if (!(o instanceof PatentSourceEntityPk)) {
            return false;
        }

        PatentSourceEntityPk that = (PatentSourceEntityPk) o;

        if (!profilePatent.equals(that.profilePatent)) {
            return false;
        }
        if (!sponsorOrcid.equals(that.sponsorOrcid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = profilePatent.hashCode();
        result = 31 * result + sponsorOrcid.hashCode();
        return result;
    }
}
