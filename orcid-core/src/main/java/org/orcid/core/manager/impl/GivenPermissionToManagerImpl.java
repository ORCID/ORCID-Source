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
package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.GivenPermissionToManager;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;

public class GivenPermissionToManagerImpl implements GivenPermissionToManager{

    @Resource 
    private GivenPermissionToDao givenPermissionToDao;
    
    @Override
    public GivenPermissionToEntity findByGiverAndReceiverOrcid(String giverOrcid, String receiverOrcid) {
        return givenPermissionToDao.findByGiverAndReceiverOrcid(giverOrcid, receiverOrcid);
    }

    @Override
    public void remove(String giverOrcid, String receiverOrcid) {
        givenPermissionToDao.remove(giverOrcid, receiverOrcid);
    }

    @Override
    public void merge(GivenPermissionToEntity permission) {
        givenPermissionToDao.merge(permission);
        
    }

}
