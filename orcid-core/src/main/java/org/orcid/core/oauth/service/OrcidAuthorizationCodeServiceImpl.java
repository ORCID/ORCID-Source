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
package org.orcid.core.oauth.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOauth2AuthInfo;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationRequestHolder;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

/**
 * 2011-2012 ORCID
 * 
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

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource(name = "profileEntityManager")
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private ProfileDao profileDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAuthorizationCodeServiceImpl.class);

    @Override
    protected void store(String code, AuthorizationRequestHolder authentication) {
        OrcidOauth2AuthoriziationCodeDetail detail = getDetailFromAuthorizationRequestHolder(code, authentication);
        if (detail == null) {
            throw new IllegalArgumentException("Cannot persist the authorisation code as the user and/or client " + "cannot be found");
        }
        orcidOauth2AuthoriziationCodeDetailDao.persist(detail);
        OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authentication);
        LOGGER.info("Storing authorization code: code={}, clientId={}, scopes={}, userOrcid={}", new Object[] { code, authInfo.getClientId(), authInfo.getScopes(),
                authInfo.getUserOrcid() });
    }

    @Override
    protected AuthorizationRequestHolder remove(String code) {
        OrcidOauth2AuthoriziationCodeDetail detail = orcidOauth2AuthoriziationCodeDetailDao.removeAndReturn(code);
        AuthorizationRequestHolder authorizationRequestHolder = getAuthorizationRequestHolderFromDetail(detail);
        OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authorizationRequestHolder);
        if (detail == null) {
            LOGGER.info("No such authorization code to remove: code={}, clientId={}, scopes={}, userOrcid={}",
                    new Object[] { code, authInfo.getClientId(), authInfo.getScopes(), authInfo.getUserOrcid() });
        } else {
            LOGGER.info("Removed authorization code: code={}, clientId={}, scopes={}, userOrcid={}", new Object[] { code, authInfo.getClientId(), authInfo.getScopes(),
                    authInfo.getUserOrcid() });
        }
        return authorizationRequestHolder;
    }

    private AuthorizationRequestHolder getAuthorizationRequestHolderFromDetail(OrcidOauth2AuthoriziationCodeDetail detail) {
        if (detail == null) {
            return null;
        }
        ClientDetailsEntity clientDetailsEntity = detail.getClientDetailsEntity();
        Set<GrantedAuthority> grantedAuthorities = getGrantedAuthoritiesFromStrings(detail.getAuthorities());
        Set<String> scopes = detail.getScopes();
        DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(clientDetailsEntity.getClientId(), scopes);
        authorizationRequest.setAuthorities(grantedAuthorities);
        Set<String> resourceIds = new HashSet<>();
        resourceIds.add("orcid");
        authorizationRequest.setResourceIds(resourceIds);
        authorizationRequest.setRedirectUri(detail.getRedirectUri());
        authorizationRequest.setApproved(detail.getApproved());

        Authentication userAuthentication = new OrcidOauth2UserAuthentication(detail.getProfileEntity(), detail.getAuthenticated());
        return new AuthorizationRequestHolder(authorizationRequest, userAuthentication);
    }

    private Set<GrantedAuthority> getGrantedAuthoritiesFromStrings(Set<String> authorities) {
        Set<GrantedAuthority> grantedAuthorities = null;
        if (authorities != null && !authorities.isEmpty()) {
            grantedAuthorities = new HashSet<GrantedAuthority>(authorities.size());
            for (String authority : authorities) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        return grantedAuthorities;
    }

    private OrcidOauth2AuthoriziationCodeDetail getDetailFromAuthorizationRequestHolder(String code, AuthorizationRequestHolder authentication) {

        AuthorizationRequest authenticationRequest = authentication.getAuthenticationRequest();
        OrcidOauth2AuthoriziationCodeDetail detail = new OrcidOauth2AuthoriziationCodeDetail();
        Map<String, String> parameters = authenticationRequest.getAuthorizationParameters();
        if (parameters != null && !parameters.isEmpty()) {
            String clientId = parameters.get(CLIENT_ID);
            ClientDetailsEntity clientDetails = getClientDetails(clientId);

            if (clientDetails == null) {
                return null;
            }

            detail.setScopes(OAuth2Utils.parseParameterList(parameters.get(SCOPE)));
            detail.setState(parameters.get(STATE));
            detail.setRedirectUri(parameters.get(REDIRECT_URI));
            detail.setResponseType(parameters.get(RESPONSE_TYPE));
            detail.setClientDetailsEntity(clientDetails);
        }

        detail.setId(code);
        detail.setApproved(authenticationRequest.isApproved());
        Authentication userAuthentication = authentication.getUserAuthentication();
        Object principal = userAuthentication.getPrincipal();

        ProfileEntity entity = null;

        if (principal instanceof OrcidProfileUserDetails) {
            OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) principal;
            String effectiveOrcid = userDetails.getOrcid();
            if (effectiveOrcid != null) {
                entity = profileEntityManager.findByOrcid(effectiveOrcid);
            }
        }

        if (entity == null) {
            return null;
        }

        detail.setProfileEntity(entity);
        detail.setAuthenticated(userAuthentication.isAuthenticated());
        Set<String> authorities = getStringSetFromGrantedAuthorities(authenticationRequest.getAuthorities());
        detail.setAuthorities(authorities);
        Object authenticationDetails = userAuthentication.getDetails();
        if (authenticationDetails instanceof WebAuthenticationDetails) {
            detail.setSessionId(((WebAuthenticationDetails) authenticationDetails).getSessionId());
        }
        return detail;
    }

    private ClientDetailsEntity getClientDetails(String clientId) {
        try {
            return clientDetailsManager.findByClientId(clientId);
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
