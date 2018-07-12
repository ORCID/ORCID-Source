package org.orcid.core.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;

public class ClientDetailsEntityCacheManagerImpl implements ClientDetailsEntityCacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(ClientDetailsEntityCacheManagerImpl.class);
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource(name = "clientDetailsEntityCache")
    private Cache<Object, ClientDetailsEntity> clientDetailsCache;        
    
    @Resource(name = "clientDetailsEntityIdPCache")
    private Cache<Object, ClientDetailsEntity> clientDetailsIdPCache;        

    private String releaseName = ReleaseNameUtils.getReleaseName();
        
    @Override
    public ClientDetailsEntity retrieve(String clientId) throws IllegalArgumentException {
        Object key = new ClientIdCacheKey(clientId, releaseName);
        Date dbDate = retrieveLastModifiedDate(clientId);;
        ClientDetailsEntity clientDetails = clientDetailsCache.get(key);
        if (needsFresh(dbDate, clientDetails)) {
            clientDetails = clientDetailsCache.get(key);
            if (needsFresh(dbDate, clientDetails)) {
                clientDetails = clientDetailsManager.findByClientId(clientId);
                if (clientDetails == null)
                    throw new InvalidClientException("Client not found: " + clientId);
                clientDetailsCache.put(key, clientDetails);
            }
        }
        return clientDetails;
    }
    
    @Override
    public ClientDetailsEntity retrieveByIdP(String idp) throws IllegalArgumentException {
        Object key = new ClientIdCacheKey("IdP+" + idp, releaseName);
        Date dbDate = retrieveLastModifiedDateByIdP(idp);
        ClientDetailsEntity clientDetails = clientDetailsCache.get(key);
        if (needsFresh(dbDate, clientDetails)) {
            clientDetails = clientDetailsIdPCache.get(key);
            if (needsFresh(dbDate, clientDetails)) {
                clientDetails = clientDetailsManager.findByIdP(idp);
                if (clientDetails == null)
                    throw new IllegalArgumentException("Invalid idp " + idp);
                clientDetailsIdPCache.put(key, clientDetails);
            }
        }
        return clientDetails;
    }

    @Override
    public void put(ClientDetailsEntity clientDetailsEntity) {
        put(clientDetailsEntity.getId(), clientDetailsEntity);
        
    }

    public void put(String clientId, ClientDetailsEntity client) {
        Object key = new ClientIdCacheKey(clientId, releaseName);
        clientDetailsCache.put(key, client);
    }
    
    @Override
    public void removeAll() {
        clientDetailsCache.clear();
    }

    @Override
    public void remove(String clientId) {
        clientDetailsCache.remove(new ClientIdCacheKey(clientId, releaseName));
    }    
    
    private Date retrieveLastModifiedDate(String clientId) {
        Date date = null;
        try {
            date = clientDetailsManager.getLastModified(clientId);
        } catch (javax.persistence.NoResultException e) {
             LOG.debug("Missing lastModifiedDate clientId:" + clientId);   
        }
        return date;
    }
    
    private Date retrieveLastModifiedDateByIdP(String idp) {
        Date date = null;
        try {
            date = clientDetailsManager.getLastModifiedByIdp(idp);
        } catch (javax.persistence.NoResultException e) {
             LOG.debug("Missing lastModifiedDate idp:" + idp);   
        }
        return date;
    }
    
    static public boolean needsFresh(Date dbDate, ClientDetailsEntity clientDetailsEntity) {
        if (clientDetailsEntity == null)
            return true;
        if (dbDate == null) // not sure when this happens?
            return true;
        if (clientDetailsEntity.getLastModified().getTime() != dbDate.getTime())
            return true;
        return false;
    }
}
