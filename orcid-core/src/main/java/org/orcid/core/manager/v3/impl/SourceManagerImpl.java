package org.orcid.core.manager.v3.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.common.util.AuthenticationUtils;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.core.oauth.OrcidOboOAuth2Authentication;
import org.orcid.core.security.OrcidRoles;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
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
    
    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    /** returns the active source, either an effective ORCID iD or Client Id.
     * 
     */
    @Override
    public String retrieveActiveSourceId() {
        return AuthenticationUtils.retrieveActiveSourceId();
    }

    /** This should be used by managers that need active Source information, including OBO.
     * 
     * @return a populated Source with active agent details.
     */
    @Override
    public Source retrieveActiveSource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
         
        // API
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            String clientId = authorizationRequest.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
            Source source = new Source();
            source.setSourceClientId(new SourceClientId(clientId));
            source.setSourceName(new SourceName(clientDetails.getClientName()));  
            
            //OBO if needed
            if(Features.OAUTH_TOKEN_VALIDATION.isActive()) {
                if(OrcidOboOAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
                    OrcidOboOAuth2Authentication authDetails = (OrcidOboOAuth2Authentication) authentication;
                    if (StringUtils.isNotBlank(authDetails.getOboClientId())) {
                        ClientDetailsEntity oboClientDetails = clientDetailsManager.findByClientId(authDetails.getOboClientId());
                        source.setAssertionOriginClientId(new SourceClientId(oboClientDetails.getClientId()));
                        source.setAssertionOriginName(new SourceName(oboClientDetails.getClientName()));
                    } else {
                        if(clientDetails.isUserOBOEnabled() && authDetails.getUserAuthentication() != null && OrcidOauth2UserAuthentication.class.isAssignableFrom(authDetails.getUserAuthentication().getClass())) {
                            OrcidOauth2UserAuthentication userAuth = (OrcidOauth2UserAuthentication) authDetails.getUserAuthentication();
                            ProfileEntity profile = (ProfileEntity) userAuth.getPrincipal();
                            source.setAssertionOriginOrcid(new SourceOrcid(profile.getId()));
                            source.setAssertionOriginName(new SourceName(sourceNameCacheManager.retrieve(profile.getId())));
                        }
                    }
                } else {
                    OrcidOAuth2Authentication authDetails = (OrcidOAuth2Authentication) authentication;
                    if(clientDetails.isUserOBOEnabled() && authDetails.getUserAuthentication() != null && OrcidOauth2UserAuthentication.class.isAssignableFrom(authDetails.getUserAuthentication().getClass())) {
                        OrcidOauth2UserAuthentication userAuth = (OrcidOauth2UserAuthentication) authDetails.getUserAuthentication();
                        ProfileEntity profile = (ProfileEntity) userAuth.getPrincipal();
                        source.setAssertionOriginOrcid(new SourceOrcid(profile.getId()));
                        source.setAssertionOriginName(new SourceName(sourceNameCacheManager.retrieve(profile.getId())));
                    }
                }
            } else {
                OAuth2AuthenticationDetails authDetails = (OAuth2AuthenticationDetails) ((OAuth2Authentication) authentication).getDetails();
                if (authDetails != null && authDetails.getTokenValue() != null) { //check here because mock tests can't cope otherwise.
                    // TODO: Use the authorization server to build this list of tokens
                    OrcidOauth2TokenDetail tokenDetail = orcidOauth2TokenDetailDao.findByTokenValue(authDetails.getTokenValue());
                    if (!StringUtils.isEmpty(tokenDetail.getOboClientDetailsId())) {
                        ClientDetailsEntity oboClientDetails = clientDetailsManager.findByClientId(tokenDetail.getOboClientDetailsId());
                        source.setAssertionOriginClientId(new SourceClientId(oboClientDetails.getClientId()));
                        source.setAssertionOriginName(new SourceName(oboClientDetails.getClientName()));
                    } else if (tokenDetail.getOrcid() != null && clientDetails.isUserOBOEnabled()) {
                        source.setAssertionOriginOrcid(new SourceOrcid(tokenDetail.getOrcid()));
                        source.setAssertionOriginName(new SourceName(sourceNameCacheManager.retrieve(tokenDetail.getOrcid())));
                    }
                }
            }
            return source;
        }
        String userOrcid = AuthenticationUtils.retrieveEffectiveOrcid();
        if(userOrcid == null){
            // Must be system role
            return null;
        }
        // Normal web user
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid(userOrcid));
        return source;
    }
    
    /** Note this should only be used by managers that need an actual SourceEntity (minus OBO)
     * This means Profile and Org only.
     * 
     */
    @Override
    public SourceEntity retrieveActiveSourceEntity() {
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
            return sourceEntity;
        }
        String userOrcid = AuthenticationUtils.retrieveEffectiveOrcid();
        if(userOrcid == null){
            // Must be system role
            return null;
        }
        // Normal web user
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceProfile(new ProfileEntity(userOrcid));
        sourceEntity.setCachedSourceName(sourceNameCacheManager.retrieve(userOrcid));
        
        return sourceEntity;
    }

    @Override
    public boolean isInDelegationMode() {
        return AuthenticationUtils.isInDelegationMode();
    }

    @Override
    public String retrieveRealUserOrcid() {
        return AuthenticationUtils.retrieveRealUserOrcid();
    }

    @Override
    public ProfileEntity retrieveSourceProfileEntity() {
        String sourceOrcid = retrieveActiveSourceId();
        if (sourceOrcid == null) {
            return null;
        }
        return profileDao.find(sourceOrcid);
    }

    @Override
    public boolean isDelegatedByAnAdmin() {
        return AuthenticationUtils.isDelegatedByAnAdmin();
    }
    
}
