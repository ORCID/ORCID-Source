package org.orcid.core.manager.impl;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ActivityManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Affiliation;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;

public class ActivityManagerImpl extends ManagerReadOnlyBaseImpl implements ActivityManager {
    
    @Resource
    private PeerReviewManager peerReviewManager;
    
    @Resource
    private ProfileFundingManager profileFundingManager;
    
    @Resource
    private WorkManager workManager;
    
    @Resource
    private AffiliationsManager affiliationsManager;
    
    @Resource(name = "recordNameManagerReadOnly")
    private RecordNameManagerReadOnly recordNameManagerReadOnly;

    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid) {
        List<PeerReview> peerReviews = peerReviewManager.findPeerReviews(orcid);
        LinkedHashMap<Long, PeerReview> peerReviewMap = new LinkedHashMap<>();
        if (peerReviews != null) {
            if (!peerReviews.isEmpty()) {                
                for(PeerReview peerReview : peerReviews) {
                    if(peerReview.getVisibility().equals(Visibility.PUBLIC)) {
                        peerReviewMap.put(peerReview.getPutCode(), peerReview);
                    }
                }
            }
        }
        return peerReviewMap;
    }
    
    public LinkedHashMap<Long, Funding> fundingMap(String orcid) {
    	List<Funding> fundings = profileFundingManager.getFundingList(orcid);
        LinkedHashMap<Long, Funding> fundingMap = new LinkedHashMap<>();
		if (fundings != null) {
			for (Funding funding : fundings) {
				if (funding.getVisibility().equals(Visibility.PUBLIC))
					fundingMap.put(Long.valueOf(funding.getPutCode()), funding);
			}
		}
        return fundingMap;
    }

    public LinkedHashMap<Long, Affiliation> affiliationMap(String orcid) {
        LinkedHashMap<Long, Affiliation> affiliationMap = new LinkedHashMap<>();
        List<Affiliation> affiliations = affiliationsManager.getAffiliations(orcid);        
        for(Affiliation affiliation : affiliations) {
            if(Visibility.PUBLIC.equals(affiliation.getVisibility())) {
                affiliationMap.put(affiliation.getPutCode(), affiliation);
            }
        }
        return affiliationMap;
    }

    @Override
    public String getCreditName(String orcid) {
        return recordNameManagerReadOnly.fetchDisplayableCreditName(orcid);        
    }
    
    @Override
    public String getPublicCreditName(String orcid) {
        return recordNameManagerReadOnly.fetchDisplayablePublicName(orcid);
    }
    
}
