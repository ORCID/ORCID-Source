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

import java.security.AccessControlException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidForbiddenException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.Visibility;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.record.summary.FundingGroup;
import org.orcid.jaxb.model.record.summary.FundingSummary;
import org.orcid.jaxb.model.record.summary.Fundings;
import org.orcid.jaxb.model.record.summary.WorkGroup;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.orcid.jaxb.model.record.summary.Works;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManagerImpl implements OrcidSecurityManager {

    @Resource
    private SourceManager sourceManager;

    @Override
    public void checkVisibility(ActivitiesSummary activities) {
        List<EducationSummary> educations = activities.getEducations();        
        for(EducationSummary education : educations) {
            checkVisibility(education);
        }
                
        List<EmploymentSummary> employments = activities.getEmployments();
            for(EmploymentSummary employment : employments) {
                checkVisibility(employment);
            }
        
        Fundings fundings = activities.getFundings();
        if(fundings != null) {
            List<FundingGroup> fundingGroups = fundings.getFundingGroups();
            for(FundingGroup group : fundingGroups) {
                List<FundingSummary> summaries = group.getFundingSummary();
                for(FundingSummary summary : summaries) {
                    checkVisibility(summary);
                }
            }
        }
        
        Works works = activities.getWorks();
        if(works != null) {
            List<WorkGroup> workGroups = works.getWorkGroup();
            for(WorkGroup group : workGroups) {
                List<WorkSummary> summaries = group.getWorkSummary();
                for(WorkSummary summary : summaries) {
                    checkVisibility(summary);
                }
            }
        }
    }
    
    @Override
    public void checkVisibility(Activity activity) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
        String clientId = authorizationRequest.getClientId();
        Visibility visibility = activity.getVisibility();
        Set<String> readLimitedScopes = getReadLimitedScopesThatTheClientHas(authorizationRequest, activity);
        if (readLimitedScopes.isEmpty()) {
            // This client only has permission for read public
            if ((visibility == null || Visibility.PRIVATE.equals(visibility)) && !clientId.equals(activity.retrieveSourcePath())) {
                throw new OrcidForbiddenException("The activity is private and you are not the source");
            }
            if (visibility.isMoreRestrictiveThan(Visibility.PUBLIC)) {
                throw new OrcidUnauthorizedException("The activity is not public");
            }
        } else {
            // The client has permission for read limited
            if ((visibility == null || Visibility.PRIVATE.equals(visibility)) && !clientId.equals(activity.retrieveSourcePath())) {
                throw new OrcidForbiddenException("The activity is private and you are not the source");
            }
        }
    }
    
    @Override
    public void checkHavePublicVisibility(Activity activity) {
        Visibility visibility = activity.getVisibility();
        if(!Visibility.PUBLIC.equals(visibility)) {
            throw new OrcidUnauthorizedException("The activity is not public");
        }
    }
    
    @Override
    public void checkHavePublicVisibility(ActivitiesSummary activities) {
        List<EducationSummary> educations = activities.getEducations();        
        for(EducationSummary education : educations) {
            checkHavePublicVisibility(education);
        }
                
        List<EmploymentSummary> employments = activities.getEmployments();
            for(EmploymentSummary employment : employments) {
                checkHavePublicVisibility(employment);
            }
        
        Fundings fundings = activities.getFundings();
        if(fundings != null) {
            List<FundingGroup> fundingGroups = fundings.getFundingGroups();
            for(FundingGroup group : fundingGroups) {
                List<FundingSummary> summaries = group.getFundingSummary();
                for(FundingSummary summary : summaries) {
                    checkHavePublicVisibility(summary);
                }
            }
        }
        
        Works works = activities.getWorks();
        if(works != null) {
            List<WorkGroup> workGroups = works.getWorkGroup();
            for(WorkGroup group : workGroups) {
                List<WorkSummary> summaries = group.getWorkSummary();
                for(WorkSummary summary : summaries) {
                    checkHavePublicVisibility(summary);
                }
            }
        }
    }

    @Override
    public void checkSource(SourceEntity existingSource) {
        String sourceIdOfUpdater = sourceManager.retrieveSourceOrcid();
        if (sourceIdOfUpdater != null && (existingSource == null || !sourceIdOfUpdater.equals(existingSource.getSourceId()))) {
            throw new WrongSourceException("You are not the source of the work, so you are not allowed to update it");
        }
    }

    private Set<String> getReadLimitedScopesThatTheClientHas(OAuth2Request authorizationRequest, Activity activity) {
        Set<String> requestedScopes = ScopePathType.getCombinedScopesFromStringsAsStrings(authorizationRequest.getScope());
        Set<String> readLimitedScopes = new HashSet<>();
        readLimitedScopes.add(ScopePathType.ACTIVITIES_READ_LIMITED.value());
        readLimitedScopes.add(ScopePathType.ORCID_PROFILE_READ_LIMITED.value());
        if (activity instanceof Work) {
            readLimitedScopes.add(ScopePathType.ORCID_WORKS_READ_LIMITED.value());
        } else if (activity instanceof Funding) {
            readLimitedScopes.add(ScopePathType.FUNDING_READ_LIMITED.value());
        } else if (activity instanceof Education || activity instanceof Employment) {
            readLimitedScopes.add(ScopePathType.AFFILIATIONS_READ_LIMITED.value());
        }
        readLimitedScopes.retainAll(requestedScopes);
        return readLimitedScopes;
    }

    private OAuth2Authentication getOAuth2Authentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            return oAuth2Authentication;
        } else {
            throw new AccessControlException("Cannot access method with authentication type " + authentication != null ? authentication.toString() : ", as it's null!");
        }
    }
}
