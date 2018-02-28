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
package org.orcid.core.manager.v3.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SourceManagerImpl implements SourceManager {

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ClientDetailsManager clientDetailsManager;    

    @Override
    public String retrieveSourceOrcid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        // API
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            return authorizationRequest.getClientId();
        }
        // Normal web user
        return retrieveEffectiveOrcid(authentication);
    }

    @Override
    public SourceEntity retrieveSourceEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
         
        // API
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            String clientId = authorizationRequest.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setSourceClient(new ClientDetailsEntity(clientId, clientDetails.getClientName()));
            sourceEntity.getSourceName();
            return sourceEntity;
        }
        String userOrcid = retrieveEffectiveOrcid(authentication);
        if(userOrcid == null){
            // Must be system role
            return null;
        }
        // Normal web user
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceProfile(new ProfileEntity(userOrcid));
        
        //TODO! Set the source name in the SourceEntity
        
        return sourceEntity;
    }

    private String retrieveEffectiveOrcid(Authentication authentication) {
        if (authentication.getDetails() != null && OrcidProfileUserDetails.class.isAssignableFrom(authentication.getDetails().getClass())) {
            return ((OrcidProfileUserDetails) authentication.getDetails()).getOrcid();
        }
        return null;
    }

    private String retrieveEffectiveOrcid() {
        return retrieveEffectiveOrcid(SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public boolean isInDelegationMode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String realUserOrcid = getRealUserIfInDelegationMode(authentication);
        if (realUserOrcid == null) {
            return false;
        }
        return !retrieveEffectiveOrcid().equals(realUserOrcid);
    }

    @Override
    public String retrieveRealUserOrcid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        // API
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            return authorizationRequest.getClientId();
        }
        // Delegation mode
        String realUserIfInDelegationMode = getRealUserIfInDelegationMode(authentication);
        if (realUserIfInDelegationMode != null) {
            return realUserIfInDelegationMode;
        }
        // Normal web user
        return retrieveEffectiveOrcid(authentication);
    }

    private String getRealUserIfInDelegationMode(Authentication authentication) {
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                for (GrantedAuthority authority : authorities) {
                    if (authority instanceof SwitchUserGrantedAuthority) {
                        SwitchUserGrantedAuthority suga = (SwitchUserGrantedAuthority) authority;
                        Authentication sourceAuthentication = suga.getSource();
                        if ((sourceAuthentication instanceof UsernamePasswordAuthenticationToken || sourceAuthentication instanceof PreAuthenticatedAuthenticationToken)
                                && sourceAuthentication.getDetails() instanceof OrcidProfileUserDetails) {
                            return ((OrcidProfileUserDetails) sourceAuthentication.getDetails()).getOrcid();
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ProfileEntity retrieveSourceProfileEntity() {
        String sourceOrcid = retrieveSourceOrcid();
        if (sourceOrcid == null) {
            return null;
        }
        return profileDao.find(sourceOrcid);
    }

    @Override
    public boolean isDelegatedByAnAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                for (GrantedAuthority authority : authorities) {
                    if (authority instanceof SwitchUserGrantedAuthority) {
                        SwitchUserGrantedAuthority suga = (SwitchUserGrantedAuthority) authority;
                        Authentication sourceAuthentication = suga.getSource();
                        if (sourceAuthentication instanceof UsernamePasswordAuthenticationToken && sourceAuthentication.getDetails() instanceof OrcidProfileUserDetails) {
                            return ((OrcidProfileUserDetails) sourceAuthentication.getDetails()).getAuthorities().contains(OrcidWebRole.ROLE_ADMIN);
                        }
                    }
                }
            }
        }
        return false;
    }
}
