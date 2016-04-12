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
