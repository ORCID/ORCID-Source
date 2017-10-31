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
package org.orcid.core.manager;

import java.util.LinkedHashMap;

import org.orcid.jaxb.model.record_v2.Affiliation;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface ActivityManager {

    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid);
    
    public LinkedHashMap<Long, Funding> fundingMap(String orcid);
    
    public LinkedHashMap<Long, Affiliation> affiliationMap(String orcid);
    
    public String getCreditName(ProfileEntity profile);
    
    public String getPublicCreditName(ProfileEntity profile);
}
