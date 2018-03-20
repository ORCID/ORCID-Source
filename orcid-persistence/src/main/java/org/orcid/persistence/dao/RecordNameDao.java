package org.orcid.persistence.dao;

import java.util.Date;

import org.orcid.persistence.jpa.entities.RecordNameEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordNameDao extends GenericDao<RecordNameEntity, Long> {
    boolean exists(String orcid);
    
    RecordNameEntity getRecordName(String orcid, long lastModified);
    
    RecordNameEntity findByCreditName(String creditName);

    boolean updateRecordName(RecordNameEntity recordName);

    void createRecordName(RecordNameEntity recordName);
    
    Date getLastModified(String orcid);
}
