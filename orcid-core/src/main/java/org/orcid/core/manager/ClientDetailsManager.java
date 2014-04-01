/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface ClientDetailsManager {
    ClientDetailsEntity findByClientId(String orcid);

    void removeByClientId(String clientId);

    void persist(ClientDetailsEntity clientDetails);

    ClientDetailsEntity merge(ClientDetailsEntity clientDetails);
    
    void remove(String clientId);
    
    ClientDetailsEntity find(String clientId);
    
    List<ClientDetailsEntity> getAll();
}
