package org.orcid.core.manager.v3.impl;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.common.util.AuthenticationUtils;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
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
        Source source = new Source();

        // API authentication
        if (OrcidBearerTokenAuthentication.class.isAssignableFrom(authentication.getClass())) {
            OrcidBearerTokenAuthentication authDetails = (OrcidBearerTokenAuthentication) authentication;
            String clientId = authDetails.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);

            source.setSourceClientId(new SourceClientId(clientId));
            source.setSourceName(new SourceName(clientDetails.getClientName()));  

            // Check member OBO
            if(StringUtils.isNotBlank(authDetails.getOboClientId())) {
                String oboClientId = authDetails.getOboClientId();
                ClientDetailsEntity oboClientDetails = clientDetailsManager.findByClientId(oboClientId);
                source.setAssertionOriginClientId(new SourceClientId(oboClientId));
                source.setAssertionOriginName(new SourceName(oboClientDetails.getClientName()));
            } else if(clientDetails.isUserOBOEnabled()){
                // Check user OBO
                source.setAssertionOriginOrcid(new SourceOrcid(authDetails.getUserOrcid()));
                source.setAssertionOriginName(new SourceName(sourceNameCacheManager.retrieve(authDetails.getUserOrcid())));
            }
        } else {
            // User authentication
            String userOrcid = AuthenticationUtils.retrieveEffectiveOrcid();
            if(userOrcid == null){
                // Must be system role
                return null;
            }
            // Normal web user
            source.setSourceOrcid(new SourceOrcid(userOrcid));
        }

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

        SourceEntity sourceEntity = new SourceEntity();
        // API authentication
        if (OrcidBearerTokenAuthentication.class.isAssignableFrom(authentication.getClass())) {
            OrcidBearerTokenAuthentication authDetails = (OrcidBearerTokenAuthentication) authentication;
            String clientId = authDetails.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
            sourceEntity.setSourceClient(new ClientDetailsEntity(clientId, clientDetails.getClientName()));
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
