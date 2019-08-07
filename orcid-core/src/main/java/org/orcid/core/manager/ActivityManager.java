package org.orcid.core.manager;

import java.util.LinkedHashMap;

import org.orcid.jaxb.model.record_v2.Affiliation;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;

public interface ActivityManager {

    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid);
    
    public LinkedHashMap<Long, Funding> fundingMap(String orcid);
    
    public LinkedHashMap<Long, Affiliation> affiliationMap(String orcid);
    
    public String getCreditName(String orcid);        
    
    public String getPublicCreditName(String orcid);
}
