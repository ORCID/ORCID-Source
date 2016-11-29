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
package org.orcid.core.adapter.impl;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbClientAdapter;
import org.orcid.jaxb.model.client_rc4.Client;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public class JpaJaxbClientAdapterImpl implements JpaJaxbClientAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
    
    @Override
    public Client toClient(ClientDetailsEntity entity) {
        return mapperFacade.map(entity, Client.class);
    }


}
