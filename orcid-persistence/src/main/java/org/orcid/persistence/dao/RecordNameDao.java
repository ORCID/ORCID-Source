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
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean exists(String orcid);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    RecordNameEntity getRecordName(String orcid, long lastModified);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    RecordNameEntity findByCreditName(String creditName);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateRecordName(RecordNameEntity recordName);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Date getLastModified(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<RecordNameEntity> getRecordNames(List<String> orcids);
}
