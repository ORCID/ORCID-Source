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
import org.orcid.core.manager.WorkManager;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.springframework.cache.annotation.Cacheable;

public class ActivityCacheManagerImpl extends Object implements ActivityCacheManager {
    
    @Resource
    private PeerReviewManager peerReviewManager;
    
    @Resource
    private WorkManager workManager;

    @Cacheable(value = "pub-min-works-maps", key = "#profile.getCacheKey()")
    public LinkedHashMap<Long, WorkForm> pubMinWorksMap(OrcidProfile profile) {
        LinkedHashMap<Long, WorkForm> workMap = new LinkedHashMap<>();
        List<Work> works = workManager.findPublicWorks(profile.getOrcidIdentifier().getPath());
        if (works != null) {
            for (Work work : works) {                
                workMap.put(work.getPutCode(), WorkForm.valueOf(work));                
            }                                
        }
        return workMap;
    }
    
    @Cacheable(value = "pub-peer-reviews-maps", key = "#orcid.concat('-').concat(#lastModified)")
    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid, long lastModified) {
        List<PeerReview> peerReviews = peerReviewManager.findPeerReviews(orcid, lastModified);
        LinkedHashMap<Long, PeerReview> peerReviewMap = new LinkedHashMap<>();
        if (peerReviews != null) {
            if (!peerReviews.isEmpty()) {                
                for(PeerReview peerReview : peerReviews) {
                    if(peerReview.getVisibility().equals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC)) {
                        peerReviewMap.put(peerReview.getPutCode(), peerReview);
                    }
                }
            }
        }
        return peerReviewMap;
    }
    
    @Cacheable(value = "pub-funding-maps", key = "#profile.getCacheKey()")
    public LinkedHashMap<Long, Funding> fundingMap(OrcidProfile profile) {
        LinkedHashMap<Long, Funding> fundingMap = new LinkedHashMap<>();
        if (profile.getOrcidActivities() != null) {
            if (profile.getOrcidActivities().getFundings() != null) {
                for (Funding funding : profile.getOrcidActivities().getFundings().getFundings())
                    if (Visibility.PUBLIC.equals(funding.getVisibility()))
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
        if (profile != null) {            
            if (StringUtils.isNotBlank(profile.getCreditName())) {
                return profile.getCreditName();
            } else {
                String givenName = profile.getGivenNames();
                String familyName = profile.getFamilyName();
                String composedCreditName = (PojoUtil.isEmpty(givenName) ? "" : givenName) + " " + (PojoUtil.isEmpty(familyName) ? "" : familyName);
                return composedCreditName;
            }
            
        }

        return null;
    }
    
    @Cacheable(value = "pub-credit-name", key = "#profile.getCacheKey()")
    public String getPublicCreditName(ProfileEntity profile) {
        if(profile != null) {
            if (Visibility.PUBLIC.equals(profile.getNamesVisibility()) && StringUtils.isNotBlank(profile.getCreditName())) {
                String publicCreditName = null;
                if(!PojoUtil.isEmpty(profile.getCreditName())) {
                    publicCreditName = profile.getCreditName();
                } else {
                    String givenName = profile.getGivenNames();
                    String familyName = profile.getFamilyName();
                    publicCreditName = (PojoUtil.isEmpty(givenName) ? "" : givenName) + " " + (PojoUtil.isEmpty(familyName) ? "" : familyName);
                }
                return publicCreditName;
            } 
        }
        
        return null;
    }
    
}
