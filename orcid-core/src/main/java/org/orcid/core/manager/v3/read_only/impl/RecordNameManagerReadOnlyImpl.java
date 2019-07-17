package org.orcid.core.manager.v3.read_only.impl;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbNameAdapter;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.utils.RecordNameUtils;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class RecordNameManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements RecordNameManagerReadOnly {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordNameManagerReadOnlyImpl.class);
    
    @Resource(name = "jpaJaxbNameAdapterV3")
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
    
    @Override
    public String fetchDisplayableCreditName(String orcid) {
        RecordNameEntity recordName = recordNameDao.getRecordName(orcid, getLastModified(orcid));
        return RecordNameUtils.getCreditNameXXX(recordName);
    } 
    
    @Override
    public String fetchDisplayableUserName(String orcid) {
        RecordNameEntity recordName = recordNameDao.getRecordName(orcid, getLastModified(orcid));
        return RecordNameUtils.getCreditNameXXX(recordName);
    }
    
    
}
