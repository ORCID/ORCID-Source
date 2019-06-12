package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;

/**
 * 
 * @author Will Simpson
 *
 */
public interface UserConnectionManager {

    List<UserconnectionEntity> findByOrcid(String orcid);
    
    UserconnectionEntity findByUserConnectionId(String userConnectionId);

    void remove(String orcid, UserconnectionPK userConnectionPK);

    UserconnectionEntity findByProviderIdAndProviderUserId(String providerUserId, String providerId);

    void updateLoginInformation(UserconnectionPK pk);

    UserconnectionEntity findByProviderIdAndProviderUserIdAndIdType(String userId, String shibIdentityProvider, String idType);

    void create(String providerUserId, String providerId, String email, String userName, String accessToken, Long expireTime);

    void update(UserconnectionEntity entity);
    
    void update(String providerUserId, String providerId, String accessToken, Long expireTime);
}
