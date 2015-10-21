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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.jaxb.model.common.Filterable;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Email;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.ResearcherUrl;
import org.orcid.jaxb.model.record.Work;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    public void checkVisibility(Filterable filterable) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        // If it is null, it might be a call from the public API
        Set<String> readLimitedScopes = new HashSet<String>();
        Visibility visibility = filterable.getVisibility();
        String clientId = null;

        if (oAuth2Authentication != null) {
            OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
            clientId = authorizationRequest.getClientId();
            readLimitedScopes = getReadLimitedScopesThatTheClientHas(authorizationRequest, filterable);
        }

        if (readLimitedScopes.isEmpty()) {
            // This client only has permission for read public
            if ((visibility == null || Visibility.PRIVATE.equals(visibility)) && clientId != null && !clientId.equals(filterable.retrieveSourcePath())) {
                throw new OrcidVisibilityException();
            }
            if (visibility.isMoreRestrictiveThan(Visibility.PUBLIC)) {
                throw new OrcidUnauthorizedException("The activity is not public");
            }
        } else {
            // The client has permission for read limited
            if ((visibility == null || Visibility.PRIVATE.equals(visibility)) && clientId != null && !clientId.equals(filterable.retrieveSourcePath())) {
                throw new OrcidVisibilityException();
            }
        }
    }

    @Override
    public void checkSource(SourceEntity existingSource) {
        String sourceIdOfUpdater = sourceManager.retrieveSourceOrcid();
        if (sourceIdOfUpdater != null && (existingSource == null || !sourceIdOfUpdater.equals(existingSource.getSourceId()))) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("activity", "work");
        	throw new WrongSourceException(params);
        }
    }

    @Override
    public boolean isAdmin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof OrcidProfileUserDetails) {
                OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) principal;
                return OrcidType.ADMIN.equals(userDetails.getOrcidType());
            }
        }
        return false;
    }

    private Set<String> getReadLimitedScopesThatTheClientHas(OAuth2Request authorizationRequest, Filterable filterable) {
        Set<String> requestedScopes = ScopePathType.getCombinedScopesFromStringsAsStrings(authorizationRequest.getScope());
        Set<String> readLimitedScopes = new HashSet<>();
        readLimitedScopes.add(ScopePathType.ACTIVITIES_READ_LIMITED.value());
        readLimitedScopes.add(ScopePathType.ORCID_PROFILE_READ_LIMITED.value());
        if (filterable instanceof Work) {
            readLimitedScopes.add(ScopePathType.ORCID_WORKS_READ_LIMITED.value());
        } else if (filterable instanceof Funding) {
            readLimitedScopes.add(ScopePathType.FUNDING_READ_LIMITED.value());
        } else if (filterable instanceof Education || filterable instanceof Employment) {
            readLimitedScopes.add(ScopePathType.AFFILIATIONS_READ_LIMITED.value());
        } else if (filterable instanceof PeerReview) {
            readLimitedScopes.add(ScopePathType.PEER_REVIEW_READ_LIMITED.value());
        } else if (filterable instanceof ResearcherUrl || filterable instanceof Email) {
            readLimitedScopes.add(ScopePathType.PERSON_READ_LIMITED.value());
        }
        readLimitedScopes.retainAll(requestedScopes);
        return readLimitedScopes;
    }

    private OAuth2Authentication getOAuth2Authentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        // if authentication is null, it might be a call from the public api,
        // so, return null
        if (authentication == null)
            return null;
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            return oAuth2Authentication;
        } else {
            for (GrantedAuthority grantedAuth : authentication.getAuthorities()) {
                if ("ROLE_ANONYMOUS".equals(grantedAuth.getAuthority())) {
                    // Assume that anonymous authority is like not having
                    // authority at all
                    return null;
                }
            }

            throw new AccessControlException("Cannot access method with authentication type " + authentication != null ? authentication.toString() : ", as it's null!");
        }
    }
    
    @Override
    public String getClientIdFromAPIRequest() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            OAuth2Request request = oAuth2Authentication.getOAuth2Request();
            return request.getClientId();
        } 
        return null;
    }

}
