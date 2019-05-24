package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

public class WorkDaoImpl extends GenericDaoImpl<WorkEntity, Long> implements WorkDao {

    public WorkDaoImpl() {
        super(WorkEntity.class);
    }
    
    @Override
    public MinimizedWorkEntity getMinimizedWorkEntity(Long id) {
        TypedQuery<MinimizedWorkEntity> query = entityManager
                .createQuery("from MinimizedWorkEntity where id = :id", MinimizedWorkEntity.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
    
    @Override
    public List<MinimizedWorkEntity> getMinimizedWorkEntities(List<Long> ids) {
        // batch up list into sets of 50;
        List<MinimizedWorkEntity> list = new ArrayList<>();
        for (List<Long> partition : Lists.partition(ids, 50)) {
            TypedQuery<MinimizedWorkEntity> query = entityManager.createQuery("SELECT x FROM MinimizedWorkEntity x WHERE x.id IN :ids", MinimizedWorkEntity.class);
            query.setParameter("ids", partition);
            list.addAll(query.getResultList());
        }
        return list;
    }

    @Override
    public List<WorkEntity> getWorkEntities(String orcid, List<Long> ids) {
        // batch up list into sets of 50;
        List<WorkEntity> list = new ArrayList<>();
        for (List<Long> partition : Lists.partition(ids, 50)) {
            TypedQuery<WorkEntity> query = entityManager.createQuery("SELECT x FROM WorkEntity x WHERE x.orcid=:orcid AND x.id IN :ids", WorkEntity.class);
            query.setParameter("ids", partition);
            query.setParameter("orcid", orcid);
            list.addAll(query.getResultList());
        }
        return list;
    }
    
    @Override
    public void detach(WorkBaseEntity workBaseEntity) {
        entityManager.detach(workBaseEntity);        
    }
    
    /**
     * Updates the visibility of an existing work
     * 
     * @param workId
     *            The id of the work that will be updated
     * @param visibility
     *            The new visibility value for the profile work relationship
     * @return true if the relationship was updated
     * */
    @Override
    @Transactional
    public boolean updateVisibilities(String orcid, List<Long> workIds, String visibility) {
        Query query = entityManager.createNativeQuery("UPDATE work SET visibility=:visibility, last_modified=now() WHERE work_id in (:workIds)");
        query.setParameter("visibility", visibility);
        query.setParameter("workIds", workIds);
        return query.executeUpdate() > 0;
    }
    
    /**
     * Removes a work.
     * 
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the work was deleted
     * */
    @Override
    @Transactional
    public boolean removeWorks(String clientOrcid, List<Long> workIds) {
        Query query = entityManager.createNativeQuery("DELETE FROM work WHERE work_id in (:workIds)");        
        query.setParameter("workIds", workIds);
        return query.executeUpdate() > 0;
    }
        
    /**
     * Remove a single work
     * 
     * @param workId
     *          The id of the work that should be deleted     
     * */
    @Override
    @Transactional
    public boolean removeWork(String orcid, Long workId) {
        Query query = entityManager.createNativeQuery("DELETE FROM work WHERE work_id = :workId and orcid = :orcid");        
        query.setParameter("workId", workId);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    @Override
    @Transactional
    public void removeWorks(String orcid) {
        Query query = entityManager.createQuery("delete from WorkEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }
    
    /**
     * Sets the display index of the new work
     * @param workId
     *          The work id
     * @param orcid
     *          The work owner 
     * @return true if the work index was correctly set                  
     * */
    @Override
    @Transactional
    public boolean updateToMaxDisplay(String orcid, Long workId) {
        Query query = entityManager.createNativeQuery("UPDATE work SET display_index=(select coalesce(MAX(display_index) + 1, 0) from work where orcid=:orcid and work_id != :workId ), last_modified=now() WHERE work_id=:workId");        
        query.setParameter("workId", workId);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }    
    
    /**
     * Returns a list of work ids where the ext id relationship is null
     * @param limit
     *          The batch number to fetch
     * @param workId
     *          The id of the latest work processed         
     * @return a list of work ids    
     * */
    @Override
    @SuppressWarnings("unchecked")    
    public List<BigInteger> getWorksWithNullRelationship() {
        Query query = entityManager.createNativeQuery("SELECT distinct(work_id) FROM (SELECT work_id, json_array_elements(json_extract_path(external_ids_json, 'workExternalIdentifier')) AS j FROM work where external_ids_json is not null) AS a WHERE (j->>'relationship') is null");                
        return query.getResultList();
    }
    
    /**
     * Returns a list of work ids where the work matches the work type and ext ids type
     * @param workType
     *          The work type
     * @param extIdType
     *          The ext id type
     * @param limit
     *          The batch number to fetch
     * @param workId
     *          The id of the latest work processed         
     * @return a list of work ids    
     * */
    @Override
    @SuppressWarnings("unchecked")    
    public List<BigInteger> getWorksByWorkTypeAndExtIdType(String workType, String extIdType) {
        Query query = entityManager.createNativeQuery("SELECT distinct(work_id) FROM (SELECT work_id, json_array_elements(json_extract_path(external_ids_json, 'workExternalIdentifier')) AS j FROM work where work_type=:workType and external_ids_json is not null) AS a WHERE (j->>'workExternalIdentifierType') = :extIdType");
        query.setParameter("extIdType", extIdType);
        query.setParameter("workType", workType);
        return query.getResultList();
    }
    
    /**
     * Retrieve a work from database
     * @param orcid
     * @param id
     * @return the WorkEntity associated with the parameter id
     * */
    @Override
    public WorkEntity getWork(String orcid, Long id) {
        TypedQuery<WorkEntity> query = entityManager.createQuery("FROM WorkEntity WHERE id = :workId and orcid = :orcid", WorkEntity.class);        
        query.setParameter("workId", id);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WorkLastModifiedEntity> getWorkLastModifiedList(String orcid) {
        Query query = entityManager.createQuery("from WorkLastModifiedEntity w where w.orcid=:orcid order by w.displayIndex desc, w.dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<WorkLastModifiedEntity> getPublicWorkLastModifiedList(String orcid) {
        Query query = entityManager.createQuery("from WorkLastModifiedEntity w where w.visibility='PUBLIC' and w.orcid=:orcid order by w.displayIndex desc, w.dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WorkLastModifiedEntity> getWorkLastModifiedList(String orcid, List<Long> ids) {
        Query query = entityManager.createQuery("from WorkLastModifiedEntity w where w.orcid=:orcid and id in (:ids) order by w.displayIndex desc, w.dateCreated asc");
        query.setParameter("orcid", orcid);
        query.setParameter("ids", ids);        
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public boolean increaseDisplayIndexOnAllElements(String orcid) {
        Query query = entityManager.createNativeQuery("update work set display_index=(display_index + 1), last_modified=now() where orcid=:orcid");                
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    public List<WorkEntity> getWorksByOrcidId(String orcid) {
        List<WorkEntity> works = new ArrayList<>();
        List<WorkLastModifiedEntity> lastModifiedWorks = getWorkLastModifiedList(orcid);
        List<Long> ids = lastModifiedWorks.stream().map(w -> w.getId()).collect(Collectors.toList());
        for(List<Long> partition : Lists.partition(ids, 50)) {
            TypedQuery<WorkEntity> query = entityManager.createQuery("SELECT x FROM WorkEntity x WHERE x.id IN :ids", WorkEntity.class);
            query.setParameter("ids", partition);
            works.addAll(query.getResultList());
        }
        return works;
    }

    @Override
    public boolean hasPublicWorks(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM work WHERE orcid=:orcid AND visibility='PUBLIC'");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger)query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClientIds) {
        Query query = entityManager.createNativeQuery("SELECT work_id FROM work WHERE client_source_id = source_id AND client_source_id IN :nonPublicClientIds");
        query.setParameter("nonPublicClientIds", nonPublicClientIds);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctClientSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE work SET client_source_id = source_id, source_id = NULL where work_id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClientIds) {
        Query query = entityManager.createNativeQuery("SELECT work_id FROM work WHERE client_source_id = source_id AND client_source_id IN :publicClientIds");
        query.setParameter("publicClientIds", publicClientIds);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctUserSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE work SET source_id = client_source_id, client_source_id = NULL where work_id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max) {
        Query query = entityManager.createNativeQuery("SELECT work_id FROM work WHERE client_source_id = :clientDetailsId AND assertion_origin_source_id IS NULL");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void updateUserOBODetails(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE work SET assertion_origin_source_id = orcid where work_id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }
}

