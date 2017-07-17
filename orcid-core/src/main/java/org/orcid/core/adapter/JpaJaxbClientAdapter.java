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
package org.orcid.core.adapter;

import java.util.Collection;
import java.util.Set;

import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface JpaJaxbClientAdapter {

    Client toClient(ClientDetailsEntity entity);
    
    ClientSummary toClientSummary(ClientDetailsEntity entity);
    
    Set<Client> toClientList(Collection<ClientDetailsEntity> entities);
    
    ClientDetailsEntity toEntity(Client client);
    
    ClientDetailsEntity toEntity(Client client, ClientDetailsEntity existing);
    
}
