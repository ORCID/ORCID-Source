package org.orcid.core.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDetailsEntityCacheManagerImpl implements ClientDetailsEntityCacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(ClientDetailsEntityCacheManagerImpl.class);
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource(name = "clientDetailsEntityCache")
    private Cache clientDetailsCache;        
    
    @Resource(name = "clientDetailsEntityIdPCache")
    private Cache clientDetailsIdPCache;        

    private String releaseName = ReleaseNameUtils.getReleaseName();
        
    @Override
    public ClientDetailsEntity retrieve(String clientId) throws IllegalArgumentException {
        Object key = new ClientIdCacheKey(clientId, releaseName);
        Date dbDate = retrieveLastModifiedDate(clientId);;
        ClientDetailsEntity clientDetails = null;
        try {
            clientDetailsCache.acquireReadLockOnKey(key);
            clientDetails = toClientDetailsEntity(clientDetailsCache.get(key));
        } finally {
            clientDetailsCache.releaseReadLockOnKey(key);
        }
        if (needsFresh(dbDate, clientDetails)) {
            try {
                clientDetailsCache.acquireWriteLockOnKey(key);
                clientDetails = toClientDetailsEntity(clientDetailsCache.get(key));
                if (needsFresh(dbDate, clientDetails)) {
                    clientDetails = clientDetailsManager.findByClientId(clientId);
                    if (clientDetails == null)
                        throw new IllegalArgumentException("Invalid client id " + clientId);
                    clientDetailsCache.put(new Element(key, clientDetails));
                }
            } finally {
                clientDetailsCache.releaseWriteLockOnKey(key);
            }
        }
        return clientDetails;
    }
    
    @Override
    public ClientDetailsEntity retrieveByIdP(String idp) throws IllegalArgumentException {
        Object key = new ClientIdCacheKey("IdP+" + idp, releaseName);
        Date dbDate = retrieveLastModifiedDateByIdP(idp);
        ClientDetailsEntity clientDetails = null; 
        try {
            clientDetailsIdPCache.acquireReadLockOnKey(key);
            clientDetails = toClientDetailsEntity(clientDetailsCache.get(key));
        } finally {
            clientDetailsIdPCache.releaseReadLockOnKey(key);
        }
        if (needsFresh(dbDate, clientDetails)) {
            try {
                clientDetailsIdPCache.acquireWriteLockOnKey(key);
                clientDetails = toClientDetailsEntity(clientDetailsIdPCache.get(key));
                if (needsFresh(dbDate, clientDetails)) {
                    clientDetails = clientDetailsManager.findByIdP(idp);
                    if (clientDetails == null)
                        throw new IllegalArgumentException("Invalid idp " + idp);
                    clientDetailsIdPCache.put(new Element(key, clientDetails));
                }
            } finally {
                clientDetailsIdPCache.releaseWriteLockOnKey(key);
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
        try {
            clientDetailsCache.acquireWriteLockOnKey(key);
                clientDetailsCache.put(new Element(key, client));
          
        } finally {
            clientDetailsCache.releaseWriteLockOnKey(key);
        }
    }
    
    @Override
    public void removeAll() {
        clientDetailsCache.removeAll();
        
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
    
    static public ClientDetailsEntity toClientDetailsEntity(Element element) {
        return (ClientDetailsEntity) (element != null ? element.getObjectValue() : null);
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
