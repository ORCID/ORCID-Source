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

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class RecordNameManagerImpl implements RecordNameManager {

    @Resource
    private RecordNameDao recordNameDao;
    
    @Override
    public RecordNameEntity getRecordName(String orcid) {
        try {
            return recordNameDao.getRecordName(orcid);
        } catch(Exception e) {
            
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
