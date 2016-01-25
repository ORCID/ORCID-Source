/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class WorkDaoImpl extends GenericDaoImpl<WorkEntity, Long> implements WorkDao {

    public WorkDaoImpl() {
        super(WorkEntity.class);
    }

    /**
     * Add a new work to the work table
     * 
     * @param work
     *            The work that will be persisted
     * @return the work already persisted on database
     * */
    @Override 
    @Transactional
    public WorkEntity addWork(WorkEntity work) {
        this.persist(work);
        this.flush();
        return work;
    }

    @Override
    @Transactional
    public WorkEntity editWork(WorkEntity updatedWork) {
        WorkEntity workToUpdate =  this.find(updatedWork.getId());
        mergeWork(workToUpdate, updatedWork);
        workToUpdate = this.merge(workToUpdate);
        return workToUpdate;
    }
    
    private void mergeWork(WorkEntity workToUpdate, WorkEntity workWithNewData) {
        workToUpdate.setTitle(workWithNewData.getTitle());
        workToUpdate.setTranslatedTitle(workWithNewData.getTranslatedTitle());
        workToUpdate.setSubtitle(workWithNewData.getSubtitle());
        workToUpdate.setDescription(workWithNewData.getDescription());
        workToUpdate.setWorkUrl(workWithNewData.getWorkUrl());
        workToUpdate.setCitation(workWithNewData.getCitation());
        workToUpdate.setJournalTitle(workWithNewData.getJournalTitle());
        workToUpdate.setLanguageCode(workWithNewData.getLanguageCode());
        workToUpdate.setTranslatedTitleLanguageCode(workWithNewData.getTranslatedTitleLanguageCode());
        workToUpdate.setIso2Country(workWithNewData.getIso2Country());
        workToUpdate.setCitationType(workWithNewData.getCitationType());
        workToUpdate.setWorkType(workWithNewData.getWorkType());
        workToUpdate.setPublicationDate(workWithNewData.getPublicationDate());
        workToUpdate.setContributorsJson(workWithNewData.getContributorsJson());
        workToUpdate.setExternalIdentifiersJson(workWithNewData.getExternalIdentifiersJson());        
        workToUpdate.setVisibility(workWithNewData.getVisibility());
        workToUpdate.setDisplayIndex(workWithNewData.getDisplayIndex());        
        workToUpdate.setSource(workWithNewData.getSource());
        workToUpdate.setLastModified(new Date());
        if(workWithNewData.getAddedToProfileDate() != null) {
            workToUpdate.setAddedToProfileDate(workWithNewData.getAddedToProfileDate());
        }
        workToUpdate.setProfile(workWithNewData.getProfile());
    }

    /**
     * Find works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     * */
    @SuppressWarnings("unchecked")
    @Cacheable(value = "dao-works", key = "#orcid.concat('-').concat(#lastModified)")
    public List<MinimizedWorkEntity> findWorks(String orcid, long lastModified) {

        Query query = entityManager
                .createQuery("select NEW org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity(w.id, w.title, w.subtitle, w.journalTitle, w.description, w.publicationDate.day, w.publicationDate.month, w.publicationDate.year, w.visibility, w.externalIdentifiersJson, w.displayIndex, w.source, w.dateCreated, w.lastModified, w.workType, w.languageCode, w.translatedTitleLanguageCode, w.translatedTitle, w.workUrl) "
                        + "from WorkEntity w "
                        + "where w.profile.id=:orcid "
                        + "order by w.displayIndex desc, w.dateCreated asc");
        query.setParameter("orcid", orcid);

        return query.getResultList();
    }

    /**
     * Find the public works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     * */
    @SuppressWarnings("unchecked")
    @Cacheable(value = "dao-public-works", key = "#orcid.concat('-').concat(#lastModified)")
    public List<MinimizedWorkEntity> findPublicWorks(String orcid, long lastModified) {
        Query query = entityManager
                .createQuery("select NEW org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity(w.id, w.title, w.subtitle, w.journalTitle, w.description, w.publicationDate.day, w.publicationDate.month, w.publicationDate.year, w.visibility, w.externalIdentifiersJson, w.displayIndex, w.source, w.dateCreated, w.lastModified, w.workType, w.languageCode, w.translatedTitleLanguageCode, w.translatedTitle, w.workUrl) "
                        + "from WorkEntity w "
                        + "where w.visibility='PUBLIC' and w.profile.id=:orcid "
                        + "order by w.displayIndex desc, w.dateCreated asc");
        query.setParameter("orcid", orcid);

        return query.getResultList();
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
    public boolean updateVisibilities(String orcid, List<Long> workIds, Visibility visibility) {
        Query query = entityManager.createNativeQuery("UPDATE work SET visibility=:visibility, last_modified=now() WHERE work_id in (:workIds)");
        query.setParameter("visibility", visibility.name());
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
        Query query = entityManager.createNativeQuery("UPDATE work SET display_index=(select coalesce(MAX(display_index) + 1, 0) from work where orcid=:orcid and work_id != :workId ) WHERE work_id=:workId");        
        query.setParameter("workId", workId);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    
    /**
     * Returns a list of work ids of works that still have old external identifiers
     * @param limit
     *          The batch number to fetch
     * @return a list of work ids with old ext ids          
     * */
    @Override
    @SuppressWarnings("unchecked")    
    public List<BigInteger> getWorksWithOldExtIds(long workId, long limit) {
        Query query = entityManager.createNativeQuery("SELECT distinct(work_id) FROM (SELECT work_id, json_array_elements(json_extract_path(external_ids_json, 'workExternalIdentifier')) AS j FROM work where work_id > :workId and external_ids_json is not null order by work_id limit :limit) AS a WHERE (j->'relationship') is null");
        query.setParameter("limit", limit);
        query.setParameter("workId", workId);
        return query.getResultList();
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
        TypedQuery<WorkEntity> query = entityManager.createQuery("FROM WorkEntity WHERE id = :workId and profile.id = :orcid", WorkEntity.class);        
        query.setParameter("workId", id);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }
}

