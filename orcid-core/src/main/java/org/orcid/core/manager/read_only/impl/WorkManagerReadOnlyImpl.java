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
package org.orcid.core.manager.read_only.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.PutCodeFormatException;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.activities.ActivitiesGroup;
import org.orcid.core.utils.activities.ActivitiesGroupGenerator;
import org.orcid.core.utils.activities.WorkComparators;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.BulkElement;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.GroupAble;
import org.orcid.jaxb.model.record_v2.GroupableActivity;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.springframework.cache.annotation.Cacheable;

public class WorkManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements WorkManagerReadOnly {

    public static final String BULK_PUT_CODES_DELIMITER = ",";

    public static final Integer MAX_BULK_PUT_CODES = 50;

    @Resource
    protected JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Resource
    protected OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    protected WorkDao workDao;

    protected WorkEntityCacheManager workEntityCacheManager;

    public void setWorkDao(WorkDao workDao) {
        this.workDao = workDao;
    }

    public void setWorkEntityCacheManager(WorkEntityCacheManager workEntityCacheManager) {
        this.workEntityCacheManager = workEntityCacheManager;
    }

    /**
     * Find the works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     */
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
     */
    @Cacheable(value = "public-works", key = "#orcid.concat('-').concat(#lastModified)")
    public List<Work> findPublicWorks(String orcid, long lastModified) {
        List<MinimizedWorkEntity> minimizedWorks = workEntityCacheManager.retrievePublicMinimizedWorks(orcid, lastModified);
        return jpaJaxbWorkAdapter.toMinimizedWork(minimizedWorks);
    }

    /**
     * Get the given Work from the database
     * 
     * @param orcid
     *            The work owner
     * @param workId
     *            The work id
     */
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

    /**
     * Get the list of works that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *            Last modified date used to check the cache
     * @return the list of works that belongs to this user
     */
    @Override
    @Cacheable(value = "works-summaries", key = "#orcid.concat('-').concat(#lastModified)")
    public List<WorkSummary> getWorksSummaryList(String orcid, long lastModified) {
        List<MinimizedWorkEntity> works = workEntityCacheManager.retrieveMinimizedWorks(orcid, lastModified);
        return jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(works);
    }

    /**
     * Generate a grouped list of works with the given list of works
     * 
     * @param works
     *            The list of works to group
     * @param justPublic
     *            Specify if we want to group only the public elements in the
     *            given list
     * @return Works element with the WorkSummary elements grouped
     */
    @Override
    public Works groupWorks(List<WorkSummary> works, boolean justPublic) {
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        Works result = new Works();
        // Group all works
        for (WorkSummary work : works) {
            if (justPublic && !work.getVisibility().equals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC)) {
                // If it is just public and the work is not public, just ignore
                // it
            } else {
                groupGenerator.group(work);
            }
        }

        List<ActivitiesGroup> groups = groupGenerator.getGroups();

        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            WorkGroup workGroup = new WorkGroup();
            // Fill the work groups with the external identifiers
            if(externalIdentifiers == null || externalIdentifiers.isEmpty()) {
                // Initialize the ids as an empty list
                workGroup.getIdentifiers().getExternalIdentifier();
            } else {
                for (GroupAble extId : externalIdentifiers) {
                    ExternalID workExtId = (ExternalID) extId;
                    workGroup.getIdentifiers().getExternalIdentifier().add(workExtId.clone());
                }
            }
            
            // Fill the work group with the list of activities
            for (GroupableActivity activity : activities) {
                WorkSummary workSummary = (WorkSummary) activity;
                workGroup.getWorkSummary().add(workSummary);
            }

            // Sort the works
            workGroup.getWorkSummary().sort(WorkComparators.ALL);
            result.getWorkGroup().add(workGroup);
        }
        // Sort the groups!
        result.getWorkGroup().sort(WorkComparators.GROUP);
        return result;
    }

    @Override
    public WorkBulk findWorkBulk(String orcid, String putCodesAsString, long profileLastModified) {
        List<BulkElement> works = new ArrayList<>();
        String[] putCodes = getPutCodeArray(putCodesAsString);
        for (String putCode : putCodes) {
            try {
                Long id = Long.valueOf(putCode);
                WorkEntity workEntity = workEntityCacheManager.retrieveFullWork(orcid, id, profileLastModified);
                works.add(jpaJaxbWorkAdapter.toWork(workEntity));
            } catch (Exception e) {
                works.add(orcidCoreExceptionMapper.getOrcidError(new PutCodeFormatException("'" + putCode + "' is not a valid put code")));
            }
        }
        WorkBulk bulk = new WorkBulk();
        bulk.setBulk(works);
        return bulk;
    }

    private String[] getPutCodeArray(String putCodesAsString) {
        String[] putCodeArray = putCodesAsString.split(BULK_PUT_CODES_DELIMITER);
        if (putCodeArray.length > MAX_BULK_PUT_CODES) {
            throw new ExceedMaxNumberOfPutCodesException(MAX_BULK_PUT_CODES);
        }
        return putCodeArray;
    }

}
