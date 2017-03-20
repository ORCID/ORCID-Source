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

    void remove(String orcid, UserconnectionPK userConnectionPK);

    UserconnectionEntity findByProviderIdAndProviderUserId(String providerUserId, String providerId);

    void updateLoginInformation(UserconnectionPK pk);

    UserconnectionEntity findByProviderIdAndProviderUserIdAndIdType(String userId, String shibIdentityProvider, String idType);

    void update(UserconnectionEntity userConnectionEntity);

}
