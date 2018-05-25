package org.orcid.core.manager.v3.impl;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.utils.RecordNameUtils;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ActivityManagerImpl extends Object implements ActivityManager {
    
    @Resource(name = "peerReviewManagerV3")
    private PeerReviewManager peerReviewManager;
    
    @Resource(name = "profileFundingManagerV3")
    private ProfileFundingManager profileFundingManager;
    
    @Resource(name = "workManagerV3")
    private WorkManager workManager;
    
    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;

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
		for (Affiliation affiliation : affiliations) {
			if (Visibility.PUBLIC.equals(affiliation.getVisibility())) {
				affiliationMap.put(affiliation.getPutCode(), affiliation);
			}
		}
		return affiliationMap;
	}

    public String getCreditName(ProfileEntity profile) {
        String creditName = null;
        if (profile != null) {
            if(profile.getRecordNameEntity() != null) {
                if (StringUtils.isNotBlank(profile.getRecordNameEntity().getCreditName())) {
                    creditName = profile.getRecordNameEntity().getCreditName();
                } else {
                    String givenName = profile.getRecordNameEntity().getGivenNames();
                    String familyName = profile.getRecordNameEntity().getFamilyName();
                    String composedCreditName = (PojoUtil.isEmpty(givenName) ? "" : givenName) + " " + (PojoUtil.isEmpty(familyName) ? "" : familyName);
                    creditName = composedCreditName;
                }
            }                        
        }
                                          
        return creditName;
    }
    
    public String getPublicCreditName(ProfileEntity profile) {
        String publicCreditName = null;
        if(profile != null && profile.getRecordNameEntity() != null) {
            publicCreditName = RecordNameUtils.getPublicName(profile.getRecordNameEntity());        
        }
        
        return publicCreditName;
    }
    
}
