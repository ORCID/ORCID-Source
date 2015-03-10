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
package org.orcid.core.manager.impl;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileWorkDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.transaction.annotation.Transactional;

public class ProfileWorkManagerImpl implements ProfileWorkManager {

    @Resource
    private ProfileWorkDao profileWorkDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private WorkDao workDao;

    @Resource
    private JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param clientOrcid
     *            The client orcid
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @return true if the relationship was deleted
     * */
    @Override
    public boolean removeWork(String clientOrcid, String workId) {
        return profileWorkDao.removeWork(clientOrcid, workId);
    }

    @Override
    public boolean checkSourceAndRemoveWork(String orcid, String workId) {
        ProfileWorkEntity profileWorkEntity = profileWorkDao.getProfileWork(orcid, workId);
        SourceEntity existingSource = profileWorkEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        return profileWorkDao.removeWork(orcid, workId);
    }

    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param clientOrcid
     *            The client orcid
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @return true if the relationship was deleted
     * */
    @Override
    public boolean removeWorks(String clientOrcid, ArrayList<Long> workIds) {
        return profileWorkDao.removeWorks(clientOrcid, workIds);
    }

    /**
     * Updates the visibility of an existing profile work relationship
     * 
     * @param workId
     *            The id of the work that will be updated
     * @param visibility
     *            The new visibility value for the profile work relationship
     * @return true if the relationship was updated
     * */
    public boolean updateVisibility(String orcid, String workId, Visibility visibility) {
        return profileWorkDao.updateVisibility(orcid, workId, visibility);
    }

    /**
     * Updates the visibility of an existing profile work relationship
     * 
     * @param workId
     *            The id of the work that will be updated
     * @param visibility
     *            The new visibility value for the profile work relationship
     * @return true if the relationship was updated
     * */
    public boolean updateVisibilities(String orcid, ArrayList<Long> workIds, Visibility visibility) {
        return profileWorkDao.updateVisibilities(orcid, workIds, visibility);
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
    public ProfileWorkEntity getProfileWork(String clientOrcid, String workId) {
        return profileWorkDao.getProfileWork(clientOrcid, workId);
    }

    @Override
    public Work getWork(String orcid, String workId) {
        return jpaJaxbWorkAdapter.toWork(profileWorkDao.getProfileWork(orcid, workId));
    }

    @Override
    public WorkSummary getWorkSummary(String orcid, String workId) {
        return jpaJaxbWorkAdapter.toWorkSummary(profileWorkDao.getProfileWork(orcid, workId));
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
    public boolean addProfileWork(String orcid, long workId, Visibility visibility, String sourceOrcid) {
        return profileWorkDao.addProfileWork(orcid, workId, visibility, sourceOrcid);
    }

    public boolean updateToMaxDisplay(String orcid, String workId) {
        return profileWorkDao.updateToMaxDisplay(orcid, workId);
    }

    @Override
    @Transactional
    public Work createWork(String orcid, Work work) {
        ProfileWorkEntity profileWorkEntity = jpaJaxbWorkAdapter.toProfileWorkEntity(work);
        profileWorkEntity.setSource(sourceManager.retrieveSourceEntity());
        ProfileEntity profile = profileDao.find(orcid);
        profileWorkEntity.setProfile(profile);
        setIncomingWorkPrivacy(profileWorkEntity, profile);
        profileWorkDao.persist(profileWorkEntity);
        return jpaJaxbWorkAdapter.toWork(profileWorkEntity);
    }

    @Override
    @Transactional
    public Work updateWork(String orcid, Work work) {
        ProfileWorkEntity profileWorkEntity = profileWorkDao.getProfileWork(orcid, work.getPutCode());
        Visibility originalVisibility = profileWorkEntity.getVisibility();
        SourceEntity existingSource = profileWorkEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        jpaJaxbWorkAdapter.toProfileWorkEntity(work, profileWorkEntity);
        profileWorkEntity.setVisibility(originalVisibility);
        profileWorkEntity.setSource(existingSource);
        profileWorkDao.merge(profileWorkEntity);
        return jpaJaxbWorkAdapter.toWork(profileWorkEntity);
    }

    private void setIncomingWorkPrivacy(ProfileWorkEntity profileWorkEntity, ProfileEntity profile) {
        Visibility incomingWorkVisibility = profileWorkEntity.getVisibility();
        Visibility defaultWorkVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {
            if (defaultWorkVisibility.isMoreRestrictiveThan(incomingWorkVisibility)) {
                profileWorkEntity.setVisibility(defaultWorkVisibility);
            }
        } else if (incomingWorkVisibility == null) {
            profileWorkEntity.setVisibility(Visibility.PRIVATE);
        }
    }

}
