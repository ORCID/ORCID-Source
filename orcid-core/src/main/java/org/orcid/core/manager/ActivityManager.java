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
