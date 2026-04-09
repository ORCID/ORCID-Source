package org.orcid.persistence.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

public class ProfileLastModifiedDaoImpl implements ProfileLastModifiedDao {
    
    @Value("${org.orcid.postgres.query.timeout:30000}")
    private Integer queryTimeout;
    
    protected EntityManager entityManager;

    /**
     * This method is used to update the last modified and indexing status
     * without triggering last update events
     * 
     * @param orcid
     * @param indexingStatus
     */
    @Override
    @Transactional
    public void updateLastModifiedDateAndIndexingStatus(String orcid, IndexingStatus indexingStatus) {
        Query updateQuery = entityManager.createQuery("update ProfileEntity p set p.lastModified = now(), p.indexingStatus = :indexingStatus where p.id = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("indexingStatus", indexingStatus);
        // Sets a timeout for this query
        updateQuery.setHint("jakarta.persistence.query.timeout", queryTimeout);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void updateLastModifiedDateWithoutResult(String orcid) {
        Query query = entityManager.createNativeQuery("update profile set last_modified = now() where orcid = :orcid ");
        query.setParameter("orcid", orcid);
        // Sets a timeout for this query
        query.setHint("jakarta.persistence.query.timeout", queryTimeout);
        query.executeUpdate();
    }

    /**
     * This method is used to update the indexing status
     * without triggering last update events
     * 
     * @param orcid
     * @param indexingStatus
     */
    @Override
    @Transactional
    public boolean updateIndexingStatus(List<String> orcidIds, IndexingStatus indexingStatus) {
        Query updateQuery = entityManager.createQuery("update ProfileEntity p set p.indexingStatus = :indexingStatus where p.id IN :orcid");
        updateQuery.setParameter("orcid", orcidIds);
        updateQuery.setParameter("indexingStatus", indexingStatus);
        return updateQuery.executeUpdate() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    /**
     * Fetches the last modified from the database Do not call unless it also
     * manages the request level cache
     * 
     * @See ProfileLastModifiedAspect
     * 
     */
    public Date retrieveLastModifiedDate(String orcid) {
        Query nativeQuery = entityManager.createNativeQuery("Select p.last_modified FROM profile p WHERE p.orcid =:orcid");
        nativeQuery.setParameter("orcid", orcid);
        List<Timestamp> tsList = nativeQuery.getResultList();
        if (tsList != null && !tsList.isEmpty()) {
            return new Date(tsList.get(0).getTime());
        }
        return null;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
}
