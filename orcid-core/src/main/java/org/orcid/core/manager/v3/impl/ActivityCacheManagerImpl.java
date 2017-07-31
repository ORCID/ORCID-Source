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
package org.orcid.core.manager.v3.impl;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.v3.ActivityCacheManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.utils.RecordNameUtils;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Affiliation;
import org.orcid.jaxb.model.v3.dev1.record.Funding;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.springframework.cache.annotation.Cacheable;

public class ActivityCacheManagerImpl extends Object implements ActivityCacheManager {
    
    @Resource(name = "peerReviewManagerV3")
    private PeerReviewManager peerReviewManager;
    
    @Resource(name = "profileFundingManagerV3")
    private ProfileFundingManager profileFundingManager;
    
    @Resource(name = "workManagerV3")
    private WorkManager workManager;
    
    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;

    @Cacheable(value = "pub-min-works-maps", key = "#orcid.concat('-').concat(#lastModified)")
    public LinkedHashMap<Long, WorkForm> pubMinWorksMap(String orcid) {
        LinkedHashMap<Long, WorkForm> workMap = new LinkedHashMap<>();
        List<Work> works = workManager.findPublicWorks(orcid);
        if (works != null)
            for (Work work : works)                
                workMap.put(work.getPutCode(), WorkForm.valueOf(work));                          
        return workMap;
    }
    
    @Cacheable(value = "pub-peer-reviews-maps", key = "#orcid.concat('-').concat(#lastModified)")
    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid, long lastModified) {
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
    
    @Cacheable(value = "pub-funding-maps", key = "#orcid.concat('-').concat(#lastModified)")
    public LinkedHashMap<Long, Funding> fundingMap(String orcid, long lastModified) {
    	List<Funding> fundings = profileFundingManager.getFundingList(orcid, lastModified);
        LinkedHashMap<Long, Funding> fundingMap = new LinkedHashMap<>();
		if (fundings != null) {
			for (Funding funding : fundings) {
				if (funding.getVisibility().equals(Visibility.PUBLIC))
					fundingMap.put(Long.valueOf(funding.getPutCode()), funding);
			}
		}
        return fundingMap;
    }

    @Cacheable(value = "pub-affiliation-maps", key = "#orcid.concat('-').concat(#lastModified)")
    public LinkedHashMap<Long, Affiliation> affiliationMap(String orcid, long lastModified) {
        LinkedHashMap<Long, Affiliation> affiliationMap = new LinkedHashMap<>();
        List<Affiliation> affiliations = affiliationsManager.getAffiliations(orcid);        
        for(Affiliation affiliation : affiliations) {
            if(Visibility.PUBLIC.equals(affiliation.getVisibility())) {
                affiliationMap.put(affiliation.getPutCode(), affiliation);
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
        if(profile != null && profile.getRecordNameEntity() != null) {
            publicCreditName = RecordNameUtils.getPublicName(profile.getRecordNameEntity());        
        }
        
        return publicCreditName;
    }
    
}
