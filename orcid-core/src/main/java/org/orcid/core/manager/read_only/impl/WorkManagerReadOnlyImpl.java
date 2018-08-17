package org.orcid.core.manager.read_only.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.PutCodeFormatException;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.activities.ActivitiesGroup;
import org.orcid.core.utils.activities.ActivitiesGroupGenerator;
import org.orcid.core.utils.activities.WorkComparators;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.GroupAble;
import org.orcid.jaxb.model.record_v2.GroupableActivity;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class WorkManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements WorkManagerReadOnly {

    public static final String BULK_PUT_CODES_DELIMITER = ",";

    @Resource
    protected JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Resource
    protected OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    protected WorkDao workDao;

    protected WorkEntityCacheManager workEntityCacheManager;
    
    private static final Logger LOG = LoggerFactory.getLogger(WorkManagerReadOnlyImpl.class);

    private final Integer maxBulkSize;
    
    public WorkManagerReadOnlyImpl(@Value("${org.orcid.core.works.bulk.max:100}") Integer bulkSize) {
        this.maxBulkSize = (bulkSize == null || bulkSize > 100) ? 100 : bulkSize;
    }
    
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
    public List<Work> findWorks(String orcid) {
        List<MinimizedWorkEntity> minimizedWorks = workEntityCacheManager.retrieveMinimizedWorks(orcid, getLastModified(orcid));
        return jpaJaxbWorkAdapter.toMinimizedWork(minimizedWorks);
    }

    /**
     * Find the public works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     */
    public List<Work> findPublicWorks(String orcid) {
        List<MinimizedWorkEntity> minimizedWorks = workEntityCacheManager.retrievePublicMinimizedWorks(orcid, getLastModified(orcid));
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
    public Work getWork(String orcid, Long workId) {
        WorkEntity work = workDao.getWork(orcid, workId);
        return jpaJaxbWorkAdapter.toWork(work);
    }

    @Override
    public WorkSummary getWorkSummary(String orcid, Long workId) {
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
    public List<WorkSummary> getWorksSummaryList(String orcid) {
        List<MinimizedWorkEntity> works = workEntityCacheManager.retrieveMinimizedWorks(orcid, getLastModified(orcid));
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
        groupDebug("Grouping works");
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        Works result = new Works();
        // Group all works
        for (WorkSummary work : works) {
            groupDebug("Examining work summary " + work.toString() + " (" + work.getPutCode() + ")");
            if (justPublic && !work.getVisibility().equals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC)) {
                groupDebug("Work is not public, not including in groups");
                // If it is just public and the work is not public, just ignore
                // it
            } else {
                groupDebug("Sending work summary to group generator");
                groupGenerator.group(work);
            }
        }

        List<ActivitiesGroup> groups = groupGenerator.getGroups();
        groupDebug("Received " + groups.size() + " activities groups from group generator");

        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            
            groupDebug("Creating new workGroup based on activitiesGroup");
            WorkGroup workGroup = new WorkGroup();
            // Fill the work groups with the external identifiers
            if(externalIdentifiers == null || externalIdentifiers.isEmpty()) {
                // Initialize the ids as an empty list
                workGroup.getIdentifiers().getExternalIdentifier();
            } else {
                for (GroupAble extId : externalIdentifiers) {
                    ExternalID workExtId = (ExternalID) extId;
                    groupDebug("adding external id " + workExtId.getType() + " / " + workExtId.getValue() + " to group");
                    workGroup.getIdentifiers().getExternalIdentifier().add(workExtId.clone());
                }
            }
            
            // Fill the work group with the list of activities
            for (GroupableActivity activity : activities) {
                WorkSummary workSummary = (WorkSummary) activity;
                groupDebug("Adding work summary " + workSummary + " to group");
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

    private void groupDebug(String string) {
        if (Features.WORK_GROUP_LOGGING.isActive()) {
            LOG.info("### WORKMANAGER GROUP LOGGING: " + string);
        }
    }

    @Override
    public WorkBulk findWorkBulk(String orcid, String putCodesAsString) {
        List<BulkElement> works = new ArrayList<>();        
        List<Long> putCodes = Arrays.stream(getPutCodeArray(putCodesAsString)).map(s -> Long.parseLong(s)).collect(Collectors.toList());        
        List<WorkEntity> entities = workEntityCacheManager.retrieveFullWorks(orcid, putCodes);
        
        for(WorkEntity entity : entities) {
            works.add(jpaJaxbWorkAdapter.toWork(entity));
            putCodes.remove(entity.getId());
        }
        
        // Put codes still in this list doesn't exists on the database
        for(Long invalidPutCode : putCodes) {
            works.add(orcidCoreExceptionMapper.getOrcidError(new PutCodeFormatException("'" + invalidPutCode + "' is not a valid put code")));
        }
        
        WorkBulk bulk = new WorkBulk();
        bulk.setBulk(works);
        return bulk;
    }

    private String[] getPutCodeArray(String putCodesAsString) {
        String[] putCodeArray = putCodesAsString.split(BULK_PUT_CODES_DELIMITER);
        if (putCodeArray.length > maxBulkSize) {
            throw new ExceedMaxNumberOfPutCodesException(maxBulkSize);
        }
        return putCodeArray;
    }

}
