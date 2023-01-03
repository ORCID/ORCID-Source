package org.orcid.listener.persistence.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.listener.persistence.entities.Api30RecordStatusEntity;
import org.orcid.listener.persistence.entities.SearchEngineRecordStatusEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SearchEngineRecordStatusDao {
    @PersistenceContext
    protected EntityManager entityManager;

    public SearchEngineRecordStatusEntity get(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT * FROM search_engine_record_status WHERE orcid = :orcid", Api30RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return (SearchEngineRecordStatusEntity) query.getSingleResult();
    }

    public boolean exists(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM search_engine_record_status WHERE orcid=:orcid");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger) query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    @Transactional
    public void create(String orcid, boolean solrOk) throws EntityExistsException {
        SearchEngineRecordStatusEntity entity = new SearchEngineRecordStatusEntity();
        entity.setId(orcid);
        Date now = new Date();
        entity.setDateCreated(now);
        entity.setLastModified(now);
        entity.setSolrStatus(solrOk ? 0 : 1);
        if(solrOk) {
            entity.setSolrLastIndexed(new Date());
        }
        entityManager.persist(entity);
    }

    @Transactional
    public boolean setSolrFail(String orcid) throws IllegalArgumentException {        
        Query query = entityManager.createNativeQuery("UPDATE search_engine_record_status SET solr_status = solr_status + 1 WHERE orcid = :orcid", SearchEngineRecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    @Transactional
    public boolean setSolrOk(String orcid) throws IllegalArgumentException {        
        Query query = entityManager.createNativeQuery("UPDATE search_engine_record_status SET solr_status = 0, summary_last_indexed = now() WHERE orcid = :orcid", SearchEngineRecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    public List<SearchEngineRecordStatusEntity> getFailedElements(int batchSize) {
        TypedQuery<SearchEngineRecordStatusEntity> query = entityManager.createQuery(
                "FROM SearchEngineRecordStatusEntity WHERE solrStatus > 0 ORDER BY solrLastIndexed",
                SearchEngineRecordStatusEntity.class);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }    
              
}
