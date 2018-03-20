package org.orcid.core.security.visibility.aop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.security.PermissionChecker;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.VisibilityType;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;

/**
 * @author Declan Newman (declan) Date: 16/03/2012
 */
@Deprecated
@Aspect
@Component
@Order(100)
public class OrcidApiAuthorizationSecurityAspect {

    public static final String CLIENT_ID = "client_id";

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource(name = "visibilityFilter")
    private VisibilityFilter visibilityFilter;

    @Resource(name = "defaultPermissionChecker")
    private PermissionChecker permissionChecker;

    public void setOrcidOauth2TokenDetailDao(OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao) {
        this.orcidOauth2TokenDetailDao = orcidOauth2TokenDetailDao;
    }
    
    @Before("@annotation(accessControl) && (args(uriInfo ,orcid, orcidMessage))")
    public void checkPermissionsWithAll(AccessControl accessControl, UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid, orcidMessage);
    }

    @Before("@annotation(accessControl) && (args(uriInfo, orcidMessage))")
    public void checkPermissionsWithOrcidMessage(AccessControl accessControl, UriInfo uriInfo, OrcidMessage orcidMessage) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcidMessage);

    }

    @Before("@annotation(accessControl) && args(orcid)")
    public void checkPermissionsWithOrcid(AccessControl accessControl, String orcid) {
        Authentication auth = getAuthentication();
        boolean allowAnonymousCall = allowAnonymousAccess(auth, accessControl);
        if(!allowAnonymousCall) {
            permissionChecker.checkPermissions(auth, accessControl.requiredScope(), orcid);
        }
                
    }

    @Before("@annotation(accessControl) && args(orcid, id)")
    public void checkPermissionsWithLongId(AccessControl accessControl, String orcid, Long id) {
        Authentication auth = getAuthentication();
        boolean allowAnonymousCall = allowAnonymousAccess(auth, accessControl);
        if(!allowAnonymousCall) {
            permissionChecker.checkPermissions(auth, accessControl.requiredScope(), orcid);
        }
    }
    
    @Before("@annotation(accessControl) && args(orcid, id)")
    public void checkPermissionsWithId(AccessControl accessControl, String orcid, String id) {
        Authentication auth = getAuthentication();
        boolean allowAnonymousCall = allowAnonymousAccess(auth, accessControl);
        if(!allowAnonymousCall) {
            permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
        }
    }

    @Before("@annotation(accessControl) && args(uriInfo, orcid, notification)")
    public void checkPermissionsWithNotification(AccessControl accessControl, UriInfo uriInfo, String orcid, Notification notification) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }
    
    @Before("@annotation(accessControl) && args(orcid, activity)")
    public void checkPermissionsWithWork(AccessControl accessControl, String orcid, Activity activity) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }
    
    @Before("@annotation(accessControl) && args(orcid, putCode, activity)")
    public void checkPermissionsWithWork(AccessControl accessControl, String orcid, String putCode, Activity activity) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }
    
    @Before("@annotation(accessControl) && args(uriInfo, orcid, webhookUri)")
    public void checkPermissionsWithOrcidAndWebhookUri(AccessControl accessControl, UriInfo uriInfo, String orcid, String webhookUri) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }

    @AfterReturning(pointcut = "@annotation(accessControl)", returning = "response")
    public void visibilityResponseFilter(Response response, AccessControl accessControl) {    
        if(accessControl.requestComesFromInternalApi()) {
            return;
        }
        Object entity = response.getEntity();
        if (entity != null && OrcidMessage.class.isAssignableFrom(entity.getClass())) {
            OrcidMessage orcidMessage = (OrcidMessage) entity;
            
            //If it is search results, don't filter them, just return them
            if(orcidMessage.getOrcidSearchResults() != null) {
                return;
            }
            
            // get the client id
            Object authentication = getAuthentication();
            
            Set<Visibility> visibilities = new HashSet<Visibility>();
            
            if(allowAnonymousAccess((Authentication)authentication, accessControl)) {
                visibilities.add(Visibility.PUBLIC);
            } else {
                visibilities = permissionChecker.obtainVisibilitiesForAuthentication(getAuthentication(), accessControl.requiredScope(), orcidMessage);
            }
            //If the message contains a bio, and the given name is filtered, restore it as an empty space
            boolean setEmptyGivenNameIfFiltered = false;
            if(orcidMessage.getOrcidProfile() != null) {
                if(orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcidBio() != null) {
                    setEmptyGivenNameIfFiltered = true;
                }
            }
            
            ScopePathType requiredScope = accessControl.requiredScope();
            // If the required scope is */read-limited or */update
            if (isUpdateOrReadScope(requiredScope)) {                
                // If the authentication contains a client_id, use it to check
                // if it should be able to
                if (OrcidOAuth2Authentication.class.isAssignableFrom(authentication.getClass())){
                    OrcidOAuth2Authentication orcidAuth = (OrcidOAuth2Authentication) getAuthentication();

                    OAuth2Request authorization = orcidAuth.getOAuth2Request();
                    String clientId = authorization.getClientId();

                    // #1: Get the user orcid
                    String userOrcid = getUserOrcidFromOrcidMessage(orcidMessage);
                    // #2: Evaluate the scope to know which field to filter
                    boolean allowWorks = false;
                    boolean allowFunding = false;
                    boolean allowAffiliations = false;

                    // Get the update equivalent scope, if it is reading, but,
                    // doesnt have the read permissions, check if it have the
                    // update permissions
                    ScopePathType equivalentUpdateScope = getEquivalentUpdateScope(requiredScope);
                    if (requiredScope.equals(ScopePathType.READ_LIMITED)) {
                        if (hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()))
                            allowWorks = true;
                        if (hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()))
                            allowFunding = true;
                        if (hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()))
                            allowAffiliations = true;
                    } else if (requiredScope.equals(ScopePathType.ORCID_WORKS_UPDATE) || requiredScope.equals(ScopePathType.ORCID_WORKS_READ_LIMITED)) {
                        // Check if the member have the update or read scope on
                        // works
                        if (hasScopeEnabled(clientId, userOrcid, requiredScope.getContent(), equivalentUpdateScope == null ? null : equivalentUpdateScope.getContent()))
                            // If so, allow him to see private works
                            allowWorks = true;
                    } else if (requiredScope.equals(ScopePathType.FUNDING_UPDATE) || requiredScope.equals(ScopePathType.FUNDING_READ_LIMITED)) {
                        // Check if the member have the update or read scope on
                        // funding
                        if (hasScopeEnabled(clientId, userOrcid, requiredScope.getContent(), equivalentUpdateScope == null ? null : equivalentUpdateScope.getContent()))
                            // If so, allow him to see private funding
                            allowFunding = true;
                    } else if (requiredScope.equals(ScopePathType.AFFILIATIONS_UPDATE) || requiredScope.equals(ScopePathType.AFFILIATIONS_READ_LIMITED)) {
                        // Check if the member have the update or read scope on
                        // affiliations
                        if (hasScopeEnabled(clientId, userOrcid, requiredScope.getContent(), equivalentUpdateScope == null ? null : equivalentUpdateScope.getContent()))
                            // If so, allow him to see private affiliations
                            allowAffiliations = true;
                    }

                    visibilityFilter.filter(orcidMessage, clientId, allowWorks, allowFunding, allowAffiliations,
                            visibilities.toArray(new Visibility[visibilities.size()]));
                } else {
                    visibilityFilter.filter(orcidMessage, null, false, false, false, visibilities.toArray(new Visibility[visibilities.size()]));
                }

            } else {
                visibilityFilter.filter(orcidMessage, null, false, false, false, visibilities.toArray(new Visibility[visibilities.size()]));
            }
            
            //This applies for given names that were filtered because of the new visibility field applied on them
            //If the given name was set at the beginning and now is filtered, it means we should restore it as an empty field
            if(setEmptyGivenNameIfFiltered) {
                if(orcidMessage.getOrcidProfile() != null) {
                    if(orcidMessage.getOrcidProfile().getOrcidBio() == null) {
                        orcidMessage.getOrcidProfile().setOrcidBio(new OrcidBio());
                    }
                    
                    if(orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails() == null) {
                        orcidMessage.getOrcidProfile().getOrcidBio().setPersonalDetails(new PersonalDetails());
                    }
                }
            }
            
            //Filter given or family names visibility 
            if(orcidMessage.getOrcidProfile() != null) {
                if(orcidMessage.getOrcidProfile().getOrcidBio() != null) {
                    if(orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails() != null) {
                        if(orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames() != null) {
                            orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames().setVisibility(null); 
                        } else {
                            //Null given names could break client integrations, so, lets return an empty string
                            GivenNames empty = new GivenNames();
                            empty.setContent(StringUtils.EMPTY);
                            orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().setGivenNames(empty);
                        }
                        
                        if(orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName() != null) {
                            orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().setVisibility(null); 
                        }
                    }
                }                
            }
            
            //replace section visibilities now we may have filtered items
            if(orcidMessage.getOrcidProfile() != null) {
                if(orcidMessage.getOrcidProfile().getOrcidBio() != null) {
                    if(orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails() != null) {
                        OtherNames n = orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getOtherNames();
                        if(n != null) {
                            n.setVisibility(getMostFromCollection(n.getOtherName()));
                        }
                    }  
                    ExternalIdentifiers ids = orcidMessage.getOrcidProfile().getOrcidBio().getExternalIdentifiers();
                    if (ids != null){
                        ids.setVisibility(getMostFromCollection(ids.getExternalIdentifier()));                        
                    }
                    Keywords kws = orcidMessage.getOrcidProfile().getOrcidBio().getKeywords();
                    if (kws != null){
                        kws.setVisibility(getMostFromCollection(kws.getKeyword()));
                    }
                    ResearcherUrls urls = orcidMessage.getOrcidProfile().getOrcidBio().getResearcherUrls();
                    if (urls != null){
                        urls.setVisibility(getMostFromCollection(urls.getResearcherUrl()));                        
                    }
                }
            }
        }
    }

    private Visibility getMostFromCollection(List<? extends VisibilityType> c){
        Visibility most = Visibility.PUBLIC;
        for (VisibilityType x : c){
            if (x.getVisibility().isMoreRestrictiveThan(most))
                    most = x.getVisibility();
        }
        return most;
    }
    private String getUserOrcidFromOrcidMessage(OrcidMessage message) {
        OrcidProfile profile = message.getOrcidProfile();
        return profile.getOrcidIdentifier().getPath();
    }

    private boolean isUpdateOrReadScope(ScopePathType requiredScope) {
        switch (requiredScope) {
        case AFFILIATIONS_READ_LIMITED:
        case AFFILIATIONS_UPDATE:
        case FUNDING_READ_LIMITED:
        case FUNDING_UPDATE:
        case ORCID_BIO_READ_LIMITED:
        case ORCID_BIO_UPDATE:
        case ORCID_PATENTS_READ_LIMITED:
        case ORCID_PATENTS_UPDATE:
        case ORCID_PROFILE_READ_LIMITED:
        case READ_LIMITED: 
        case ORCID_WORKS_READ_LIMITED:
        case ORCID_WORKS_UPDATE:
            return true;
        default:
            return false;
        }
    }
    
    @Deprecated
    public boolean hasScopeEnabled(String clientId, String userName, String scope, String equivalentScope) {
        List<String> scopes = new ArrayList<String>();
        scopes.add(scope);
        if (equivalentScope != null)
            scopes.add(equivalentScope); 
        return checkIfScopeIsAvailableForMember(clientId, userName, scopes);
    }

    private Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            return context.getAuthentication();
        } else {
            throw new IllegalStateException("No security context found. This is bad!");
        }
    }

    private ScopePathType getEquivalentUpdateScope(ScopePathType readScope) {
        if (readScope != null)
            switch (readScope) {
            case AFFILIATIONS_READ_LIMITED:
                return ScopePathType.AFFILIATIONS_UPDATE;
            case FUNDING_READ_LIMITED:
                return ScopePathType.FUNDING_UPDATE;
            case ORCID_WORKS_READ_LIMITED:
                return ScopePathType.ORCID_WORKS_UPDATE;
            default:
                return null;
            }
        return null;
    }
    
    private boolean allowAnonymousAccess(Authentication auth, AccessControl accessControl) {        
        boolean allowAnonymousAccess = false;
        if(auth != null) {
            for(GrantedAuthority grantedAuth : auth.getAuthorities()) {
                if("ROLE_ANONYMOUS".equals(grantedAuth.getAuthority())) {
                    if(!accessControl.enableAnonymousAccess()) {
                        break;
                    }
                    allowAnonymousAccess = true;
                    break;
                }
            }                
        }
        return allowAnonymousAccess;
    } 
    
    /**
     * Check if a member have a specific scope over a client
     * 
     * @param clientId
     * @param userName
     * @param scopes
     * @return true if the member have access to any of the specified scope on the specified user
     * */
    @Deprecated
    private boolean checkIfScopeIsAvailableForMember(String clientId, String userName, List<String> requiredScopes) {
        List<String> availableScopes = orcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userName);        
        for(String availableScope : availableScopes) {
            String [] simpleScopes = availableScope.split(" ");
            for(String simpleScope : simpleScopes) {
                if(!PojoUtil.isEmpty(simpleScope)) {
                    ScopePathType scopePathType = ScopePathType.fromValue(simpleScope);
                    for(String requiredScope: requiredScopes) {
                        if(scopePathType.hasScope(requiredScope))
                            return true;
                    }
                }
            }            
        }
        return false;
    }

}
