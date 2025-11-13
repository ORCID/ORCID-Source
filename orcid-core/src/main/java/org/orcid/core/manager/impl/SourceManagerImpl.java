package org.orcid.core.manager.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.orcid.core.common.util.AuthenticationUtils;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
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
public class SourceManagerImpl extends ManagerReadOnlyBaseImpl implements SourceManager {

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ClientDetailsManager clientDetailsManager;    
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Override
    public String retrieveSourceOrcid() {
        return AuthenticationUtils.retrieveActiveSourceId();
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
            
            ClientDetailsEntity sourceClient = new ClientDetailsEntity(clientId, clientDetails.getClientName());
            sourceClient.setUserOBOEnabled(clientDetails.isUserOBOEnabled());
            sourceEntity.setSourceClient(sourceClient);          
            
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
    public String retrieveRealUserOrcid() {
        return AuthenticationUtils.retrieveRealUserOrcid();
    }

}
