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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;
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
        workToUpdate.setProfile(workWithNewData.getProfile());
        workToUpdate.setVisibility(workWithNewData.getVisibility());
        workToUpdate.setDisplayIndex(workWithNewData.getDisplayIndex());
        workToUpdate.setAddedToProfileDate(workWithNewData.getAddedToProfileDate());
        workToUpdate.setSource(workWithNewData.getSource());
        workToUpdate.setLastModified(new Date());
    }

    /**
     * Find works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     * */
    @SuppressWarnings("unchecked")
    public List<MinimizedWorkEntity> findWorks(String orcid) {

        Query query = entityManager
                .createQuery("select NEW org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity(w.id, w.title, w.subtitle, w.journalTitle, w.description, w.publicationDate.day, w.publicationDate.month, w.publicationDate.year, pw.visibility, w.externalIdentifiersJson, pw.displayIndex, pw.source, pw.dateCreated, pw.lastModified, w.workType, w.languageCode, w.translatedTitleLanguageCode, w.translatedTitle, w.workUrl) "
                        + "from WorkEntity w, ProfileWorkEntity pw "
                        + "where pw.profile.id=:orcid and w.id=pw.work.id "
                        + "order by pw.displayIndex desc, pw.dateCreated asc");
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
    public List<MinimizedWorkEntity> findPublicWorks(String orcid) {
        Query query = entityManager
                .createQuery("select NEW org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity(w.id, w.title, w.subtitle, w.journalTitle, w.description, w.publicationDate.day, w.publicationDate.month, w.publicationDate.year, pw.visibility, w.externalIdentifiersJson, pw.displayIndex, pw.sourceProfile, pw.dateCreated, pw.lastModified, w.workType, w.languageCode, w.translatedTitleLanguageCode, w.translatedTitle, w.workUrl) "
                        + "from WorkEntity w, ProfileWorkEntity pw "
                        + "where pw.visibility='PUBLIC' and pw.profile.id=:orcid and w.id=pw.work.id "
                        + "order by pw.displayIndex desc, pw.dateCreated asc");
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
    public boolean updateVisibilities(String orcid, ArrayList<Long> workIds, Visibility visibility) {
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
    public boolean removeWorks(String clientOrcid, ArrayList<Long> workIds) {
        Query query = entityManager.createNativeQuery("DELETE FROM work WHERE work_id in (:workIds)");        
        query.setParameter("workIds", workIds);
        return query.executeUpdate() > 0;
    }
    
    /**
     * Copy the data from the profile_work table to the work table
     * @param profileWork
     *          The profileWork object that contains the profile_work info
     * @param workId
     *          The id of the work we want to update
     * @return true if the work was updated                  
     * */
    @Override
    @Transactional
    public boolean copyDataFromProfileWork(Long workId, ProfileWorkEntity profileWork) {     
        WorkEntity work = this.find(workId);
        work.setAddedToProfileDate(profileWork.getAddedToProfileDate());
        work.setDisplayIndex(profileWork.getDisplayIndex());
        work.setVisibility(profileWork.getVisibility());
        work.setProfile(profileWork.getProfile());
        work.setSource(profileWork.getSource());
        this.merge(work);
        return true;
    }
    
    /**
     * Sets the display index of the new work
     * @param workId
     *          The work id
     * @param displayIndex
     *          The display index for the work
     * @return true if the work index was correctly set                  
     * */
    @Override
    @Transactional
    public boolean updateToMaxDisplay(String workId, Long displayIndex) {
        Query query = entityManager.createNativeQuery("UPDATE work SET display_index=:index WHERE work_id=:workId");
        query.setParameter("index", displayIndex);
        query.setParameter("workId", Long.valueOf(workId));
        return query.executeUpdate() > 0;
    }
    
    
    /**
     * Returns a list of work ids of works that still have old external identifiers
     * @param limit
     *          The batch number to fetch
     * @return a list of work ids with old ext ids          
     * */
    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getWorksWithOldExtIds(long limit) {
        Query query = entityManager.createNativeQuery("SELECT distinct(work_id) FROM (SELECT work_id, json_array_elements(json_extract_path(external_ids_json, 'workExternalIdentifier')) AS j FROM work where external_ids_json is not null limit :limit) AS a WHERE (j->'relationship') is null");
        query.setParameter("limit", limit);
        return query.getResultList();
    }
}

