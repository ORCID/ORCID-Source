/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public class ClientDetailsManagerImpl implements ClientDetailsManager {

    @Resource
    ClientDetailsDao clientDetailsDao;
    @Resource 
    ProfileDao profileDao;

    @Override    
    public ClientDetailsEntity findByClientId(String orcid) {
        Date lastModified = profileDao.retrieveLastModifiedDate(orcid);
        return clientDetailsDao.findByClientId(orcid, lastModified);
    }

    @Override    
    public void removeByClientId(String clientId) {
        clientDetailsDao.removeByClientId(clientId);
    }

    @Override
    public void persist(ClientDetailsEntity clientDetails) {
        clientDetailsDao.persist(clientDetails);
    }

    @Override
    public ClientDetailsEntity merge(ClientDetailsEntity clientDetails) {
        return clientDetailsDao.merge(clientDetails);
    }
    
    @Override
    public void remove(String clientId){
        clientDetailsDao.remove(clientId);        
    }
    
    @Override    
    public ClientDetailsEntity find(String clientId) {
        return clientDetailsDao.find(clientId);
    }
    
    @Override
    public List<ClientDetailsEntity> getAll() {
        return clientDetailsDao.getAll();
    }
}
