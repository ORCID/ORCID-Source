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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.manager.validator.ExternalIDValidator;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.notification.amended_rc2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_rc2.Item;
import org.orcid.jaxb.model.notification.permission_rc2.ItemType;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class WorkManagerImpl implements WorkManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkManagerImpl.class);

    @Resource
    private WorkDao workDao;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    private JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private WorkEntityCacheManager workEntityCacheManager;

    @Resource
    private NotificationManager notificationManager;
    
    @Resource 
    private ExternalIDValidator externalIDValidator;

    @Resource 
    private ActivityValidator activityValidator;

    @Override
    public void setSourceManager(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    /**
     * Find the works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     * */
    @Cacheable(value = "works", key = "#orcid.concat('-').concat(#lastModified)")
    public List<Work> findWorks(String orcid, long lastModified) {
        List<MinimizedWorkEntity> minimizedWorks = workEntityCacheManager.retrieveMinimizedWorks(orcid, lastModified);
        return jpaJaxbWorkAdapter.toMinimizedWork(minimizedWorks);
    }

    /**
     * Find the public works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     * */
    @Cacheable(value = "public-works", key = "#orcid.concat('-').concat(#lastModified)")
    public List<Work> findPublicWorks(String orcid, long lastModified) {
        List<MinimizedWorkEntity> minimizedWorks = workEntityCacheManager.retrievePublicMinimizedWorks(orcid, lastModified);
        return jpaJaxbWorkAdapter.toMinimizedWork(minimizedWorks);
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
    public boolean updateVisibilities(String orcid, List<Long> workIds, Visibility visibility) {
        return workDao.updateVisibilities(orcid, workIds, visibility);
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
    public boolean removeWorks(String clientOrcid, List<Long> workIds) {
        return workDao.removeWorks(clientOrcid, workIds);
    }

    /**
     * Sets the display index of the new work
     * 
     * @param orcid
     *            The work owner
     * @param workId
     *            The work id
     * @return true if the work index was correctly set
     * */
    public boolean updateToMaxDisplay(String orcid, Long workId) {
        return workDao.updateToMaxDisplay(orcid, workId);
    }

    /**
     * Get the given Work from the database
     * 
     * @param orcid
     *            The work owner
     * @param workId
     *            The work id
     * */
    @Override
    @Cacheable(value = "single-work", key = "#orcid.concat('-').concat(#workId).concat('-').concat(#lastModified)")
    public Work getWork(String orcid, Long workId, long lastModified) {
        WorkEntity work = workDao.getWork(orcid, workId);
        return jpaJaxbWorkAdapter.toWork(work);
    }

    @Override
    @Cacheable(value = "single-work-summary", key = "#orcid.concat('-').concat(#workId).concat('-').concat(#lastModified)")
    public WorkSummary getWorkSummary(String orcid, Long workId, long lastModified) {
        WorkEntity work = workDao.getWork(orcid, workId);
        return jpaJaxbWorkAdapter.toWorkSummary(work);
    }

    @Override
    @Transactional
    public Work createWork(String orcid, Work work, boolean applyAPIValidations) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        
        if (applyAPIValidations) {
            activityValidator.validateWork(work, sourceEntity, true, applyAPIValidations, null);
            // If it is the user adding the peer review, allow him to add
            // duplicates
            if (!sourceEntity.getSourceId().equals(orcid)) {
                Date lastModified = profileEntityManager.getLastModified(orcid);
                long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();
                List<Work> existingWorks = this.findWorks(orcid, lastModifiedTime);       
                if (existingWorks != null) {
                    for (Work existing : existingWorks) {
                        activityValidator.checkExternalIdentifiersForDuplicates(work.getExternalIdentifiers(), existing.getExternalIdentifiers(), existing.getSource(),
                                sourceEntity);
                    }
                }
            }
        } else {
            // validate external ID vocab
            externalIDValidator.validateWorkOrPeerReview(work.getExternalIdentifiers());
        }

        WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        workEntity.setProfile(profile);
        workEntity.setAddedToProfileDate(new Date());
        
        // Set source id 
        if(sourceEntity.getSourceProfile() != null) {
            workEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        
        if(sourceEntity.getSourceClient() != null) {
            workEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        } 
        
        setIncomingWorkPrivacy(workEntity, profile);
        workEntity.setDisplayIndex(0L);
        //workDao.increaseDisplayIndexOnAllElements(orcid);
        workDao.persist(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItem(workEntity));
        return jpaJaxbWorkAdapter.toWork(workEntity);
    }

    @Override
    @Transactional
    public Work updateWork(String orcid, Work work, boolean applyAPIValidations) {
        WorkEntity workEntity = workDao.getWork(orcid, work.getPutCode());
        Visibility originalVisibility = Visibility.fromValue(workEntity.getVisibility().value());
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        
        //Save the original source
        String existingSourceId = workEntity.getSourceId();
        String existingClientSourceId = workEntity.getClientSourceId();
        
        if (applyAPIValidations) {
            activityValidator.validateWork(work, sourceEntity, false, applyAPIValidations, workEntity.getVisibility());
            
            Date lastModified = profileEntityManager.getLastModified(orcid);
            long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();
            List<Work> existingWorks = this.findWorks(orcid, lastModifiedTime);       
            
            for (Work existing : existingWorks) {
                // Dont compare the updated peer review with the DB version
                if (!existing.getPutCode().equals(work.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(work.getExternalIdentifiers(), existing.getExternalIdentifiers(), existing.getSource(), sourceEntity);
                }
            }
        }else{
            //validate external ID vocab
            externalIDValidator.validateWorkOrPeerReview(work.getExternalIdentifiers());            
        }
                        
        orcidSecurityManager.checkSource(workEntity);
        jpaJaxbWorkAdapter.toWorkEntity(work, workEntity);
        workEntity.setVisibility(org.orcid.jaxb.model.message.Visibility.fromValue(originalVisibility.value()));
        
        //Be sure it doesn't overwrite the source
        workEntity.setSourceId(existingSourceId);
        workEntity.setClientSourceId(existingClientSourceId);
        
        workDao.merge(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItem(workEntity));
        return jpaJaxbWorkAdapter.toWork(workEntity);
    }

    @Override
    public boolean checkSourceAndRemoveWork(String orcid, Long workId) {
        boolean result = true;
        WorkEntity workEntity = workDao.getWork(orcid, workId);
        orcidSecurityManager.checkSource(workEntity);
        try {
            Item item = createItem(workEntity);
            workDao.removeWork(orcid, workId);
            workDao.flush();
            notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, item);
        } catch (Exception e) {
            LOGGER.error("Unable to delete work with ID: " + workId);
            result = false;
        }
        return result;
    }

    private void setIncomingWorkPrivacy(WorkEntity workEntity, ProfileEntity profile) {
        org.orcid.jaxb.model.message.Visibility incomingWorkVisibility = workEntity.getVisibility();
        org.orcid.jaxb.model.message.Visibility defaultWorkVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {
            workEntity.setVisibility(defaultWorkVisibility);            
        } else if (incomingWorkVisibility == null) {
            workEntity.setVisibility(org.orcid.jaxb.model.message.Visibility.PRIVATE);
        }
    }

    /**
     * Get the list of works that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *            Last modified date used to check the cache
     * @return the list of works that belongs to this user
     * */
    @Override
    @Cacheable(value = "works-summaries", key = "#orcid.concat('-').concat(#lastModified)")
    public List<WorkSummary> getWorksSummaryList(String orcid, long lastModified) {
        List<MinimizedWorkEntity> works = workEntityCacheManager.retrieveMinimizedWorks(orcid, lastModified);
        return jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(works);
    }

    private Item createItem(WorkEntity workEntity) {
        Item item = new Item();
        item.setItemName(workEntity.getTitle());
        item.setItemType(ItemType.WORK);
        item.setPutCode(String.valueOf(workEntity.getId()));
        return item;
    }

}
