package org.orcid.core.oauth.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.oauth.OrcidOauth2AuthInfo;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

/**
 * @author Declan Newman (declan) Date: 23/04/2012
 */
@Service("orcidAuthorizationCodeService")
public class OrcidAuthorizationCodeServiceImpl extends RandomValueAuthorizationCodeServices {

    private static final String CLIENT_ID = "client_id";

    private static final String STATE = "state";

    private static final String SCOPE = "scope";

    private static final String REDIRECT_URI = "redirect_uri";

    private static final String RESPONSE_TYPE = "response_type"; 
    
    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;              
    
    @Resource(name = "profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAuthorizationCodeServiceImpl.class);
    
    @Resource 
    private NamespacedRandomCodeGenerator generator;
    
    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = generator.nextRandomCode();
        store(code, authentication);
        return code;
    }
    
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        OrcidOauth2AuthoriziationCodeDetail detail = getDetailFromAuthorization(code, authentication);
        if (detail == null) {
            throw new IllegalArgumentException("Cannot persist the authorisation code as the user and/or client " + "cannot be found");
        }
        orcidOauth2AuthoriziationCodeDetailDao.persist(detail);
        OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authentication);
        LOGGER.info("Storing authorization code: code={}, clientId={}, scopes={}, userOrcid={}", new Object[] { code, authInfo.getClientId(), authInfo.getScopes(),
                authInfo.getUserOrcid() });
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        OrcidOauth2AuthoriziationCodeDetail detail = orcidOauth2AuthoriziationCodeDetailDao.removeAndReturn(code);
        if (detail == null) {
            LOGGER.info("No such authorization code to remove: code={}",
                    new Object[] { code });
            return null;
        } 
        OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(detail.getClientDetailsEntity().getId(), detail.getScopes(), detail.getProfileEntity().getId());         
        LOGGER.info("Removed authorization code: code={}, clientId={}, scopes={}, userOrcid={}", new Object[] { code, authInfo.getClientId(), authInfo.getScopes(),
                    authInfo.getUserOrcid() });
        
        
        OAuth2Request oAuth2Request = new OAuth2Request(Collections.<String, String> emptyMap(), authInfo.getClientId(), Collections.<GrantedAuthority> emptyList(), true, authInfo.getScopes(), detail.getResourceIds(), detail.getRedirectUri(), new HashSet<String>(Arrays.asList(detail.getResponseType())), Collections.<String, Serializable> emptyMap());
        Authentication userAuth = getUserAuthentication(detail);
        OAuth2Authentication result = new OAuth2Authentication(oAuth2Request, userAuth);
        return result;        
    }        

    private OrcidOauth2UserAuthentication getUserAuthentication(OrcidOauth2AuthoriziationCodeDetail detail) {
        return new OrcidOauth2UserAuthentication(detail.getProfileEntity(), detail.getAuthenticated());
    }
    
    private OrcidOauth2AuthoriziationCodeDetail getDetailFromAuthorization(String code, OAuth2Authentication authentication) {

        OAuth2Request oAuth2Request = authentication.getOAuth2Request();
        OrcidOauth2AuthoriziationCodeDetail detail = new OrcidOauth2AuthoriziationCodeDetail();
        Map<String, String> requestParameters = oAuth2Request.getRequestParameters();
        if (requestParameters != null && !requestParameters.isEmpty()) {
            String clientId = (String) requestParameters.get(CLIENT_ID);
            ClientDetailsEntity clientDetails = getClientDetails(clientId);

            if (clientDetails == null) {
                return null;
            }

            detail.setScopes(OAuth2Utils.parseParameterList((String)requestParameters.get(SCOPE)));
            detail.setState((String)requestParameters.get(STATE));
            detail.setRedirectUri((String)requestParameters.get(REDIRECT_URI));
            detail.setResponseType((String)requestParameters.get(RESPONSE_TYPE));
            detail.setClientDetailsEntity(clientDetails);
            
            //persist the openID params if present
            if (requestParameters.get(OrcidOauth2Constants.NONCE) != null)
                detail.setNonce((String)requestParameters.get(OrcidOauth2Constants.NONCE));
        }

        detail.setId(code);
        detail.setApproved(authentication.getOAuth2Request().isApproved());
        Authentication userAuthentication = authentication.getUserAuthentication();
        Object principal = userAuthentication.getDetails();

        ProfileEntity entity = null;

        if (principal instanceof OrcidProfileUserDetails) {
            OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) principal;
            String effectiveOrcid = userDetails.getOrcid();
            if (effectiveOrcid != null) {
                entity = profileEntityCacheManager.retrieve(effectiveOrcid);
            }
        }

        if (entity == null) {
            return null;
        }

        detail.setProfileEntity(entity);
        detail.setAuthenticated(userAuthentication.isAuthenticated());
        Set<String> authorities = getStringSetFromGrantedAuthorities(authentication.getAuthorities());
        detail.setAuthorities(authorities);
        Object authenticationDetails = userAuthentication.getDetails();
        if (authenticationDetails instanceof WebAuthenticationDetails) {
            detail.setSessionId(((WebAuthenticationDetails) authenticationDetails).getSessionId());
        }
                
        boolean isPersistentTokenEnabledByUser = false;
        //Set token version to persistent token
        //TODO: As of Jan 2015 all tokens will be new tokens, so, we will have to remove the token version code and 
        //treat all tokens as new tokens
        detail.setVersion(Long.valueOf(OrcidOauth2Constants.PERSISTENT_TOKEN));
        if(requestParameters.containsKey(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN)) {
            String grantPersitentToken = (String)requestParameters.get(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN);
            if(Boolean.parseBoolean(grantPersitentToken)) {
                isPersistentTokenEnabledByUser = true;                
            }
        }        
                
        detail.setPersistent(isPersistentTokenEnabledByUser);        
        
        return detail;
    }

    private ClientDetailsEntity getClientDetails(String clientId) {
        try {
            return clientDetailsEntityCacheManager.retrieve(clientId);
        } catch (NoResultException e) {
            return null;
        }
    }

    private Set<String> getStringSetFromGrantedAuthorities(Collection<GrantedAuthority> authorities) {
        Set<String> stringSet = new HashSet<String>();
        if (authorities != null && !authorities.isEmpty()) {
            for (GrantedAuthority authority : authorities) {
                stringSet.add(authority.getAuthority());
            }
        }
        return stringSet;
    }

}
