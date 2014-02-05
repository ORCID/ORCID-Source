package org.orcid.core.manager.impl;

import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidClientDetailsService;
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public class OrcidSSOManagerImpl implements OrcidSSOManager {

    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private OrcidClientDetailsService orcidClientDetailsService;
    
    @Override
    public ClientDetailsEntity generateUserCredentials(String orcid) {
        ClientDetailsEntity result = null;
        ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);        
        if(profileEntity == null) {
            throw new IllegalArgumentException("ORCID does not exist for " + orcid + " cannot continue");
        } else if(OrcidType.CLIENT.equals(profileEntity.getOrcidType())){
            result = profileEntity.getClientDetails();
        } else {
            String clientSecret = encryptionManager.encryptForInternalUse(UUID.randomUUID().toString());
            StringBuilder clientId = new StringBuilder(profileEntity.getId());
            //TODO
            /*return populateClientDetailsEntity(clientId.toString(), profileEntity, clientSecret, clientScopes, clientResourceIds, clientAuthorizedGrantTypes,
                    clientRegisteredRedirectUris, clientGrantedAuthorities);*/
        }
        return result;
    }

    @Override
    public ClientDetailsEntity getUserCredentials(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }
}
