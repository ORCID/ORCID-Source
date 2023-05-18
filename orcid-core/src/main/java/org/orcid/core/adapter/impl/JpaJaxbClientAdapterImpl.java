package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.Set;

import org.orcid.core.adapter.JpaJaxbClientAdapter;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbClientAdapterImpl implements JpaJaxbClientAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public Client toClient(ClientDetailsEntity entity) {
        return mapperFacade.map(entity, Client.class);
    }

    @Override
    public ClientSummary toClientSummary(ClientDetailsEntity entity) {
        return mapperFacade.map(entity, ClientSummary.class);
    }

    @Override
    public Set<Client> toClientList(Collection<ClientDetailsEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsSet(entities, Client.class);
    }

    @Override
    public ClientDetailsEntity toEntity(Client client) {
        if (client == null) {
            return null;
        }
        return mapperFacade.map(client, ClientDetailsEntity.class);
    }

    @Override
    public ClientDetailsEntity toEntity(Client client, ClientDetailsEntity existing) {
        if (client == null) {
            return null;
        }

        mapperFacade.map(client, existing);
        return existing;
    }

}
