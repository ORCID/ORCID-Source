/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.List;

import javax.persistence.Query;

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
     * Updates the visibility of an existing profile work relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param workId
     *            The id of the work that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile work relationship
     * 
     * @return true if the relationship was updated
     * */
    @Override
    @Transactional
    public boolean updateWork(String clientOrcid, String workId, Visibility visibility) {
        Query query = entityManager
                .createQuery("update ProfileWorkEntity set visibility=:visibility, lastModified=now() where profile.id=:clientOrcid and work.id=:workId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("workId", Long.valueOf(workId));
        query.setParameter("visibility", visibility);
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
    public boolean addProfileWork(String orcid, long workId, Visibility visibility, String sourceOrcid) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO profile_work(orcid, work_id, date_created, last_modified, added_to_profile_date, visibility, source_id) values(:orcid, :workId, now(), now(), now(), :visibility, :sourceId)");
        query.setParameter("orcid", orcid);
        query.setParameter("workId", workId);
        query.setParameter("visibility", visibility.name());
        query.setParameter("sourceId", sourceOrcid);

        return query.executeUpdate() > 0 ? true : false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findOrcidsNeedingWorkContributorMigration(int chunkSize) {
        StringBuilder builder = new StringBuilder("SELECT DISTINCT pw.orcid FROM profile_work pw");
        builder.append(" JOIN work w ON w.work_id = pw.work_id AND w.contributors_json IS NULL");
        builder.append(" JOIN work_contributor wc ON wc.work_id = pw.work_id");
        Query query = entityManager.createNativeQuery(builder.toString());
        query.setMaxResults(chunkSize);
        return query.getResultList();
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

}
