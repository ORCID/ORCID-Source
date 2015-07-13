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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileWorkDao;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.keys.ProfileWorkEntityPk;
import org.springframework.transaction.annotation.Transactional;

public class ProfileWorkDaoImpl extends GenericDaoImpl<ProfileWorkEntity, ProfileWorkEntityPk> implements ProfileWorkDao {

    public ProfileWorkDaoImpl() {
        super(ProfileWorkEntity.class);
    }

    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    @Override
    @Transactional
    public boolean removeWork(String clientOrcid, String workId) {
        Query query = entityManager.createQuery("delete from ProfileWorkEntity where profile.id=:clientOrcid and work.id=:workId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("workId", Long.valueOf(workId));
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    @Override
    @Transactional
    public boolean removeWorks(String clientOrcid, ArrayList<Long> workIds) {
        Query query = entityManager.createQuery("delete from ProfileWorkEntity where profile.id=:clientOrcid and work.id in (:workIds)");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("workIds", workIds);
        return query.executeUpdate() > 0 ? true : false;
    }

    
    /**
     * Updates the visibility of an existing profile work relationship
     * @param workId
     *            The id of the work that will be updated
     * @param visibility
     *            The new visibility value for the profile work relationship
     * @return true if the relationship was updated
     * */
    @Override
    @Transactional
    public boolean updateVisibility(String orcid, String workId, Visibility visibility) {
        Query query = entityManager
                .createQuery("update ProfileWorkEntity set visibility=:visibility, lastModified=now(), migrated=true where work.id=:workId and  profile.id=:orcid");
        query.setParameter("workId", Long.valueOf(workId));
        query.setParameter("visibility", visibility);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    public boolean updateVisibilities(String orcid, ArrayList<Long> workIds, Visibility visibility) {
        Query query = entityManager
                .createQuery("update ProfileWorkEntity set visibility=:visibility, lastModified=now() where work.id in (:workIds) and  profile.id=:orcid");
        query.setParameter("workIds", workIds);
        query.setParameter("visibility", visibility);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0 ? true : false;
    }
    
    /**
     * Get the profile work associated with the client orcid and the workId
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param workId
     *            The id of the work that will be updated
     * 
     * @return the profileWork object
     * */
    @Override
    @Transactional
    public ProfileWorkEntity getProfileWork(String clientOrcid, String workId) {
        Query query = entityManager.createQuery("from ProfileWorkEntity where profile.id=:clientOrcid and work.id=:workId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("workId", Long.valueOf(workId));
        return (ProfileWorkEntity) query.getSingleResult();
    }

    /**
     * Creates a new profile entity relationship between the provided work and
     * the given profile.
     * 
     * @param orcid
     *            The profile id
     * 
     * @param workId
     *            The work id
     * 
     * @param visibility
     *            The work visibility
     * 
     * @return true if the profile work relationship was created
     * */
    @Override
    @Transactional
    public boolean addProfileWork(String orcid, long workId, Visibility visibility, String sourceOrcid, String clientSourceId) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO profile_work(orcid, work_id, date_created, last_modified, added_to_profile_date, visibility, source_id, client_source_id, migrated) values(:orcid, :workId, now(), now(), now(), :visibility, :sourceId, :clientSourceId, true)");
        query.setParameter("orcid", orcid);
        query.setParameter("workId", workId);
        query.setParameter("visibility", visibility.name());
        query.setParameter("sourceId", sourceOrcid);
        query.setParameter("clientSourceId", clientSourceId);

        return query.executeUpdate() > 0 ? true : false;
    }

    
    /**
     * Find the list of orcids where at least one of his works have contributors
     * but the credit name is null
     * 
     * @param chunkSize
     *            the number of orcids to fetch
     * @return A list of orcid's where at least one of his works have
     *         contributors but the credit name is null
     * */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> findOrcidsWhereWorkContributorCreditNameIsNull(int chunkSize) {
        StringBuilder builder = new StringBuilder("SELECT DISTINCT pw.orcid FROM profile_work pw");
        builder.append(" JOIN work w ON w.work_id = pw.work_id AND w.contributors_json IS NOT NULL AND w.contributors_json like '\"creditName\":null'");        
        Query query = entityManager.createNativeQuery(builder.toString());
        query.setMaxResults(chunkSize);
        return query.getResultList();
    }
    
    
    @Override
    @Transactional
    public boolean updateToMaxDisplay(String orcid, String workId) {
        Query query = entityManager.createNativeQuery("UPDATE profile_work SET display_index = (select coalesce(MAX(display_index) + 1, 0) from profile_work where orcid=:orcid and work_id != :workId ) WHERE work_id = :workId");
        query.setParameter("orcid", orcid);
        query.setParameter("workId", Long.valueOf(workId));
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Deletes all works where the source matches the give app id
     * @param clientSourceId the app id
     * */
    @Override
    @Transactional
    public void removeWorksByClientSourceId(String clientSourceId) {
        Query query = entityManager.createNativeQuery("DELETE FROM profile_work WHERE client_source_id=:clientSourceId");
        query.setParameter("clientSourceId", clientSourceId);
        query.executeUpdate();        
    }            
    
    /**
     * Get a list of profile_works that have not been migrated to the works table yet
     * @param chunkSize
     *          The number of profile_works to fetch
     * @return a list of profile_works to migrate
     * */
    @Override
    @Transactional
    public List<ProfileWorkEntity> getNonMigratedProfileWorks(int chunkSize) {
        TypedQuery<ProfileWorkEntity> query = entityManager.createQuery("FROM ProfileWorkEntity WHERE migrated=false", ProfileWorkEntity.class);
        query.setMaxResults(chunkSize);
        return query.getResultList();        
    }

    /**
     * Mark a profile_work as migrated
     * @param orcid
     *          The work owner
     * @param workId
     *          The work id 
     * @return true if the profile work was correctly set as migrated         
     * */
    @Override
    @Transactional
    public boolean setProfileWorkAsMigrated(String orcid, Long workId) {
        Query query = entityManager.createNativeQuery("UPDATE profile_work SET migrated=true WHERE orcid=:orcid and work_id=:workId");
        query.setParameter("orcid", orcid);
        query.setParameter("workId", workId);
        return query.executeUpdate() > 0;
    }
    
    @Override
    public boolean exists(String orcid, String workId) {
        Query query = entityManager.createQuery("from ProfileWorkEntity where profile.id=:clientOrcid and work.id=:workId");
        query.setParameter("clientOrcid", orcid);
        query.setParameter("workId", Long.valueOf(workId));
        List results = query.getResultList();
        if(results == null || results.isEmpty())
            return false;
        return true;
    }
}
