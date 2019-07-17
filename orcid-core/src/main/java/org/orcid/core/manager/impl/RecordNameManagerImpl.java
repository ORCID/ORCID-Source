package org.orcid.core.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.manager.RecordNameManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.read_only.impl.RecordNameManagerReadOnlyImpl;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class RecordNameManagerImpl extends RecordNameManagerReadOnlyImpl implements RecordNameManager {

    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Override
    public boolean updateRecordName(String orcid, Name name) {
        if(name == null) {
            return false;
        }
        
        RecordNameEntity entity = jpaJaxbNameAdapter.toRecordNameEntity(name);
        if(PojoUtil.isEmpty(entity.getOrcid())) {
            entity.setOrcid(orcid);
        }
        entity.setLastModified(new Date());
        recordNameDao.updateRecordName(entity);
        // Evict the name in the source name manager
        sourceNameCacheManager.remove(orcid);
        return true;
    }

    @Override
    public void createRecordName(String orcid, Name name) {
        if(name == null) {
            return;
        }
        
        if(recordNameDao.exists(orcid)) {
            throw new IllegalArgumentException("The name for " + orcid + " already exists");
        }
        RecordNameEntity entity = jpaJaxbNameAdapter.toRecordNameEntity(name);
        if(PojoUtil.isEmpty(entity.getOrcid())) {
            entity.setOrcid(orcid);
        }
        Date now = new Date();
        entity.setDateCreated(now);
        entity.setLastModified(now);
        recordNameDao.createRecordName(entity);
    }
}
