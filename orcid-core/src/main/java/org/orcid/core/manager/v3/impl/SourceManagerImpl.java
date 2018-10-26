package org.orcid.core.manager.v3.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.SourceClientId;
import org.orcid.jaxb.model.v3.rc2.common.SourceName;
import org.orcid.jaxb.model.v3.rc2.common.SourceOrcid;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
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

    /** returns the active source, either an effective ORCID iD or Client Id.
     * 
     */
    @Override
    public String retrieveActiveSourceId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        // Token endpoint
        if (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return ((UsernamePasswordAuthenticationToken) authentication).getName();
        }
        // API
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            return authorizationRequest.getClientId();
        }
        // Normal web user
        return retrieveEffectiveOrcid(authentication);
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
            OAuth2AuthenticationDetails authDetails = (OAuth2AuthenticationDetails)((OAuth2Authentication) authentication).getDetails();
            OrcidOauth2TokenDetail tokenDetail = orcidOauth2TokenDetailDao.findByTokenValue(authDetails.getTokenValue());
            if (!StringUtils.isEmpty(tokenDetail.getOboClientDetailsId())){
                ClientDetailsEntity oboClientDetails = clientDetailsManager.findByClientId(tokenDetail.getOboClientDetailsId());
                source.setAssertionOriginClientId(new SourceClientId(oboClientDetails.getClientId()));
                source.setAssertionOriginName(new SourceName(oboClientDetails.getClientName()));  
            }
            
            //TODO: can add OBO person here if client is always OBO person...
            
            return source;
        }
        String userOrcid = retrieveEffectiveOrcid(authentication);
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
            SourceEntityUtils.getSourceName(sourceEntity);
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
        String sourceOrcid = retrieveActiveSourceId();
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
