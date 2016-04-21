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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.jaxb.model.common_rc2.Filterable;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
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

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int writeValiditySeconds;

    @Override
    public void checkVisibility(Filterable filterable, String orcid) {
        checkIsCorrectUser(orcid);
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        // If it is null, it might be a call from the public API
        Set<String> readLimitedScopes = new HashSet<String>();
        // If it is null, it might be a call from the public API
        Set<String> updateScopes = new HashSet<String>();
        Visibility visibility = filterable.getVisibility();
        String clientId = null;

        if (oAuth2Authentication != null) {
            OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
            clientId = authorizationRequest.getClientId();
            readLimitedScopes = getReadLimitedScopesThatTheClientHas(authorizationRequest, filterable, orcid);
            updateScopes = getUpdateScopesThatTheClientHas(authorizationRequest, filterable, orcid);
        }

        // If we are using a read-limited or update scope and the client is the
        // source of the object, we should not worry about the visibility
        if (!readLimitedScopes.isEmpty() || !updateScopes.isEmpty()) {
            if (!PojoUtil.isEmpty(filterable.retrieveSourcePath())) {
                if (filterable.retrieveSourcePath().equals(clientId)) {
                    return;
                }
            }
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
    public void checkVisibility(Name name, String orcid) {
        checkIsCorrectUser(orcid);
        if (Visibility.PRIVATE.equals(name.getVisibility())) {
            throw new OrcidVisibilityException();
        }
        boolean hasReadLimitedScope = hasScope(ScopePathType.PERSON_READ_LIMITED);
        if (!hasReadLimitedScope) {
            if (Visibility.LIMITED.equals(name.getVisibility())) {
                throw new OrcidUnauthorizedException("You dont have permissions to view this element");
            }
        }
    }

    @Override
    public void checkVisibility(Biography biography, String orcid) {
        checkIsCorrectUser(orcid);
        if (Visibility.PRIVATE.equals(biography.getVisibility())) {
            throw new OrcidVisibilityException();
        }
        boolean hasReadLimitedScope = hasScope(ScopePathType.PERSON_READ_LIMITED);
        if (!hasReadLimitedScope) {
            if (Visibility.LIMITED.equals(biography.getVisibility())) {
                throw new OrcidUnauthorizedException("You dont have permissions to view this element");
            }
        }
    }

    @Override
    public void checkVisibility(OtherName otherName, String orcid) {
        checkIsCorrectUser(orcid);
        if (Visibility.PRIVATE.equals(otherName.getVisibility())) {
            OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
            String clientId = null;

            if (oAuth2Authentication != null) {
                OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
                clientId = authorizationRequest.getClientId();
            }

            if (clientId == null || otherName.getSource() == null || !clientId.equals(otherName.getSource().retrieveSourcePath())) {
                throw new OrcidVisibilityException();
            }
        }
        boolean hasReadLimitedScope = hasScope(ScopePathType.PERSON_READ_LIMITED);
        if (!hasReadLimitedScope) {
            if (Visibility.LIMITED.equals(otherName.getVisibility())) {
                throw new OrcidUnauthorizedException("You dont have permissions to view this element");
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
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof OrcidProfileUserDetails) {
                OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) principal;
                return OrcidType.ADMIN.equals(userDetails.getOrcidType());
            }
        }
        return false;
    }

    @Override
    public boolean isPasswordConfirmationRequired() {
        return sourceManager.isInDelegationMode() && !sourceManager.isDelegatedByAnAdmin();
    }

    private Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            return context.getAuthentication();
        } 
        return null;
    }
    
    private Set<String> getReadLimitedScopesThatTheClientHas(OAuth2Request authorizationRequest, Filterable filterable, String orcid) {
        Set<String> readLimitedScopes = new HashSet<>();
        Set<String> requestedScopes = ScopePathType.getCombinedScopesFromStringsAsStrings(authorizationRequest.getScope());
        readLimitedScopes.add(ScopePathType.READ_LIMITED.value());
        readLimitedScopes.add(ScopePathType.ACTIVITIES_READ_LIMITED.value());
        readLimitedScopes.add(ScopePathType.ORCID_PROFILE_READ_LIMITED.value());
        if (filterable instanceof Work || filterable instanceof WorkSummary) {
            readLimitedScopes.add(ScopePathType.ORCID_WORKS_READ_LIMITED.value());
        } else if (filterable instanceof Funding || filterable instanceof FundingSummary) {
            readLimitedScopes.add(ScopePathType.FUNDING_READ_LIMITED.value());
        } else if (filterable instanceof Education || filterable instanceof Employment || filterable instanceof EducationSummary
                || filterable instanceof EmploymentSummary) {
            readLimitedScopes.add(ScopePathType.AFFILIATIONS_READ_LIMITED.value());
        } else if (filterable instanceof PeerReview || filterable instanceof PeerReviewSummary) {
            readLimitedScopes.add(ScopePathType.PEER_REVIEW_READ_LIMITED.value());
        } else if (filterable instanceof ResearcherUrl || filterable instanceof Email || filterable instanceof Emails || filterable instanceof Address
                || filterable instanceof PersonExternalIdentifier || filterable instanceof Keyword || filterable instanceof OtherName || filterable instanceof Person
                || filterable instanceof Name || filterable instanceof Biography) {
            readLimitedScopes.add(ScopePathType.PERSON_READ_LIMITED.value());
            readLimitedScopes.add(ScopePathType.ORCID_BIO_READ_LIMITED.value());
        }
        readLimitedScopes.retainAll(requestedScopes);
        return readLimitedScopes;
    }

    private Set<String> getUpdateScopesThatTheClientHas(OAuth2Request authorizationRequest, Filterable filterable, String orcid) {
        Set<String> updateScopes = new HashSet<>();
        Set<String> requestedScopes = ScopePathType.getCombinedScopesFromStringsAsStrings(authorizationRequest.getScope());
        updateScopes.add(ScopePathType.ACTIVITIES_UPDATE.value());
        updateScopes.add(ScopePathType.PERSON_UPDATE.value());
        if (filterable instanceof Work || filterable instanceof WorkSummary) {
            updateScopes.add(ScopePathType.ORCID_WORKS_UPDATE.value());
        } else if (filterable instanceof Funding || filterable instanceof FundingSummary) {
            updateScopes.add(ScopePathType.FUNDING_UPDATE.value());
        } else if (filterable instanceof Education || filterable instanceof Employment || filterable instanceof EducationSummary
                || filterable instanceof EmploymentSummary) {
            updateScopes.add(ScopePathType.AFFILIATIONS_UPDATE.value());
        } else if (filterable instanceof PeerReview || filterable instanceof PeerReviewSummary) {
            updateScopes.add(ScopePathType.PEER_REVIEW_UPDATE.value());
        } else if (filterable instanceof ResearcherUrl || filterable instanceof Email || filterable instanceof Address || filterable instanceof PersonExternalIdentifier
                || filterable instanceof Keyword || filterable instanceof OtherName || filterable instanceof Person || filterable instanceof Name
                || filterable instanceof Biography) {
            updateScopes.add(ScopePathType.PERSON_UPDATE.value());
            updateScopes.add(ScopePathType.ORCID_BIO_UPDATE.value());
            if (filterable instanceof PersonExternalIdentifier) {
                updateScopes.add(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE.value());
            }
        }
        updateScopes.retainAll(requestedScopes);
        return updateScopes;
    }

    private void checkIsCorrectUser(String orcid) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        if (oAuth2Authentication != null) {
            Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
            if (userAuthentication != null) {
                Object principal = userAuthentication.getPrincipal();
                if (principal instanceof ProfileEntity) {
                    ProfileEntity profileEntity = (ProfileEntity) principal;
                    if (!orcid.equals(profileEntity.getId())) {
                        throw new OrcidUnauthorizedException("Access token is for a different record");
                    }
                }
            }
        }
        throw new OrcidUnauthorizedException("No user authentication found");
    }

    private OAuth2Authentication getOAuth2Authentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Authentication authentication = context.getAuthentication();
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

                throw new AccessControlException(
                        "Cannot access method with authentication type " + authentication != null ? authentication.toString() : ", as it's null!");
            }
        } else {
            throw new IllegalStateException("No security context found. This is bad!");
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

    private boolean hasScope(ScopePathType scope) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        if (oAuth2Authentication != null) {
            OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
            Set<String> requestedScopes = ScopePathType.getCombinedScopesFromStringsAsStrings(authorizationRequest.getScope());
            if (requestedScopes.contains(scope.value())) {
                return true;
            }
        }

        return false;
    }

    public void checkPermissions(ScopePathType requiredScope, String orcid) {
        if (orcid != null) {
            checkIsCorrectUser(orcid);
        }
        checkScopes(requiredScope, orcid);
    }

    private void checkScopes(ScopePathType requiredScope, String orcid) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
        Set<String> requestedScopes = authorizationRequest.getScope();
        if (requiredScope.isUserGrantWriteScope()) {
            OrcidOAuth2Authentication orcidOauth2Authentication = (OrcidOAuth2Authentication) oAuth2Authentication;
            String activeToken = orcidOauth2Authentication.getActiveToken();
            if (activeToken != null) {
                OrcidOauth2TokenDetail tokenDetail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(activeToken);
                if (removeUserGrantWriteScopePastValitity(tokenDetail)) {
                    throw new AccessControlException("Write scopes for this token have expired ");
                }
            }
        }
        if (!hasScope(requiredScope)) {
            throw new AccessControlException("Insufficient or wrong scope " + requestedScopes);
        }
    }

    public boolean removeUserGrantWriteScopePastValitity(OrcidOauth2TokenDetail tokenDetail) {
        boolean scopeRemoved = false;
        if (tokenDetail != null && tokenDetail.getScope() != null) {
            // Clean the scope if it is not a persistent token
            if (!tokenDetail.isPersistent()) {
                Set<String> scopes = OAuth2Utils.parseParameterList(tokenDetail.getScope());
                List<String> removeScopes = new ArrayList<String>();
                for (String scope : scopes) {
                    if (scope != null && !scope.isEmpty()) {
                        ScopePathType scopePathType = ScopePathType.fromValue(scope);
                        if (scopePathType.isUserGrantWriteScope()) {
                            Date now = new Date();
                            if (now.getTime() > tokenDetail.getDateCreated().getTime() + (writeValiditySeconds * 1000)) {
                                removeScopes.add(scope);
                                scopeRemoved = true;
                            }
                        }
                    }
                }
                if (scopeRemoved) {
                    for (String scope : removeScopes)
                        scopes.remove(scope);
                    tokenDetail.setScope(OAuth2Utils.formatParameterList(scopes));
                    orcidOauthTokenDetailService.saveOrUpdate(tokenDetail);
                    return true;
                }
            }
        }
        return false;
    }
}
