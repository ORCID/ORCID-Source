package org.orcid.core.manager.v3;

import java.util.LinkedHashMap;

import org.orcid.jaxb.model.v3.dev1.record.Affiliation;
import org.orcid.jaxb.model.v3.dev1.record.Funding;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface ActivityManager {

    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid);
    
    public LinkedHashMap<Long, Funding> fundingMap(String orcid);
    
    public LinkedHashMap<Long, Affiliation> affiliationMap(String orcid);
    
    public String getCreditName(ProfileEntity profile);
    
    public String getPublicCreditName(ProfileEntity profile);
}
