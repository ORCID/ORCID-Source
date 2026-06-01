package org.orcid.core.manager;

import java.util.Collection;
import java.util.Map;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface ClientDetailsEntityCacheManager {

    public ClientDetailsEntity retrieve(String clientId) throws IllegalArgumentException;

    public Map<String, ClientDetailsEntity> retrieveAll(Collection<String> clientIds);
    
    public ClientDetailsEntity retrieveByIdP(String clientId) throws IllegalArgumentException;
    
    public void put(ClientDetailsEntity clientDetailsEntity);
    
    public void removeAll();
    
    public void remove(String clientId);    
}
