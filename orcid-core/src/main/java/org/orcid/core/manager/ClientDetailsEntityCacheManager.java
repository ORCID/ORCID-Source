package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface ClientDetailsEntityCacheManager {

    public ClientDetailsEntity retrieve(String clientId) throws IllegalArgumentException;
    
    public ClientDetailsEntity retrieveByIdP(String clientId) throws IllegalArgumentException;
    
    public void put(ClientDetailsEntity clientDetailsEntity);
    
    public void removeAll();
    
    public void remove(String clientId);    
}
