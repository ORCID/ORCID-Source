/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ActivityCacheManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Work;
import org.springframework.cache.annotation.Cacheable;

public class ActivityCacheManagerImpl extends Object implements ActivityCacheManager {

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Cacheable(value = "pub-min-works-maps", key = "#profile.getCacheKey()")
    public HashMap<String, Work> pubMinWorksMap(OrcidProfile profile) {
        HashMap<String, Work> workMap = new HashMap<String, Work>();
        if (profile.getOrcidActivities() != null) {
            if (profile.getOrcidActivities().getOrcidWorks() != null) {
                for (OrcidWork orcidWork : profile.getOrcidActivities().getOrcidWorks().getOrcidWork()) {
                    if (Visibility.PUBLIC.equals(orcidWork.getVisibility())) {
                        workMap.put(orcidWork.getPutCode(), Work.minimizedValueOf(orcidWork));
                    }
                }
            }
        }
        return workMap;
    }

    @Cacheable(value = "pub-funding-maps", key = "#profile.getCacheKey()")
    public HashMap<String, Funding> fundingMap(OrcidProfile profile) {
        HashMap<String, Funding> fundingMap = new HashMap<String, Funding>();
        if (profile.getOrcidActivities() != null) {
            if (profile.getOrcidActivities().getFundings() != null) {
                for (Funding funding : profile.getOrcidActivities().getFundings().getFundings()) {
                    if (Visibility.PUBLIC.equals(funding.getVisibility())) {
                        fundingMap.put(funding.getPutCode(), funding);
                    }
                }
            }
        }
        return fundingMap;
    }

    @Cacheable(value = "pub-affiliation-maps", key = "#profile.getCacheKey()")
    public HashMap<String, Affiliation> affiliationMap(OrcidProfile profile) {
        HashMap<String, Affiliation> affiliationMap = new HashMap<String, Affiliation>();
        if (profile.getOrcidActivities() != null) {
            if (profile.getOrcidActivities().getAffiliations() != null) {
                affiliationMap = (HashMap<String, Affiliation>) profile.getOrcidActivities().getAffiliations().retrieveActivitiesAsMap();
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
            if (Visibility.PUBLIC.equals(profile.getCreditNameVisibility()) && StringUtils.isNotBlank(profile.getCreditName())) {
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
    
}
