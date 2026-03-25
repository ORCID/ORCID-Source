package org.orcid.core.manager.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.orcid.core.common.util.AuthenticationUtils;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

        SourceEntity sourceEntity = new SourceEntity();
        // API authentication
        if (OrcidBearerTokenAuthentication.class.isAssignableFrom(authentication.getClass())) {
            OrcidBearerTokenAuthentication authDetails = (OrcidBearerTokenAuthentication) authentication;
            String clientId = authDetails.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
            ClientDetailsEntity sourceClient = new ClientDetailsEntity(clientId, clientDetails.getClientName());
            sourceClient.setUserOBOEnabled(clientDetails.isUserOBOEnabled());
            sourceEntity.setSourceClient(sourceClient);
        } else {
            // User authentication
            String userOrcid = AuthenticationUtils.retrieveEffectiveOrcid();
            if (userOrcid == null) {
                // Must be system role
                return null;
            }
            sourceEntity.setSourceProfile(new ProfileEntity(userOrcid));
            sourceEntity.setCachedSourceName(sourceNameCacheManager.retrieve(userOrcid));
        }
        
        return sourceEntity;
    }

    @Override
    public String retrieveRealUserOrcid() {
        return AuthenticationUtils.retrieveRealUserOrcid();
    }

}
