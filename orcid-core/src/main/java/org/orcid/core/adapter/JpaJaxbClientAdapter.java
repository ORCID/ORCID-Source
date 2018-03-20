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
