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

import org.orcid.core.manager.RecordNameManager;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class RecordNameManagerImpl implements RecordNameManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordNameManagerImpl.class);
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Override
    public RecordNameEntity getRecordName(String orcid) {
        try {
            return recordNameDao.getRecordName(orcid);
        } catch(Exception e) {
            LOGGER.error("Exception getting record name", e);
        }
        return null;
    }

    @Override
    public RecordNameEntity findByCreditName(String creditName) {
        try {
            return recordNameDao.findByCreditName(creditName);
        } catch(Exception e) {
            LOGGER.error("Exception getting record name by credit name", e);
        }
        return null;
    }
    
    @Override
    public boolean updateRecordName(RecordNameEntity recordName) {
        if(recordName == null || recordName.getId() == null || recordName.getProfile() == null) {
            return false;
        }
        return recordNameDao.updateRecordName(recordName);
    }

    @Override
    public void createRecordName(RecordNameEntity recordName) {
        if(recordName == null || recordName.getProfile() == null) {
            return;
        }
        
        recordNameDao.createRecordName(recordName);
    }
}
