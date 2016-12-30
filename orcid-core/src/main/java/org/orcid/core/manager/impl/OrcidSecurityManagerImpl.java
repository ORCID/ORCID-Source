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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.common_rc4.Filterable;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.common_rc4.VisibilityType;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.ExternalIDs;
import org.orcid.jaxb.model.record_rc4.Group;
import org.orcid.jaxb.model.record_rc4.GroupableActivity;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int writeValiditySeconds;

    @Value("${org.orcid.core.claimWaitPeriodDays:10}")
    private int claimWaitPeriodDays;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

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

    /**
     * Checks a record status and throw an exception indicating if the profile
     * have any of the following conditions: - The record is not claimed and is
     * not old enough nor being accessed by its creator - It is locked - It is
     * deprecated - It is deactivated
     * 
     * @throws OrcidDeprecatedException
     *             in case the account is deprecated
     * @throws OrcidNotClaimedException
     *             in case the account is not claimed
     * @throws LockedException
     *             in the case the account is locked
     */
    @Override
    public void checkProfile(String orcid) throws NoResultException, OrcidDeprecatedException, OrcidNotClaimedException, LockedException {
        ProfileEntity profile = null;

        try {
            profile = profileEntityCacheManager.retrieve(orcid);
        } catch (IllegalArgumentException e) {
            throw new NoResultException();
        }

        // Check if the user record is deprecated
        if (profile.getPrimaryRecord() != null) {
            StringBuffer primary = new StringBuffer(baseUrl).append("/").append(profile.getPrimaryRecord().getId());
            Map<String, String> params = new HashMap<String, String>();
            params.put(OrcidDeprecatedException.ORCID, primary.toString());
            if (profile.getDeprecatedDate() != null) {
                XMLGregorianCalendar calendar = DateUtils.convertToXMLGregorianCalendar(profile.getDeprecatedDate());
                params.put(OrcidDeprecatedException.DEPRECATED_DATE, calendar.toString());
            }
            throw new OrcidDeprecatedException(params);
        }

        // Check if the profile is not claimed and not old enough
        if ((profile.getClaimed() == null || Boolean.FALSE.equals(profile.getClaimed())) && !isOldEnough(profile)) {
            // Let the creator access the profile even if it is not claimed and
            // not old enough
            SourceEntity currentSourceEntity = sourceManager.retrieveSourceEntity();

            String profileSource = profile.getSource() == null ? null : profile.getSource().getSourceId();
            String currentSource = currentSourceEntity == null ? null : currentSourceEntity.getSourceId();

            // If the profile doesn't have source or the current source is not
            // the profile source, throw an exception
            if (profileSource == null || !Objects.equals(profileSource, currentSource)) {
                throw new OrcidNotClaimedException();
            }
        }

        // Check if the record is locked
        if (!profile.isAccountNonLocked()) {
            LockedException lockedException = new LockedException();
            lockedException.setOrcid(profile.getId());
            throw lockedException;
        }
    }

    private boolean isOldEnough(ProfileEntity profile) {
        return DateUtils.olderThan(profile.getSubmissionDate(), claimWaitPeriodDays);
    }

    @Override
    public void checkSource(SourceAwareEntity<?> existingEntity) {
        String sourceIdOfUpdater = sourceManager.retrieveSourceOrcid();
        if (sourceIdOfUpdater != null && !(sourceIdOfUpdater.equals(existingEntity.getSourceId()) || sourceIdOfUpdater.equals(existingEntity.getClientSourceId()))) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("activity", "work");
            throw new WrongSourceException(params);
        }
    }

    @Override
    public void checkSource(IdentifierTypeEntity existingEntity) {
        String sourceIdOfUpdater = sourceManager.retrieveSourceOrcid();
        String existingEntitySourceId = existingEntity.getSourceClient() == null ? null : existingEntity.getSourceClient().getId();
        if (!Objects.equals(sourceIdOfUpdater, existingEntitySourceId)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("activity", "work");
            throw new WrongSourceException(params);
        }
    }

    @Override
    public void checkScopes(ScopePathType requiredScope) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
        Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromStrings(authorizationRequest.getScope());
        for (ScopePathType scope : requestedScopes) {
            if (scope.hasScope(requiredScope)) {
                return;
            }
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("requiredScope", requiredScope.value());
        throw new OrcidAccessControlException(params);
    }

    @Override
    public void checkAndFilter(String orcid, Collection<? extends VisibilityType> elements, ScopePathType requiredScope) {
        checkAndFilter(orcid, elements, requiredScope, false);
    }

    private void checkAndFilter(String orcid, Collection<? extends VisibilityType> elements, ScopePathType requiredScope, boolean tokenAlreadyChecked) {
        if (elements == null) {
            return;
        }

        // Check the token
        if (!tokenAlreadyChecked) {
            isMyToken(orcid);
        }

        Iterator<? extends VisibilityType> it = elements.iterator();
        while (it.hasNext()) {
            VisibilityType element = it.next();
            try {
                checkAndFilter(orcid, element, requiredScope, true);
            } catch (Exception e) {
                it.remove();
            }
        }
    }

    @Override
    public void checkAndFilter(String orcid, ActivitiesSummary activities, ScopePathType requiredScope) {
        if (activities == null) {
            return;
        }

        // Check the token
        isMyToken(orcid);

        // Educations
        if (activities.getEducations() != null) {
            checkAndFilter(orcid, activities.getEducations().getSummaries(), requiredScope, true);
        }

        // Employments
        if (activities.getEmployments() != null) {
            checkAndFilter(orcid, activities.getEmployments().getSummaries(), requiredScope, true);
        }

        // Funding
        if (activities.getFundings() != null) {
            Iterator<FundingGroup> groupIt = activities.getFundings().getFundingGroup().iterator();
            while (groupIt.hasNext()) {
                FundingGroup group = groupIt.next();
                // Filter the list of elements
                checkAndFilter(orcid, group.getFundingSummary(), requiredScope, true);
                // Clean external identifiers
                if (group.getFundingSummary().isEmpty()) {
                    groupIt.remove();
                } else {
                    filterExternalIdentifiers(group);
                }
            }
        }

        // PeerReviews
        if (activities.getPeerReviews() != null) {
            Iterator<PeerReviewGroup> groupIt = activities.getPeerReviews().getPeerReviewGroup().iterator();
            while (groupIt.hasNext()) {
                PeerReviewGroup group = groupIt.next();
                // Filter the list of elements
                checkAndFilter(orcid, group.getPeerReviewSummary(), requiredScope, true);
                if (group.getPeerReviewSummary().isEmpty()) {
                    groupIt.remove();
                }
            }
        }

        // Works
        if (activities.getWorks() != null) {
            Iterator<WorkGroup> groupIt = activities.getWorks().getWorkGroup().iterator();
            while (groupIt.hasNext()) {
                WorkGroup group = groupIt.next();
                // Filter the list of elements
                checkAndFilter(orcid, group.getWorkSummary(), requiredScope, true);
                // Clean external identifiers
                if (group.getWorkSummary().isEmpty()) {
                    groupIt.remove();
                } else {
                    filterExternalIdentifiers(group);
                }
            }
        }
    }

    @Override
    public void checkAndFilter(String orcid, PersonalDetails personalDetails, ScopePathType requiredScope) {
        if (personalDetails == null) {
            return;
        }

        // Check the token
        isMyToken(orcid);

        if (personalDetails.getOtherNames() != null) {
            checkAndFilter(orcid, personalDetails.getOtherNames().getOtherNames(), requiredScope, true);
        }

        if (personalDetails.getBiography() != null) {
            try {
                checkAndFilter(orcid, personalDetails.getBiography(), requiredScope, true);
            } catch (Exception e) {
                personalDetails.setBiography(null);
            }
        }

        if (personalDetails.getName() != null) {
            try {
                checkAndFilter(orcid, personalDetails.getName(), requiredScope, true);
            } catch (Exception e) {
                personalDetails.setName(null);
            }
        }
    }

    @Override
    public void checkAndFilter(String orcid, Person person, ScopePathType requiredScope) {
        if (person == null) {
            return;
        }

        // Check the token
        isMyToken(orcid);

        if (person.getAddresses() != null) {
            checkAndFilter(orcid, person.getAddresses().getAddress(), requiredScope, true);
        }

        if (person.getBiography() != null) {
            try {
                checkAndFilter(orcid, person.getBiography(), requiredScope, true);
            } catch (Exception e) {
                person.setBiography(null);
            }
        }

        if (person.getEmails() != null) {
            checkAndFilter(orcid, person.getEmails().getEmails(), requiredScope, true);
        }

        if (person.getExternalIdentifiers() != null) {
            checkAndFilter(orcid, person.getExternalIdentifiers().getExternalIdentifiers(), requiredScope, true);
        }

        if (person.getKeywords() != null) {
            checkAndFilter(orcid, person.getKeywords().getKeywords(), requiredScope, true);
        }

        if (person.getName() != null) {
            try {
                checkAndFilter(orcid, person.getName(), requiredScope, true);
            } catch (Exception e) {
                person.setName(null);
            }
        }

        if (person.getOtherNames() != null) {
            checkAndFilter(orcid, person.getOtherNames().getOtherNames(), requiredScope, true);
        }

        if (person.getResearcherUrls() != null) {
            checkAndFilter(orcid, person.getResearcherUrls().getResearcherUrls(), requiredScope, true);
        }
    }

    @Override
    public void checkAndFilter(String orcid, Record record, ScopePathType requiredScope) {
        if (record == null) {
            return;
        }

        // Check the token
        isMyToken(orcid);

        if (record.getActivitiesSummary() != null) {
            checkAndFilter(orcid, record.getActivitiesSummary(), requiredScope);
        }

        if (record.getPerson() != null) {
            checkAndFilter(orcid, record.getPerson(), requiredScope);
        }
    }

    @Override
    public void checkClientAccessAndScopes(String orcid, ScopePathType requiredScope) {
        // Check the token belongs to the user
        isMyToken(orcid);
        // Check you have the required scopes
        checkScopes(requiredScope);
    }

    /**
     * Check the permissions of a request over an element.
     * 
     * @param orcid
     *            The user owner of the element
     * @param element
     *            The element to check
     * @param requiredScope
     *            The required scope to access this element
     * @throws OrcidUnauthorizedException
     *             In case the token used was not issued for the owner of the
     *             element
     * @throws OrcidAccessControlException
     *             In case the request doesn't have the required scopes
     * @throws OrcidVisibilityException
     *             In case the element is not visible due the visibility
     */
    @Override
    public void checkAndFilter(String orcid, VisibilityType element, ScopePathType requiredScope) {
        checkAndFilter(orcid, element, requiredScope, false);
    }

    /**
     * Check the permissions of a request over an element. Private
     * implementation that will also include a parameter that indicates if we
     * should check the token or, if it was already checked previously
     * 
     * @param orcid
     *            The user owner of the element
     * @param element
     *            The element to check
     * @param requiredScope
     *            The required scope to access this element
     * @param tokenAlreadyChecked
     *            Indicates if the token was already checked previously, so, we
     *            don't expend time checking it again
     * @throws OrcidUnauthorizedException
     *             In case the token used was not issued for the owner of the
     *             element
     * @throws OrcidAccessControlException
     *             In case the request doesn't have the required scopes
     * @throws OrcidVisibilityException
     *             In case the element is not visible due the visibility
     */
    private void checkAndFilter(String orcid, VisibilityType element, ScopePathType requiredScope, boolean tokenAlreadyChecked) {
        if (element == null) {
            return;
        }

        // Check the token was issued for this user
        if (!tokenAlreadyChecked) {
            isMyToken(orcid);
        }

        // Check if the client is the source of the element
        if (element instanceof Filterable) {
            Filterable filterable = (Filterable) element;
            OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
            if (oAuth2Authentication != null) {
                OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
                String clientId = authorizationRequest.getClientId();
                if (clientId.equals(filterable.retrieveSourcePath())) {
                    // The client doing the request is the source of the element
                    return;
                }
            }
        }

        // Check if the element is public and the token contains the
        // /read-public scope
        if (Visibility.PUBLIC.equals(element.getVisibility())) {
            try {
                checkScopes(ScopePathType.READ_PUBLIC);
                // This means it have ScopePathType.READ_PUBLIC scope, so, we
                // can return it
                return;
            } catch (OrcidAccessControlException e) {
                // Just continue filtering
            }
        }

        // Filter
        filter(element, requiredScope);
    }

    /**
     * Filter the group external identifiers to match the external identifiers
     * that belongs to the activities it have after filtering
     * 
     * @param group
     *            The group we want to filter the external identifiers
     */
    private void filterExternalIdentifiers(Group group) {
        // Iterate over every external identifier and check if it is still
        // present in the list of filtered elements
        ExternalIDs extIds = group.getIdentifiers();
        Iterator<ExternalID> extIdsIt = extIds.getExternalIdentifier().iterator();
        while (extIdsIt.hasNext()) {
            ExternalID extId = extIdsIt.next();
            boolean found = false;
            for (GroupableActivity summary : group.getActivities()) {
                if (summary.getExternalIdentifiers() != null) {
                    if (summary.getExternalIdentifiers().getExternalIdentifier().contains(extId)) {
                        found = true;
                        break;
                    }
                }
            }
            // If the ext id is not found, remove it from the list of ext ids
            if (!found) {
                extIdsIt.remove();
            }
        }
    }

    private void filter(VisibilityType element, ScopePathType requiredScope) {
        // Check the request have the required scope
        checkScopes(requiredScope);

        if (requiredScope.isReadOnlyScope()) {
            if (Visibility.PRIVATE.equals(element.getVisibility())) {
                throw new OrcidVisibilityException();
            }
        } else {
            throw new IllegalArgumentException("Only 'read-only' scopes are allowed");
        }
    }

    private boolean isNonClientCredentialScope(OAuth2Authentication oAuth2Authentication) {
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
        Set<String> requestedScopes = ScopePathType.getCombinedScopesFromStringsAsStrings(authorizationRequest.getScope());
        for (String scopeName : requestedScopes) {
            ScopePathType scopePathType = ScopePathType.fromValue(scopeName);
            if (!scopePathType.isClientCreditalScope()) {
                return true;
            }
        }
        return false;
    }

    private boolean clientIsProfileSource(String clientId, ProfileEntity profile) {
        Boolean claimed = profile.getClaimed();
        SourceEntity source = profile.getSource();
        return source != null && (claimed == null || !claimed) && clientId.equals(source.getSourceId());
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

    private void isMyToken(String orcid) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        if (oAuth2Authentication == null) {
            throw new OrcidUnauthorizedException("No OAuth2 authentication found");
        }

        String clientId = sourceManager.retrieveSourceOrcid();
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        if (userAuthentication != null) {
            Object principal = userAuthentication.getPrincipal();
            if (principal instanceof ProfileEntity) {
                ProfileEntity profileEntity = (ProfileEntity) principal;
                if (!orcid.equals(profileEntity.getId())) {
                    throw new OrcidUnauthorizedException("Access token is for a different record");
                }
            } else {
                throw new OrcidUnauthorizedException("Missing user authentication");
            }
        } else if (isNonClientCredentialScope(oAuth2Authentication) && !clientIsProfileSource(clientId, profile)) {
            throw new IllegalStateException("Non client credential scope found in client request");
        }
    }
}
