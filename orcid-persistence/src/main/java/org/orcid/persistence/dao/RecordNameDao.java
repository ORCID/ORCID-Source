package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordNameDao extends GenericDao<RecordNameEntity, Long> {
    @Transactional(readOnly = true)
    boolean exists(String orcid);
    
    @Transactional(readOnly = true)
    RecordNameEntity getRecordName(String orcid, long lastModified);
    
    @Transactional(readOnly = true)
    RecordNameEntity findByCreditName(String creditName);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateRecordName(RecordNameEntity recordName);

    @Transactional(readOnly = true)
    Date getLastModified(String orcid);

    @Transactional(readOnly = true)
    List<RecordNameEntity> getRecordNames(List<String> orcids);
}
