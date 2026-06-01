package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.BackupCodeEntity;

public interface BackupCodeDao extends GenericDao<BackupCodeEntity, Long> {
    
    List<BackupCodeEntity> getUnusedBackupCodes(String orcid);
    
    void markUsed(String orcid, String hashedCode);

    void removedUsedBackupCodes(String orcid);

    Date getBackupCodesCreationDate(String orcid);
    
}
