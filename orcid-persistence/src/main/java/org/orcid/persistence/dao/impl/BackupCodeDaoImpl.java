package org.orcid.persistence.dao.impl;

import java.util.List;

import jakarta.persistence.Query;

import org.orcid.persistence.dao.BackupCodeDao;
import org.orcid.persistence.jpa.entities.BackupCodeEntity;
import org.springframework.transaction.annotation.Transactional;

public class BackupCodeDaoImpl extends GenericDaoImpl<BackupCodeEntity, Long> implements BackupCodeDao {
    
    public BackupCodeDaoImpl() {
        super(BackupCodeEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BackupCodeEntity> getUnusedBackupCodes(String orcid) {
        Query query = entityManager.createQuery("FROM BackupCodeEntity WHERE orcid = :orcid AND usedDate IS NULL");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void markUsed(String orcid, String hashedCode) {
        Query query = entityManager.createQuery("UPDATE BackupCodeEntity b SET b.usedDate = now() WHERE b.orcid = :orcid AND b.hashedCode = :hashedCode");
        query.setParameter("orcid", orcid);
        query.setParameter("hashedCode", hashedCode);
        query.executeUpdate();
    }   
    
    @Override
    @Transactional
    public void removedUsedBackupCodes(String orcid) {
        Query query = entityManager.createQuery("DELETE FROM BackupCodeEntity b WHERE b.orcid = :orcid AND b.usedDate IS NULL");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }   
}
