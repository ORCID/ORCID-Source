package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;

/**
 * @author Shobhit Tyagi
 */
public interface UserConnectionDao extends GenericDao<UserconnectionEntity, UserconnectionPK> {

    void updateLoginInformation(UserconnectionPK pk);

    UserconnectionEntity findByProviderIdAndProviderUserId(String providerUserId, String providerId);
    
    UserconnectionEntity findByProviderIdAndProviderUserIdAndIdType(String providerUserId, String providerId, String idType);

    List<UserconnectionEntity> findByOrcid(String orcid);

    void deleteByOrcid(String orcid);
}
