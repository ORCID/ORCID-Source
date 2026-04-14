package org.orcid.core.manager.read_only.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.persistence.NoResultException;

import org.orcid.core.adapter.JpaJaxbClientAdapter;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDetailsManagerReadOnlyImpl implements ClientDetailsManagerReadOnly {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailsManagerReadOnlyImpl.class);    
    
    @Resource
    protected JpaJaxbClientAdapter jpaJaxbClientAdapter;
    
    @Resource
    protected EncryptionManager encryptionManager;    
    
    private ClientDetailsDao clientDetailsDao;
    
    protected ClientSecretDao clientSecretDao;
    
    protected ClientRedirectDao clientRedirectDao;
    
    private Set<String> legacyClientIds;
    
    public void setClientDetailsDao(ClientDetailsDao clientDetailsDao) {
		this.clientDetailsDao = clientDetailsDao;
	}

	public void setClientSecretDao(ClientSecretDao clientSecretDao) {
		this.clientSecretDao = clientSecretDao;
	}

	public void setClientRedirectDao(ClientRedirectDao clientRedirectDao) {
		this.clientRedirectDao = clientRedirectDao;
	}
    
    @Override
    public ClientDetailsEntity findByClientId(String clientId) {
        ClientDetailsEntity result = null;
        try {
            Date lastModified = clientDetailsDao.getLastModified(clientId);
            result = clientDetailsDao.findByClientId(clientId, lastModified.getTime());
            if (result != null) {
                if (!result.getClientId().equals(clientId))
                    LOGGER.error("Client getClientId doesn't match. Requested: " + clientId + " Returned: " + result.getClientId());
                if (!result.getId().equals(clientId))
                    LOGGER.error("Client getId() doesn't match. Requested: " + clientId + " Returned: " + result.getId());
            }
        } catch (NoResultException nre) {
            LOGGER.error("Client not found: '" + clientId + "'");
        }
        return result;
    }    
    
    @Override
    public List<ClientDetailsEntity> getAll() {
        return clientDetailsDao.getAll();
    }
    
    @Override
    public boolean exists(String clientId) {
        return clientDetailsDao.exists(clientId);
    }

    /**
     * Verifies if a client belongs to the given group id
     * 
     * @param clientId
     * @param groupId
     * @return true if clientId belongs to groupId
     * */
    @Override
    public boolean belongsTo(String clientId, String groupId) {
        return clientDetailsDao.belongsTo(clientId, groupId);
    }

    /**
     * Fetch all clients that belongs to a group
     * 
     * @param groupId
     *            Group id
     * @return A list containing all clients that belongs to the given group
     * */
    @Override
    public List<ClientDetailsEntity> findByGroupId(String groupId) {
        return clientDetailsDao.findByGroupId(groupId);
    }

    /**
     * Get the public profile that belongs to the given orcid ID
     * 
     * @param ownerId
     *            The user or group id
     * @return the public client that belongs to the given user
     * */
    @Override
    public ClientDetailsEntity getPublicClient(String ownerId) {
        return clientDetailsDao.getPublicClient(ownerId);
    }
    
    /**
     * Get member name
     * 
     * @param clientId
     *            The client id
     * @return the name of the member owner of the given client 
     * */
    @Override
    public String getMemberName(String clientId) {
        return clientDetailsDao.getMemberName(clientId);
    }
    
    @Override    
    public Date getLastModified(String clientId) {
        return clientDetailsDao.getLastModified(clientId);
    }

    @Override    
    public Date getLastModifiedByIdp(String idp) {
        try {
            return clientDetailsDao.getLastModifiedByIdP(idp);
        } catch(Exception e) {
            LOGGER.warn("There is no client with the IdP: " + idp);
        }
        return null;
    }
    
    @Override
    public ClientDetailsEntity findByIdP(String idp) {
        try {
            ClientDetailsEntity result = clientDetailsDao.findByIdP(idp);
            return result;
        } catch(Exception e) {
            LOGGER.warn("There is no client with the IdP: " + idp);
        }
        return null;
    }
    
    @Override
    public boolean isLegacyClientId(String clientId) {
        initLegacyClientIds();
        return legacyClientIds.contains(clientId);
    }

    private void initLegacyClientIds() {
        if (legacyClientIds == null) {
            synchronized (this) {
                if (legacyClientIds == null) {
                    legacyClientIds = new HashSet<>(clientDetailsDao.findLegacyClientIds());
                }
            }
        }
    }
        
}
