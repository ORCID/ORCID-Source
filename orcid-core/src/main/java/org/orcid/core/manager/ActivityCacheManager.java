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
import org.orcid.pojo.ajaxForm.WorkForm;

public interface ActivityCacheManager {

    public LinkedHashMap<Long, WorkForm> pubMinWorksMap(String orcid, long lastModified);
    
    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid, long lastModified);
    
    public LinkedHashMap<Long, Funding> fundingMap(String orcid, long lastModified);
    
    public LinkedHashMap<Long, Affiliation> affiliationMap(String orcid, long lastModified);
    
    public String getCreditName(ProfileEntity profile);
    
    public String getPublicCreditName(ProfileEntity profile);
}
