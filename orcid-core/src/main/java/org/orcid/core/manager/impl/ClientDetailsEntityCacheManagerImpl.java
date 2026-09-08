package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.core.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDetailsEntityCacheManagerImpl implements ClientDetailsEntityCacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(ClientDetailsEntityCacheManagerImpl.class);

    @Resource(name = "clientDetailsManagerReadOnly")
    private ClientDetailsManagerReadOnly clientDetailsManager;

    public void setClientDetailsManager(ClientDetailsManagerReadOnly clientDetailsManager) {
        this.clientDetailsManager = clientDetailsManager;
    }

    @Resource(name = "clientDetailsEntityCache")
    private Cache<Object, ClientDetailsEntity> clientDetailsCache;

    @Resource(name = "clientDetailsEntityIdPCache")
    private Cache<Object, ClientDetailsEntity> clientDetailsIdPCache;

    // Short-lived ehcache for the last-modified freshness check itself, to collapse bursts of
    // repeated retrieve() calls for the same client (e.g. one per activity in a record/list response)
    // into a single DB round-trip instead of one per call.
    @Resource(name = "clientDetailsLastModifiedCache")
    private Cache<String, Date> clientDetailsLastModifiedCache;

    private final String releaseName = ReleaseNameUtils.getReleaseName();

    @Override
    public ClientDetailsEntity retrieve(String clientId) throws IllegalArgumentException {
        Object key = new ClientIdCacheKey(clientId, releaseName);
        Date dbDate = retrieveLastModifiedDate(clientId);
        ClientDetailsEntity clientDetails = clientDetailsCache.get(key);
        if (needsFresh(dbDate, clientDetails)) {
            clientDetails = clientDetailsCache.get(key);
            if (needsFresh(dbDate, clientDetails)) {
                clientDetails = clientDetailsManager.findByClientId(clientId);
                if (clientDetails == null)
                    throw new IllegalArgumentException("Client not found: " + clientId);
                clientDetailsCache.put(key, clientDetails);
            }
        }
        return clientDetails;
    }

    @Override
    public Map<String, ClientDetailsEntity> retrieveAll(Collection<String> clientIds) {
        Map<String, ClientDetailsEntity> clientDetailsById = new HashMap<>();
        if (clientIds == null || clientIds.isEmpty()) {
            return clientDetailsById;
        }

        List<String> clientIdList = new ArrayList<>(clientIds);
        Map<String, Date> lastModifiedByClientId = clientDetailsManager.getLastModifiedByClientIds(clientIdList);
        if (lastModifiedByClientId == null) {
            return clientDetailsById;
        }
        List<String> staleOrMissingClientIds = new ArrayList<>();

        for (String clientId : clientIdList) {
            Date dbDate = lastModifiedByClientId.get(clientId);
            if (dbDate == null) {
                continue;
            }
            Object key = new ClientIdCacheKey(clientId, releaseName);
            ClientDetailsEntity clientDetails = clientDetailsCache.get(key);
            if (needsFresh(dbDate, clientDetails)) {
                staleOrMissingClientIds.add(clientId);
            } else {
                clientDetailsById.put(clientId, clientDetails);
            }
        }

        if (!staleOrMissingClientIds.isEmpty()) {
            for (ClientDetailsEntity clientDetails : clientDetailsManager.findByClientIds(staleOrMissingClientIds)) {
                clientDetailsCache.put(new ClientIdCacheKey(clientDetails.getId(), releaseName), clientDetails);
                clientDetailsById.put(clientDetails.getId(), clientDetails);
            }
        }

        return clientDetailsById;
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
        clientDetailsLastModifiedCache.remove(clientId);
    }

    @Override
    public void removeAll() {
        clientDetailsCache.clear();
        clientDetailsLastModifiedCache.clear();
    }

    @Override
    public void remove(String clientId) {
        clientDetailsCache.remove(new ClientIdCacheKey(clientId, releaseName));
        clientDetailsLastModifiedCache.remove(clientId);
    }

    private Date retrieveLastModifiedDate(String clientId) {
        Date cached = clientDetailsLastModifiedCache.get(clientId);
        if (cached != null) {
            return cached;
        }
        Date date = null;
        try {
            date = clientDetailsManager.getLastModified(clientId);
        } catch (jakarta.persistence.NoResultException e) {
            LOG.debug("Missing lastModifiedDate clientId:" + clientId);
        }
        // ehcache rejects null values, so only cache a real hit; unknown clients keep hitting the DB
        if (date != null) {
            clientDetailsLastModifiedCache.put(clientId, date);
        }
        return date;
    }

    private Date retrieveLastModifiedDateByIdP(String idp) {
        Date date = null;
        try {
            date = clientDetailsManager.getLastModifiedByIdp(idp);
        } catch (jakarta.persistence.NoResultException e) {
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
