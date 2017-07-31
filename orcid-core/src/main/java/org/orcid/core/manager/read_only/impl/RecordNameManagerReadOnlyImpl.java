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
package org.orcid.core.manager.read_only.impl;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbNameAdapter;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.persistence.dao.RecordNameDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class RecordNameManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements RecordNameManagerReadOnly {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordNameManagerReadOnlyImpl.class);
    
    @Resource
    protected JpaJaxbNameAdapter jpaJaxbNameAdapter;
    
    protected RecordNameDao recordNameDao;        
    
    public void setRecordNameDao(RecordNameDao recordNameDao) {
        this.recordNameDao = recordNameDao;
    }
    
    @Override
    public Name getRecordName(String orcid) {
        try {
            return jpaJaxbNameAdapter.toName(recordNameDao.getRecordName(orcid, getLastModified(orcid)));             
        } catch(Exception e) {
            LOGGER.error("Exception getting record name", e);
        }
        return null;
    }

    @Override
    public Name findByCreditName(String creditName) {
        try {
            return jpaJaxbNameAdapter.toName(recordNameDao.findByCreditName(creditName));
        } catch(Exception e) {
            LOGGER.error("Exception getting record name by credit name", e);
        }
        return null;
    }

    @Override
    public boolean exists(String orcid) {        
        return recordNameDao.exists(orcid);
    }        
}
