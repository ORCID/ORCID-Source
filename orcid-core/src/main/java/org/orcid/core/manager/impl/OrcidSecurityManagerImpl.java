package org.orcid.core.manager.impl;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.common_v2.Filterable;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.VisibilityType;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Group;
import org.orcid.jaxb.model.record_v2.GroupableActivity;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
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

    private static final ScopePathType READ_AFFILIATIONS_REQUIRED_SCOPE = ScopePathType.AFFILIATIONS_READ_LIMITED;
    private static final ScopePathType READ_BIO_REQUIRED_SCOPE = ScopePathType.ORCID_BIO_READ_LIMITED;
    private static final ScopePathType READ_FUNDING_REQUIRED_SCOPE = ScopePathType.FUNDING_READ_LIMITED;
    private static final ScopePathType READ_PEER_REVIEWS_REQUIRED_SCOPE = ScopePathType.PEER_REVIEW_READ_LIMITED;
    private static final ScopePathType READ_WORKS_REQUIRED_SCOPE = ScopePathType.ORCID_WORKS_READ_LIMITED;                
    
    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    private OrcidCoreExceptionMapper orcidCoreExceptionMapper;

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
            Object details = authentication.getDetails();
            if (details instanceof OrcidProfileUserDetails) {
                OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) details;
                return userDetails.getAuthorities().contains(OrcidWebRole.ROLE_ADMIN);
            }
        }
        return false;
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
        if (authentication != null && OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
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
    public void checkProfile(String orcid) throws NoResultException, OrcidDeprecatedException, OrcidNotClaimedException, LockedException, DeactivatedException {
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
            
            StringBuffer deprecated = new StringBuffer(baseUrl).append("/").append(profile.getId());
            params.put(OrcidDeprecatedException.DEPRECATED_ORCID, deprecated.toString());
            
            throw new OrcidDeprecatedException(params);
        }

        // Check if the user record is not claimed and not old enough
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

        // Check if the user record is locked
        if (!profile.isAccountNonLocked()) {
            LockedException lockedException = new LockedException();
            StringBuffer orcidId = new StringBuffer(baseUrl).append("/").append(profile.getId());
            lockedException.setOrcid(orcidId.toString());
            throw lockedException;
        }
        
        // Check if the user record is deactivated
        if (profile.getDeactivationDate() != null) {
            DeactivatedException exception = new DeactivatedException();
            exception.setOrcid(orcid);
            throw exception;
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
    public void checkScopes(ScopePathType... requiredScopes) {
        //Verify the client is not a public client
        checkClientType();
        
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
        Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromStrings(authorizationRequest.getScope());
        for (ScopePathType scope : requestedScopes) {
            for (ScopePathType requiredScope : requiredScopes) {
                if (scope.hasScope(requiredScope)) {
                    return;
                }
            }
        }
        throw new OrcidAccessControlException();
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
                if(element instanceof Email) {
                    Email email = (Email) element;
                    checkAndFilter(orcid, email, requiredScope, true);
                } else {
                    checkAndFilter(orcid, element, requiredScope, true);
                }                
            } catch (Exception e) {
                it.remove();
            }
        }
    }

    @Override
    public void checkAndFilter(String orcid, ActivitiesSummary activities) {
        if (activities == null) {
            return;
        }

        // Check the token
        isMyToken(orcid);

        // Educations
        if (activities.getEducations() != null) {
            checkAndFilter(orcid, activities.getEducations().getSummaries(), READ_AFFILIATIONS_REQUIRED_SCOPE, true);
        }

        // Employments
        if (activities.getEmployments() != null) {
            checkAndFilter(orcid, activities.getEmployments().getSummaries(), READ_AFFILIATIONS_REQUIRED_SCOPE, true);
        }

        // Funding
        if (activities.getFundings() != null) {
            Iterator<FundingGroup> groupIt = activities.getFundings().getFundingGroup().iterator();
            while (groupIt.hasNext()) {
                FundingGroup group = groupIt.next();
                // Filter the list of elements
                checkAndFilter(orcid, group.getFundingSummary(), READ_FUNDING_REQUIRED_SCOPE, true);
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
                checkAndFilter(orcid, group.getPeerReviewSummary(), READ_PEER_REVIEWS_REQUIRED_SCOPE, true);
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
                checkAndFilter(orcid, group.getWorkSummary(), READ_WORKS_REQUIRED_SCOPE, true);
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
    public void checkAndFilter(String orcid, PersonalDetails personalDetails) {
        if (personalDetails == null) {
            return;
        }

        // Check the token
        isMyToken(orcid);

        if (personalDetails.getOtherNames() != null) {
            checkAndFilter(orcid, personalDetails.getOtherNames().getOtherNames(), READ_BIO_REQUIRED_SCOPE, true);
        }

        if (personalDetails.getBiography() != null) {
            try {
                checkAndFilter(orcid, personalDetails.getBiography(), READ_BIO_REQUIRED_SCOPE, true);
            } catch (Exception e) {
                personalDetails.setBiography(null);                
            }            
        }

        if (personalDetails.getName() != null) {
            try {
                checkAndFilter(orcid, personalDetails.getName(), READ_BIO_REQUIRED_SCOPE, true);
            } catch (Exception e) {
                personalDetails.setName(null);
            }
        }
    }

    @Override
    public void checkAndFilter(String orcid, Person person) {
        if (person == null) {
            return;
        }

        // Check the token
        isMyToken(orcid);

        if (person.getAddresses() != null) {
            checkAndFilter(orcid, person.getAddresses().getAddress(), READ_BIO_REQUIRED_SCOPE, true);
        }

        if (person.getBiography() != null) {
            try {
                checkAndFilter(orcid, person.getBiography(), READ_BIO_REQUIRED_SCOPE, true);
            } catch (Exception e) {
                person.setBiography(null);
            }
        }

        if (person.getEmails() != null) {
            checkAndFilter(orcid, person.getEmails().getEmails(), READ_BIO_REQUIRED_SCOPE, true);
        }

        if (person.getExternalIdentifiers() != null) {
            checkAndFilter(orcid, person.getExternalIdentifiers().getExternalIdentifiers(), READ_BIO_REQUIRED_SCOPE, true);
        }

        if (person.getKeywords() != null) {
            checkAndFilter(orcid, person.getKeywords().getKeywords(), READ_BIO_REQUIRED_SCOPE, true);
        }

        if (person.getName() != null) {
            try {
                checkAndFilter(orcid, person.getName(), READ_BIO_REQUIRED_SCOPE, true);
            } catch (Exception e) {
                person.setName(null);
            }
        }

        if (person.getOtherNames() != null) {
            checkAndFilter(orcid, person.getOtherNames().getOtherNames(), READ_BIO_REQUIRED_SCOPE, true);
        }

        if (person.getResearcherUrls() != null) {
            checkAndFilter(orcid, person.getResearcherUrls().getResearcherUrls(), READ_BIO_REQUIRED_SCOPE, true);
        }
    }

    @Override
    public void checkAndFilter(String orcid, Record record) {
        if (record == null) {
            return;
        }

        // Check the token
        isMyToken(orcid);

        if (record.getActivitiesSummary() != null) {
            checkAndFilter(orcid, record.getActivitiesSummary());
        }

        if (record.getPerson() != null) {
            checkAndFilter(orcid, record.getPerson());
        }
    }
    
    @Override
    public void checkAndFilter(String orcid, WorkBulk workBulk, ScopePathType scopePathType) {
        isMyToken(orcid);
        
        List<BulkElement> bulkElements = workBulk.getBulk();
        List<BulkElement> filteredElements = new ArrayList<>();
        
        for (int i = 0; i < bulkElements.size(); i++) {
            BulkElement element = bulkElements.get(i);
            if (element instanceof OrcidError) {
                filteredElements.add(element);
                continue;
            }
            
            try {
                checkAndFilter(orcid, (Work) element, scopePathType, true);
                filteredElements.add(element);
            } catch (Exception e) {
                if (e instanceof OrcidUnauthorizedException) {
                    throw e;
                }
                OrcidError error = orcidCoreExceptionMapper.getOrcidError(e);
                filteredElements.add(error);
            }
        }
        workBulk.setBulk(filteredElements);
    }

    @Override
    public void checkClientAccessAndScopes(String orcid, ScopePathType... requiredScopes) {
        // Check the token belongs to the user
        isMyToken(orcid);
        // Check you have the required scopes
        checkScopes(requiredScopes);
    }

    /**
     * Check the permissions of a request over an email.
     * 
     * @param orcid
     *            The user owner of the element
     * @param email
     *            The email to check
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
    public void checkAndFilter(String orcid, Email email, ScopePathType requiredScope) {        
        checkAndFilter(orcid, email, requiredScope, false);
    }
    
    /**
     * Check the permissions of a request over an email. Private
     * implementation that will also include a parameter that indicates if we
     * should check the token or, if it was already checked previously
     * 
     * @param orcid
     *            The user owner of the element
     * @param email
     *            The email to check
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
    private void checkAndFilter(String orcid, Email email, ScopePathType requiredScope, boolean tokenAlreadyChecked) {        
        if (email == null) {
            return;
        }

        // Check the token was issued for this user
        if (!tokenAlreadyChecked) {
            isMyToken(orcid);
        }
        
        try {
            checkScopes(ScopePathType.EMAIL_READ_PRIVATE);
            return;
        } catch(OrcidAccessControlException oace) {
            checkAndFilter(orcid, (VisibilityType) email, READ_BIO_REQUIRED_SCOPE, true);
        }                 
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
        
        //Verify the client is not a public client
        checkClientType();
        
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
    
    private void checkClientType() {
        String clientId = sourceManager.retrieveSourceOrcid();
        
        ClientDetailsEntity client = clientDetailsEntityCacheManager.retrieve(clientId);
        if(client.getClientType() == null ||    ClientType.PUBLIC_CLIENT.equals(client.getClientType())) {
            throw new OrcidUnauthorizedException("The client application is forbidden to perform the action.");
        }
    }
    
    @Override
    public String getOrcidFromToken(){
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication();
        if (oAuth2Authentication == null) {
            throw new OrcidUnauthorizedException("No OAuth2 authentication found");
        }
        
        checkScopes(ScopePathType.AUTHENTICATE);
        
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        if (userAuthentication != null) {
            Object principal = userAuthentication.getPrincipal();
            if (principal instanceof ProfileEntity) {
                ProfileEntity profileEntity = (ProfileEntity) principal;
                return profileEntity.getId();
            } else {
                throw new OrcidUnauthorizedException("Missing user authentication");
            }
        } else {
            throw new IllegalStateException("Non client credential scope found in client request");
        }
    }
}
