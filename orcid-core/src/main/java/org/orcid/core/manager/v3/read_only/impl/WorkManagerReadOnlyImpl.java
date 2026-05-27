package org.orcid.core.manager.v3.read_only.impl;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.jsonidentifier.converter.JSONWorkExternalIdentifiersConverterV3;
import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.adapter.v3.converter.WorkContributorsConverter;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.PutCodeFormatException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.manager.v3.GroupingSuggestionManager;
import org.orcid.core.manager.v3.WorksExtendedCacheManager;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.core.utils.v3.activities.*;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.record.*;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.pojo.*;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.orcid.pojo.ajaxForm.PojoUtil.getWorkForm;

public class WorkManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements WorkManagerReadOnly {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkManagerReadOnlyImpl.class);

    public static final String BULK_PUT_CODES_DELIMITER = ",";

    @Resource(name = "jpaJaxbWorkAdapterV3")
    protected JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Resource
    protected OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    protected WorkDao workDao;

    protected WorkEntityCacheManager workEntityCacheManager;

    private final Integer maxWorksToRead;

    @Resource
    private WorksExtendedCacheManager worksExtendedCacheManager;

    @Resource
    private GroupingSuggestionManager groupingSuggestionsManager;

    @Resource(name = "contributorUtilsV3")
    private ContributorUtils contributorUtils;

    @Resource(name = "workContributorsConverter")
    private WorkContributorsConverter workContributorsConverter;

    @Resource
    private JSONWorkExternalIdentifiersConverterV3 jsonWorkExternalIdentifiersConverterV3;

    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private SourceNameCacheManager sourceNameCacheManager;

    @Resource
    private ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter;

    @Resource
    private SourceEntityUtils sourceEntityUtils;

    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;

    public WorkManagerReadOnlyImpl(@Value("${org.orcid.core.works.bulk.read.max:100}") Integer bulkReadSize) {
        this.maxWorksToRead = (bulkReadSize == null) ? 100 : bulkReadSize;
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
     * Checks if there is any public work for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return true if there is at least one public work for a specific user
     */
    @Override
    public Boolean hasPublicWorks(String orcid) {
        if (PojoUtil.isEmpty(orcid)) {
            return false;
        }
        return workDao.hasPublicWorks(orcid);
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
        long t0 = System.currentTimeMillis();
        Set<String> clientIds = works.stream()
                .map(MinimizedWorkEntity::getClientSourceId)
                .filter(clientId -> !PojoUtil.isEmpty(clientId))
                .collect(Collectors.toSet());

        // Get the client details from the database
        Map<String, ClientDetailsEntity> clientDetailsById = clientDetailsEntityCacheManager.retrieveAll(clientIds);
        Map<String, Source> sources = new HashMap<>();
        works.stream().forEach(workEntity -> {
            String sourceKey = sourceEntityUtils.getSourceKey(workEntity);
            if(!sources.containsKey(sourceKey)) {
                Source source = sourceEntityUtils.extractSourceFromEntityComplete(workEntity);
                sources.put(sourceKey, source);
            }
        });
        if (clientIds.isEmpty()) {
            return jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(works);
        }

        // This map should be read-only
        Map<String, Source> readOnlySources = Collections.unmodifiableMap(sources);
        List<WorkSummary> list = jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(works, readOnlySources);
        long t1 = System.currentTimeMillis();
        System.out.println("Time to convert to JAXB WorkSummary: " + (t1 - t0));
        return list;
    }

    /**
     * Get the list of works that belongs to a user
     *
     * @param orcid
     * @param featuredOnly
     * @return the list of works that belongs to this user
     */
    @Override
    public List<WorkSummaryExtended> getWorksSummaryExtendedList(String orcid, boolean featuredOnly) {
        List<WorkSummaryExtended> wseList = retrieveWorkSummaryExtended(orcid, featuredOnly);
        // Filter the contributors list
        long t0 = System.currentTimeMillis();
        for (WorkSummaryExtended wse : wseList) {
            if (wse.getContributorsGroupedByOrcid() != null && wse.getContributorsGroupedByOrcid().size() > 0) {
                wse.setNumberOfContributors(wse.getContributorsGroupedByOrcid().size());
            } else {
                List<ContributorsRolesAndSequences> contributorsGroupedByOrcid = contributorUtils.getContributorsGroupedByOrcid(wse.getContributors().getContributor(),
                        maxContributorsForUI);
                wse.setContributorsGroupedByOrcid(contributorsGroupedByOrcid);
                wse.setNumberOfContributors(contributorsGroupedByOrcid.size());
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Time on getWorksSummaryExtendedList: " + (t1 - t0));
        return wseList;
    }

    private List<WorkSummaryExtended> retrieveWorkSummaryExtended(String orcid, boolean featuredOnly) {
        List<WorkSummaryExtended> workSummaryExtendedList = new ArrayList<>();
        Map<String, Boolean> isUserOBOEnabled = new HashMap<String, Boolean>();
        long l0 = System.currentTimeMillis();
        List<Object[]> list = workDao.getWorksByOrcid(orcid, featuredOnly);
        for (Object[] q1 : list) {
            BigInteger putCode = (BigInteger) q1[0];
            String workType = isEmpty(q1[1]);
            String title = isEmpty(q1[2]);
            String journalTitle = isEmpty(q1[3]);
            String externalIdsJson = isEmpty(q1[4]);
            String publicationYear = isEmpty(q1[5]);
            String publicationMonth = isEmpty(q1[6]);
            String publicationDay = isEmpty(q1[7]);
            String visibility = isEmpty(q1[8]);
            BigInteger displayIndex = (BigInteger) q1[9];
            String sourceId = isEmpty(q1[10]);
            String clientSourceId = isEmpty(q1[11]);
            String assertionOriginSourceId = isEmpty(q1[12]);
            String assertionOriginClientSourceId = isEmpty(q1[13]);
            Timestamp createdDate = (Timestamp) q1[14];
            Timestamp lastModifiedDate = (Timestamp) q1[15];
            String contributors = isEmpty(q1[16]);
            int featuredDisplayIndex = (int) q1[17];
            ExternalIDs externalIDs = null;
            if (externalIdsJson != null) {
                externalIDs = jsonWorkExternalIdentifiersConverterV3.convertFrom(externalIdsJson, null);
            }
            String sourceName = null;
            String assertionOriginName = null;
            if (StringUtils.isNotBlank(clientSourceId)) {
                //Set the source name
                sourceName = sourceNameCacheManager.retrieve(clientSourceId);
                // Check if user OBO is enabled
                if (!PojoUtil.isEmpty(assertionOriginSourceId)) {
                    if(!isUserOBOEnabled.containsKey(clientSourceId)) {
                        ClientDetailsEntity clientEntity = clientDetailsEntityCacheManager.retrieve(clientSourceId);
                        if(clientEntity != null && clientEntity.isUserOBOEnabled()) {
                            isUserOBOEnabled.put(clientSourceId, true);
                        } else {
                            isUserOBOEnabled.put(clientSourceId, false);
                        }
                    }
                    if(isUserOBOEnabled.get(clientSourceId)) {
                        assertionOriginName = sourceNameCacheManager.retrieve(assertionOriginSourceId);
                    }
                }
            }

            // Check the sourceId name only if there is no clientSourceId
            if (PojoUtil.isEmpty(sourceName) && !PojoUtil.isEmpty(sourceId)) {
                sourceName = sourceNameCacheManager.retrieve(sourceId);
            }

            if (!PojoUtil.isEmpty(assertionOriginClientSourceId)) {
                assertionOriginName = sourceNameCacheManager.retrieve(assertionOriginClientSourceId);
            }

            List<WorkContributorsList> contributorList = new ArrayList<>();
            List<ContributorsRolesAndSequences> contributorsRolesAndSequencesList = new ArrayList<>();

            if (contributors != null && !"".equals(contributors)) {
                contributorsRolesAndSequencesList = contributorsRolesAndSequencesConverter.getContributorsRolesAndSequencesList(contributors);
            } else {
                contributorList = workContributorsConverter.getContributorsList(contributors);
            }

            WorkSummaryExtended wse = new WorkSummaryExtended.WorkSummaryExtendedBuilder(putCode, workType, title, sourceId, clientSourceId, createdDate,
                    lastModifiedDate).journalTitle(journalTitle).externalIdsJson(externalIDs).publicationYear(publicationYear).publicationMonth(publicationMonth)
                            .publicationDay(publicationDay).visibility(visibility).sourceName(sourceName).assertionOriginName(assertionOriginName)
                            .displayIndex(displayIndex).featuredDisplayIndex(featuredDisplayIndex).assertionOriginSourceId(assertionOriginSourceId)
                            .assertionOriginClientSourceId(assertionOriginClientSourceId).contributors(contributorList).topContributors(contributorsRolesAndSequencesList)
                            .build();
            workSummaryExtendedList.add(wse);
        }
        long l1 = System.currentTimeMillis();
        System.out.println("Time to retrieve WorkSummaryExtended: " + (l1 - l0));
        return workSummaryExtendedList;
    }

    /**
     * Get the list of works specified by the list of put codes
     * 
     * @param userOrcid
     * @param lastModified
     *            Last modified date used to check the cache
     * @return the list of works that belongs to this user
     */
    @Override
    public List<WorkSummary> getWorksSummaryList(String orcid, List<Long> putCodes) {
        List<MinimizedWorkEntity> works = workEntityCacheManager.retrieveMinimizedWorks(orcid, putCodes, getLastModified(orcid));
        return jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(works);
    }

    /**
     * Generate a grouped list of works with the given list of works and
     * generates grouping suggestions
     * 
     * @param works
     *            The list of works to group
     * @return Works element with the WorkSummary elements grouped
     */
    @Override
    public Works groupWorksAndGenerateGroupingSuggestions(List<WorkSummary> summaries, String orcid) {
        WorkGroupAndGroupingSuggestionGenerator groupGenerator = new WorkGroupAndGroupingSuggestionGenerator();
        for (WorkSummary work : summaries) {
            groupGenerator.group(work);
        }
        Works works = processGroupedWorks(groupGenerator.getGroups());
        List<WorkGroupingSuggestion> suggestions = groupGenerator.getGroupingSuggestions(orcid);
        groupingSuggestionsManager.cacheGroupingSuggestions(orcid, suggestions);
        return works;
    }

    @Override
    public WorksExtended groupWorksExtendedAndGenerateGroupingSuggestions(List<WorkSummaryExtended> summaries, String orcid) {
        long t0 = System.currentTimeMillis();
        long t02 = System.currentTimeMillis();
        WorkGroupAndGroupingSuggestionGeneratorJunie groupGenerator = new WorkGroupAndGroupingSuggestionGeneratorJunie();
        for (WorkSummaryExtended work : summaries) {
            if(work.getTitle().getTitle().getContent().equals("Title 1000000011") || work.getTitle().getTitle().getContent().equals("Title 1000000015") || work.getTitle().getTitle().getContent().equals("Title 1000000045") || work.getTitle().getTitle().getContent().equals("Title 1000000053")) {
                System.out.println("PROCESSING: " + work.getTitle().getTitle().getContent());
            }
            groupGenerator.group(work);
        }
        long t03 = System.currentTimeMillis();
        System.out.println("GROUPING WITH JUNIE: " + (t03 - t02));
        long t1 = System.currentTimeMillis();
        WorksExtended works = processGroupedWorksExtended(groupGenerator.getGroups());
        long t2 = System.currentTimeMillis();
        List<WorkGroupingSuggestion> suggestions = groupGenerator.getGroupingSuggestions(orcid);
        long t3 = System.currentTimeMillis();
        groupingSuggestionsManager.cacheGroupingSuggestions(orcid, suggestions);
        long t4 = System.currentTimeMillis();
        System.out.println("Time on groupWorksExtendedAndGenerateGroupingSuggestions 1: " + (t1 - t0));
        System.out.println("Time on groupWorksExtendedAndGenerateGroupingSuggestions 2: " + (t2 - t1));
        System.out.println("Time on groupWorksExtendedAndGenerateGroupingSuggestions 3: " + (t3 - t2));
        System.out.println("Time on groupWorksExtendedAndGenerateGroupingSuggestions 4: " + (t4 - t3));
        return works;
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
        for (WorkSummary work : works) {
            if (justPublic && !work.getVisibility().equals(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC)) {
                // If it is just public and the work is not public, just ignore
                // it
            } else {
                groupGenerator.group(work);
            }
        }
        return processGroupedWorks(groupGenerator.getGroups());
    }

    @Override
    public WorkBulk findWorkBulk(String orcid, String putCodesAsString) {
        List<BulkElement> works = new ArrayList<>();
        List<Long> putCodes = Arrays.stream(getPutCodeArray(putCodesAsString)).map(s -> Long.parseLong(s)).collect(Collectors.toList());
        List<WorkEntity> entities = new ArrayList<>();

        if (Features.READ_BULK_WORKS_DIRECTLY_FROM_DB.isActive()) {
            entities = workDao.getWorkEntities(orcid, putCodes);
        } else {
            entities = workEntityCacheManager.retrieveFullWorks(orcid, putCodes);
        }

        for (WorkEntity entity : entities) {
            works.add(jpaJaxbWorkAdapter.toWork(entity));
            putCodes.remove(entity.getId());
        }

        // Put codes still in this list doesn't exists on the database
        for (Long invalidPutCode : putCodes) {
            works.add(orcidCoreExceptionMapper.getV3OrcidError(new PutCodeFormatException("'" + invalidPutCode + "' is not a valid put code")));
        }

        WorkBulk bulk = new WorkBulk();
        bulk.setBulk(works);
        return bulk;
    }

    @Override
    public Works getWorksAsGroups(String orcid) {
        return groupWorksAndGenerateGroupingSuggestions(getWorksSummaryList(orcid), orcid);
    }

    @Override
    public WorksExtended getWorksExtendedAsGroups(String orcid) {
        return groupWorksExtendedAndGenerateGroupingSuggestions(getWorksSummaryExtendedList(orcid, false), orcid);
    }

    @Override
    public List<WorkSummaryExtended> getFeaturedWorksSummaryExtended(String orcid) {
        List<WorkSummaryExtended> featured = getWorksSummaryExtendedList(orcid, true);
        if (featured != null && !featured.isEmpty()) {
            featured.sort((a, b) -> Integer.compare(a.getFeaturedDisplayIndex(), b.getFeaturedDisplayIndex()));
        }
        return featured;
    }

    @Override
    public List<WorkForm> getFeaturedWorks(String orcid) {
        List<WorkSummaryExtended> works = worksExtendedCacheManager.getFeaturedGroupedWorksExtended(orcid);
        List<WorkForm> workForms = new ArrayList<>();
        if (!works.isEmpty()) {
            for (WorkSummaryExtended workSummary : works) {
                workForms.add(getWorkForm(workSummary));
            }
        }
        return workForms;
    }

    private String[] getPutCodeArray(String putCodesAsString) {
        String[] putCodeArray = putCodesAsString.split(BULK_PUT_CODES_DELIMITER);
        if (putCodeArray.length > maxWorksToRead) {
            throw new ExceedMaxNumberOfPutCodesException(maxWorksToRead);
        }
        return putCodeArray;
    }

    @Override
    public List<Work> findWorks(String orcid, List<WorkLastModifiedEntity> elements) {
        List<Work> result = new ArrayList<Work>();
        for (WorkLastModifiedEntity w : elements) {
            WorkEntity entity = workEntityCacheManager.retrieveFullWork(orcid, w.getId(), w.getLastModified().getTime());
            result.add(jpaJaxbWorkAdapter.toWork(entity));
        }
        return result;
    }

    @Override
    public WorkExtended getWorkExtended(String orcid, Long workId) {
        WorkEntity work = workDao.getWork(orcid, workId);
        return jpaJaxbWorkAdapter.toWorkExtended(work);
    }

    @Override
    public ExternalIDs getAllExternalIDs(String orcid) {
        List<WorkSummary> summaries = getWorksSummaryList(orcid);
        ExternalIDs ids = new ExternalIDs();
        for (WorkSummary s : summaries) {
            for (ExternalID id : s.getExternalIdentifiers().getExternalIdentifier()) {
                if (!ids.getExternalIdentifier().contains(id)) {
                    ids.getExternalIdentifier().add(id);
                }
            }
        }
        return ids;
    }

    public List<ActivityTitle> getWorksTitle(String orcid) {
        WorksExtended worksExtended = worksExtendedCacheManager.getGroupedWorksExtended(orcid);
        List<ActivityTitle> titles = new ArrayList<>();
        if (worksExtended != null && worksExtended.getWorkGroup() != null && !worksExtended.getWorkGroup().isEmpty()) {
            for (WorkGroupExtended wg : worksExtended.getWorkGroup()) {
                List<WorkSummaryExtended> orderedList = new ArrayList<>(wg.getWorkSummary());
                orderedList.sort((a, b) -> Long.compare(Long.valueOf(b.getDisplayIndex()), Long.valueOf(a.getDisplayIndex())));
                for (int i = 0; i < orderedList.size(); i++) {
                    WorkSummaryExtended w = orderedList.get(i);
                    ActivityTitle title = new ActivityTitle();
                    title.setPutCode(w.getPutCode());
                    // If this is the first element, it is the one with the
                    // highest display index
                    title.setDefault(i == 0);
                    if (w.getTitle() != null && w.getTitle().getTitle() != null && !PojoUtil.isEmpty(w.getTitle().getTitle().getContent())) {
                        title.setTitle(w.getTitle().getTitle().getContent());
                    } else {
                    	title.setTitle("");
                    }

                    title.setPublic(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC.equals(w.getVisibility()));

                    title.setFeaturedDisplayIndex(w.getFeaturedDisplayIndex());
                    if (w.getPublicationDate() != null) {
                        PublicationDate pd = w.getPublicationDate();
                        if (pd.getYear() != null && !PojoUtil.isEmpty(pd.getYear().getValue())) {
                            title.setPublicationYear(pd.getYear().getValue());
                        }
                        if (pd.getMonth() != null && !PojoUtil.isEmpty(pd.getMonth().getValue())) {
                            title.setPublicationMonth(pd.getMonth().getValue());
                        }
                        if (pd.getDay() != null && !PojoUtil.isEmpty(pd.getDay().getValue())) {
                            title.setPublicationDay(pd.getDay().getValue());
                        }
                    }
                    if (w.getType()!= null && !PojoUtil.isEmpty(w.getType().value())) {
                        title.setWorkType(w.getType().value()); 
                    }
                    if (w.getJournalTitle() != null && !PojoUtil.isEmpty(w.getJournalTitle().getContent())) {
                        title.setJournalTitle(w.getJournalTitle().getContent());
                    }
                    titles.add(title);
                }
            }
        }

        return titles;

    }

    @Override
    public ActivityTitleSearchResult searchWorksTitle(String orcid, String searchTerm, int size, int offset, boolean publicOnly, boolean defaultOnly) {

        ActivityTitleSearchResult result = new ActivityTitleSearchResult(new ArrayList<>(), 0, 0);
        if (PojoUtil.isEmpty(searchTerm)) {
            return new ActivityTitleSearchResult (new ArrayList<>(), 0,0);
        }

        List<ActivityTitle> allTitles = getWorksTitle(orcid); 
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        List<ActivityTitle> filteredTitles = allTitles.stream().filter(t -> t.getTitle() != null && t.getTitle().toLowerCase().contains(lowerCaseSearchTerm))
                .collect(Collectors.toList());
        if (publicOnly) {
            filteredTitles = filteredTitles.stream().filter(t -> t.isPublic()).collect(Collectors.toList());
        }
        
        if (defaultOnly) {
            filteredTitles = filteredTitles.stream().filter(t -> t.isDefault()).collect(Collectors.toList());
        }
        
        int totalCount = filteredTitles.size();
        
        if (filteredTitles.size() > size) {
            int fromIndex = Math.min(offset, filteredTitles.size());
            int toIndex = Math.min(fromIndex + size, filteredTitles.size());
            filteredTitles = filteredTitles.subList(fromIndex, toIndex);
        }
        result.setResults(filteredTitles);
        result.setTotalCount(totalCount);
        result.setOffset(offset);
        result.setPageSize(size);
        
        return result;
    }

    private Works processGroupedWorks(List<ActivitiesGroup> groups) {
        Works result = new Works();
        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            WorkGroup workGroup = new WorkGroup();
            // Fill the work groups with the external identifiers
            if (externalIdentifiers == null || externalIdentifiers.isEmpty()) {
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
            workGroup.getWorkSummary().sort(WorkComparators.WORKS_WITHIN_GROUP);
            result.getWorkGroup().add(workGroup);
        }
        // Sort the groups!
        result.getWorkGroup().sort(WorkComparators.GROUP);
        return result;
    }

    private WorksExtended processGroupedWorksExtended(List<ActivitiesGroup> groups) {
        WorksExtended result = new WorksExtended();
        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            WorkGroupExtended workGroup = new WorkGroupExtended();
            // Fill the work groups with the external identifiers
            if (externalIdentifiers == null || externalIdentifiers.isEmpty()) {
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
                WorkSummaryExtended workSummary = (WorkSummaryExtended) activity;
                workGroup.getWorkSummary().add(workSummary);
            }

            // Sort the works
            workGroup.getWorkSummary().sort(WorkComparators.WORKS_WITHIN_GROUP);
            result.getWorkGroup().add(workGroup);
        }
        // Sort the groups!
        result.getWorkGroup().sort(WorkComparators.GROUP_WORK_SUMMARY_EXTENDED);
        return result;
    }

    private String isEmpty(Object o) {
        if (o != null) {
            return o.toString();
        }
        return null;
    }

}
