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
package org.orcid.core.manager.read_only;

import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface ClientDetailsManagerReadOnly extends ClientDetailsService {
    ClientDetailsEntity findByClientId(String orcid);

    List<ClientDetailsEntity> getAll();

    Date getLastModified(String clientId);

    Date getLastModifiedByIdp(String idp);
    
    boolean exists(String cliendId);
    
    /**
     * Verifies if a client belongs to the given group id
     * @param clientId
     * @param groupId
     * @return true if clientId belongs to groupId
     * */
    boolean belongsTo(String clientId, String groupId);
    
    /**
     * Fetch all clients that belongs to a group
     * @param groupId
     *  Group id
     * @return A list containing all clients that belongs to the given group
     * */
    List<ClientDetailsEntity> findByGroupId(String groupId);
    
    ClientDetailsEntity getPublicClient(String ownerId);
    
    String getMemberName(String clientId);    
    
    ClientDetailsEntity findByIdP(String idp);

    boolean isLegacyClientId(String clientId);

}
