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
package org.orcid.core.manager.impl;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ActivityCacheManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.Work;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.springframework.cache.annotation.Cacheable;

public class ActivityCacheManagerImpl extends Object implements ActivityCacheManager {
    
    @Resource
    private PeerReviewManager peerReviewManager;
    
    @Resource
    private ProfileFundingManager profileFundingManager;
    
    @Resource
    private WorkManager workManager;

    @Cacheable(value = "pub-min-works-maps", key = "#orcid.concat('-').concat(#lastModified)")
    public LinkedHashMap<Long, WorkForm> pubMinWorksMap(String orcid, long lastModified) {
        LinkedHashMap<Long, WorkForm> workMap = new LinkedHashMap<>();
        List<Work> works = workManager.findPublicWorks(orcid, lastModified);
        if (works != null)
            for (Work work : works)                
                workMap.put(work.getPutCode(), WorkForm.valueOf(work));                          
        return workMap;
    }
    
    @Cacheable(value = "pub-peer-reviews-maps", key = "#orcid.concat('-').concat(#lastModified)")
    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid, long lastModified) {
        List<PeerReview> peerReviews = peerReviewManager.findPeerReviews(orcid, lastModified);
        LinkedHashMap<Long, PeerReview> peerReviewMap = new LinkedHashMap<>();
        if (peerReviews != null) {
            if (!peerReviews.isEmpty()) {                
                for(PeerReview peerReview : peerReviews) {
                    if(peerReview.getVisibility().equals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC)) {
                        peerReviewMap.put(peerReview.getPutCode(), peerReview);
                    }
                }
            }
        }
        return peerReviewMap;
    }
    
    @Cacheable(value = "pub-funding-maps", key = "#orcid.concat('-').concat(#lastModified)")
    public LinkedHashMap<Long, Funding> fundingMap(String orcid, long lastModified) {
    	List<Funding> fundings = profileFundingManager.getFundingList(orcid, lastModified);
        LinkedHashMap<Long, Funding> fundingMap = new LinkedHashMap<>();
		if (fundings != null) {
			for (Funding funding : fundings) {
				if (funding.getVisibility().equals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC))
					fundingMap.put(Long.valueOf(funding.getPutCode()), funding);
			}
		}
        return fundingMap;
    }

    @Cacheable(value = "pub-affiliation-maps", key = "#profile.getCacheKey()")
    public LinkedHashMap<Long, Affiliation> affiliationMap(OrcidProfile profile) {
        LinkedHashMap<Long, Affiliation> affiliationMap = new LinkedHashMap<>();
        if (profile.getOrcidActivities() != null) {
            if (profile.getOrcidActivities().getAffiliations() != null) {
                for (Affiliation aff:profile.getOrcidActivities().getAffiliations().getAffiliation())
                    if (Visibility.PUBLIC.equals(aff.getVisibility()))
                        affiliationMap.put(Long.valueOf(aff.getPutCode()), aff);
            }
        }
        return affiliationMap;
    }

    @Cacheable(value = "credit-name", key = "#profile.getCacheKey()")
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
    
    @Cacheable(value = "pub-credit-name", key = "#profile.getCacheKey()")
    public String getPublicCreditName(ProfileEntity profile) {
        String publicCreditName = null;
        if(profile != null) {
            if(profile.getRecordNameEntity() != null && org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC.equals(profile.getRecordNameEntity().getVisibility())) {            
                if(!PojoUtil.isEmpty(profile.getRecordNameEntity().getCreditName())) {
                    publicCreditName = profile.getRecordNameEntity().getCreditName();
                } else {
                    String givenName = profile.getRecordNameEntity().getGivenNames();
                    String familyName = profile.getRecordNameEntity().getFamilyName();
                    publicCreditName = (PojoUtil.isEmpty(givenName) ? "" : givenName) + " " + (PojoUtil.isEmpty(familyName) ? "" : familyName);
                }
            }            
        }
        
        return publicCreditName;
    }
    
}
