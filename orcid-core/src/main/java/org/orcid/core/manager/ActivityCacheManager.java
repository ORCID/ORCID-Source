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

import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.WorkForm;

public interface ActivityCacheManager {

    public LinkedHashMap<String, WorkForm> pubMinWorksMap(OrcidProfile profile);
    
    public LinkedHashMap<String, PeerReview> pubPeerReviewsMap(String orcid, long lastModified);
    
    public LinkedHashMap<String, Funding> fundingMap(OrcidProfile profile);
    
    public LinkedHashMap<String, Affiliation> affiliationMap(OrcidProfile profile);
    
    public String getCreditName(ProfileEntity profile);
    
    public String getPublicCreditName(ProfileEntity profile);
}
