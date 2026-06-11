package org.orcid.core.manager.v3.read_only.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.aop.ProfileLastModifiedAspect;
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
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.*;
import org.orcid.pojo.ActivityTitle;
import org.orcid.pojo.ActivityTitleSearchResult;
import org.orcid.pojo.WorkSummaryExtended;
import org.orcid.pojo.WorksExtended;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class WorkManagerReadOnlyImplTest {

    private static final String ORCID_1 = "0000-0000-0000-0001";
    private static final String ORCID_2 = "0000-0000-0000-0002";
    private static final Long WORK_ID_1 = 1L;
    private static final Long WORK_ID_2 = 2L;
    private static final int MAX_WORKS_TO_READ = 100;

    @Mock
    private WorkDao workDao;

    @Mock
    private WorkEntityCacheManager workEntityCacheManager;

    @Mock
    private JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Mock
    private OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @Mock
    private WorksExtendedCacheManager worksExtendedCacheManager;

    @Mock
    private GroupingSuggestionManager groupingSuggestionsManager;

    @Mock
    private ContributorUtils contributorUtils;

    @Mock
    private WorkContributorsConverter workContributorsConverter;

    @Mock
    private JSONWorkExternalIdentifiersConverterV3 jsonWorkExternalIdentifiersConverterV3;

    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Mock
    private SourceNameCacheManager sourceNameCacheManager;

    @Mock
    private ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter;

    @Mock
    private SourceEntityUtils sourceEntityUtils;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    private WorkManagerReadOnlyImpl workManagerReadOnly;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        workManagerReadOnly = new WorkManagerReadOnlyImpl(MAX_WORKS_TO_READ);
        ReflectionTestUtils.setField(workManagerReadOnly, "profileLastModifiedAspect", profileLastModifiedAspect);
        ReflectionTestUtils.setField(workManagerReadOnly, "workDao", workDao);
        ReflectionTestUtils.setField(workManagerReadOnly, "workEntityCacheManager", workEntityCacheManager);
        ReflectionTestUtils.setField(workManagerReadOnly, "jpaJaxbWorkAdapter", jpaJaxbWorkAdapter);
        ReflectionTestUtils.setField(workManagerReadOnly, "orcidCoreExceptionMapper", orcidCoreExceptionMapper);
        ReflectionTestUtils.setField(workManagerReadOnly, "worksExtendedCacheManager", worksExtendedCacheManager);
        ReflectionTestUtils.setField(workManagerReadOnly, "groupingSuggestionsManager", groupingSuggestionsManager);
        ReflectionTestUtils.setField(workManagerReadOnly, "contributorUtils", contributorUtils);
        ReflectionTestUtils.setField(workManagerReadOnly, "workContributorsConverter", workContributorsConverter);
        ReflectionTestUtils.setField(workManagerReadOnly, "jsonWorkExternalIdentifiersConverterV3", jsonWorkExternalIdentifiersConverterV3);
        ReflectionTestUtils.setField(workManagerReadOnly, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManager);
        ReflectionTestUtils.setField(workManagerReadOnly, "sourceNameCacheManager", sourceNameCacheManager);
        ReflectionTestUtils.setField(workManagerReadOnly, "contributorsRolesAndSequencesConverter", contributorsRolesAndSequencesConverter);
        ReflectionTestUtils.setField(workManagerReadOnly, "sourceEntityUtils", sourceEntityUtils);
        ReflectionTestUtils.setField(workManagerReadOnly, "maxContributorsForUI", 50);
    }

    @Test
    public void testConstructorWithBulkReadSize() {
        WorkManagerReadOnlyImpl manager = new WorkManagerReadOnlyImpl(150);
        assertEquals(150, (int) ReflectionTestUtils.getField(manager, "maxWorksToRead"));
    }

    @Test
    public void testConstructorWithNullBulkReadSize() {
        WorkManagerReadOnlyImpl manager = new WorkManagerReadOnlyImpl(null);
        assertEquals(100, (int) ReflectionTestUtils.getField(manager, "maxWorksToRead"));
    }

    @Test
    public void testSetWorkDao() {
        WorkDao newWorkDao = mock(WorkDao.class);
        workManagerReadOnly.setWorkDao(newWorkDao);
        assertEquals(newWorkDao, ReflectionTestUtils.getField(workManagerReadOnly, "workDao"));
    }

    @Test
    public void testSetWorkEntityCacheManager() {
        WorkEntityCacheManager newCacheManager = mock(WorkEntityCacheManager.class);
        workManagerReadOnly.setWorkEntityCacheManager(newCacheManager);
        assertEquals(newCacheManager, ReflectionTestUtils.getField(workManagerReadOnly, "workEntityCacheManager"));
    }

    @Test
    public void testFindWorks() {
        List<MinimizedWorkEntity> minimizedWorks = createMinimizedWorkEntities(2);
        List<Work> expectedWorks = createWorks(2);

        when(workEntityCacheManager.retrieveMinimizedWorks(ORCID_1, 0L)).thenReturn(minimizedWorks);
        when(jpaJaxbWorkAdapter.toMinimizedWork(minimizedWorks)).thenReturn(expectedWorks);

        List<Work> result = workManagerReadOnly.findWorks(ORCID_1);

        assertEquals(2, result.size());
        verify(workEntityCacheManager, times(1)).retrieveMinimizedWorks(ORCID_1, 0L);
        verify(jpaJaxbWorkAdapter, times(1)).toMinimizedWork(minimizedWorks);
    }

    @Test
    public void testHasPublicWorksTrue() {
        when(workDao.hasPublicWorks(ORCID_1)).thenReturn(true);

        Boolean result = workManagerReadOnly.hasPublicWorks(ORCID_1);

        assertTrue(result);
        verify(workDao, times(1)).hasPublicWorks(ORCID_1);
    }

    @Test
    public void testHasPublicWorksFalse() {
        when(workDao.hasPublicWorks(ORCID_1)).thenReturn(false);

        Boolean result = workManagerReadOnly.hasPublicWorks(ORCID_1);

        assertFalse(result);
        verify(workDao, times(1)).hasPublicWorks(ORCID_1);
    }

    @Test
    public void testHasPublicWorksEmptyOrcid() {
        Boolean result = workManagerReadOnly.hasPublicWorks("");

        assertFalse(result);
        verify(workDao, times(0)).hasPublicWorks(anyString());
    }

    @Test
    public void testHasPublicWorksNullOrcid() {
        Boolean result = workManagerReadOnly.hasPublicWorks(null);

        assertFalse(result);
        verify(workDao, times(0)).hasPublicWorks(anyString());
    }

    @Test
    public void testFindPublicWorks() {
        List<MinimizedWorkEntity> minimizedWorks = createMinimizedWorkEntities(1);
        List<Work> expectedWorks = createWorks(1);

        when(workEntityCacheManager.retrievePublicMinimizedWorks(ORCID_1, 0L)).thenReturn(minimizedWorks);
        when(jpaJaxbWorkAdapter.toMinimizedWork(minimizedWorks)).thenReturn(expectedWorks);

        List<Work> result = workManagerReadOnly.findPublicWorks(ORCID_1);

        assertEquals(1, result.size());
        verify(workEntityCacheManager, times(1)).retrievePublicMinimizedWorks(ORCID_1, 0L);
        verify(jpaJaxbWorkAdapter, times(1)).toMinimizedWork(minimizedWorks);
    }

    @Test
    public void testGetWork() {
        WorkEntity workEntity = createWorkEntity(WORK_ID_1);
        Work expectedWork = createWork();

        when(workDao.getWork(ORCID_1, WORK_ID_1)).thenReturn(workEntity);
        when(jpaJaxbWorkAdapter.toWork(workEntity)).thenReturn(expectedWork);

        Work result = workManagerReadOnly.getWork(ORCID_1, WORK_ID_1);

        assertNotNull(result);
        verify(workDao, times(1)).getWork(ORCID_1, WORK_ID_1);
        verify(jpaJaxbWorkAdapter, times(1)).toWork(workEntity);
    }

    @Test
    public void testGetWorkSummary() {
        WorkEntity workEntity = createWorkEntity(WORK_ID_1);
        WorkSummary expectedWorkSummary = createWorkSummary();

        when(workDao.getWork(ORCID_1, WORK_ID_1)).thenReturn(workEntity);
        when(jpaJaxbWorkAdapter.toWorkSummary(workEntity)).thenReturn(expectedWorkSummary);

        WorkSummary result = workManagerReadOnly.getWorkSummary(ORCID_1, WORK_ID_1);

        assertNotNull(result);
        verify(workDao, times(1)).getWork(ORCID_1, WORK_ID_1);
        verify(jpaJaxbWorkAdapter, times(1)).toWorkSummary(workEntity);
    }

    @Test
    public void testGetWorksSummaryList() {
        List<MinimizedWorkEntity> minimizedWorks = createMinimizedWorkEntities(3);
        List<WorkSummary> expectedWorkSummaries = createWorkSummaries(3);

        when(workEntityCacheManager.retrieveMinimizedWorks(ORCID_1, 0L)).thenReturn(minimizedWorks);
        when(clientDetailsEntityCacheManager.retrieveAll(any())).thenReturn(new HashMap<>());
        when(sourceEntityUtils.extractSourceFromEntityComplete(any())).thenReturn(new Source());
        when(jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(minimizedWorks)).thenReturn(expectedWorkSummaries);

        List<WorkSummary> result = workManagerReadOnly.getWorksSummaryList(ORCID_1);

        assertEquals(3, result.size());
        verify(workEntityCacheManager, times(1)).retrieveMinimizedWorks(ORCID_1, 0L);
        verify(jpaJaxbWorkAdapter, times(1)).toWorkSummaryFromMinimized(minimizedWorks);
    }

    @Test
    public void testGetWorksSummaryListWithPutCodes() {
        List<Long> putCodes = Arrays.asList(WORK_ID_1, WORK_ID_2);
        List<MinimizedWorkEntity> minimizedWorks = createMinimizedWorkEntities(2);
        List<WorkSummary> expectedWorkSummaries = createWorkSummaries(2);

        when(workEntityCacheManager.retrieveMinimizedWorks(ORCID_1, putCodes, 0L)).thenReturn(minimizedWorks);
        when(jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(minimizedWorks)).thenReturn(expectedWorkSummaries);

        List<WorkSummary> result = workManagerReadOnly.getWorksSummaryList(ORCID_1, putCodes);

        assertEquals(2, result.size());
        verify(workEntityCacheManager, times(1)).retrieveMinimizedWorks(ORCID_1, putCodes, 0L);
        verify(jpaJaxbWorkAdapter, times(1)).toWorkSummaryFromMinimized(minimizedWorks);
    }

    @Test
    public void testGetWorksSummaryExtendedList() {
        List<WorkSummaryExtended> expectedExtended = createWorkSummariesExtended(2);

        when(workDao.getWorksByOrcid(ORCID_1, false)).thenReturn(new ArrayList<>());

        List<WorkSummaryExtended> result = workManagerReadOnly.getWorksSummaryExtendedList(ORCID_1, false);

        assertNotNull(result);
        verify(workDao, times(1)).getWorksByOrcid(ORCID_1, false);
    }

    @Test
    public void testGroupWorksAndGenerateGroupingSuggestions() {
        List<WorkSummary> summaries = createWorkSummaries(2);
        List<WorkGroupingSuggestion> suggestions = new ArrayList<>();

        Works result = workManagerReadOnly.groupWorksAndGenerateGroupingSuggestions(summaries, ORCID_1);

        assertNotNull(result);
        verify(groupingSuggestionsManager, times(1)).cacheGroupingSuggestions(ORCID_1, suggestions);
    }

    @Test
    public void testGroupWorks() {
        List<WorkSummary> summaries = createWorkSummaries(2);

        Works result = workManagerReadOnly.groupWorks(summaries, false);

        assertNotNull(result);
        assertNotNull(result.getWorkGroup());
    }

    @Test
    public void testGroupWorksJustPublic() {
        List<WorkSummary> summaries = new ArrayList<>();
        WorkSummary summary1 = createWorkSummary();
        summary1.setVisibility(Visibility.PUBLIC);
        WorkSummary summary2 = createWorkSummary();
        summary2.setVisibility(Visibility.PRIVATE);
        summaries.add(summary1);
        summaries.add(summary2);

        Works result = workManagerReadOnly.groupWorks(summaries, true);

        assertNotNull(result);
    }

    @Test
    public void testFindWorkBulk() {
        String putCodesAsString = "1,2,3";
        List<WorkEntity> entities = createWorkEntities(3);
        List<Work> expectedWorks = createWorks(3);

        when(workDao.getWorkEntities(anyString(), anyList())).thenReturn(new ArrayList<>(entities));
        when(jpaJaxbWorkAdapter.toWork(any(WorkEntity.class))).thenReturn(expectedWorks.get(0), expectedWorks.get(1), expectedWorks.get(2));

        WorkBulk result = workManagerReadOnly.findWorkBulk(ORCID_1, putCodesAsString);

        assertNotNull(result);
        assertEquals(3, result.getBulk().size());
        verify(workDao, times(1)).getWorkEntities(anyString(), anyList());
    }

    @Test
    public void testFindWorkBulkExceedsMaximum() {
        String putCodesAsString = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,"
                + "21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,"
                + "41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,"
                + "61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,"
                + "81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101";

        try {
            workManagerReadOnly.findWorkBulk(ORCID_1, putCodesAsString);
        } catch (ExceedMaxNumberOfPutCodesException e) {
            assertTrue(e.getMessage().contains(String.valueOf(MAX_WORKS_TO_READ)));
        }
    }

    @Test
    public void testFindWorksWithLastModifiedEntities() {
        List<WorkLastModifiedEntity> elements = new ArrayList<>();
        WorkLastModifiedEntity entity = new WorkLastModifiedEntity();
        entity.setId(WORK_ID_1);
        ReflectionTestUtils.setField(entity, "lastModified", new Timestamp(System.currentTimeMillis()));
        elements.add(entity);

        WorkEntity workEntity = createWorkEntity(WORK_ID_1);
        Work expectedWork = createWork();

        when(workEntityCacheManager.retrieveFullWork(ORCID_1, WORK_ID_1, entity.getLastModified().getTime())).thenReturn(workEntity);
        when(jpaJaxbWorkAdapter.toWork(workEntity)).thenReturn(expectedWork);

        List<Work> result = workManagerReadOnly.findWorks(ORCID_1, elements);

        assertEquals(1, result.size());
        verify(workEntityCacheManager, times(1)).retrieveFullWork(ORCID_1, WORK_ID_1, entity.getLastModified().getTime());
    }

    @Test
    public void testGetWorksTitle() {
        WorksExtended worksExtended = new WorksExtended();
        List<WorkSummaryExtended> titles = new ArrayList<>();

        when(worksExtendedCacheManager.getGroupedWorksExtended(ORCID_1)).thenReturn(worksExtended);

        List<ActivityTitle> result = workManagerReadOnly.getWorksTitle(ORCID_1);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testSearchWorksTitle() {
        String searchTerm = "Journal";
        ActivityTitleSearchResult result = workManagerReadOnly.searchWorksTitle(ORCID_1, searchTerm, 10, 0, false, false);

        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testSearchWorksTitleEmptySearchTerm() {
        ActivityTitleSearchResult result = workManagerReadOnly.searchWorksTitle(ORCID_1, "", 10, 0, false, false);

        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testGetFeaturedWorks() {
        List<WorkSummaryExtended> works = createWorkSummariesExtended(2);

        when(worksExtendedCacheManager.getFeaturedGroupedWorksExtended(ORCID_1)).thenReturn(works);

        List<WorkForm> result = workManagerReadOnly.getFeaturedWorks(ORCID_1);

        assertNotNull(result);
        verify(worksExtendedCacheManager, times(1)).getFeaturedGroupedWorksExtended(ORCID_1);
    }

    @Test
    public void testGetFeaturedWorksSummaryExtended() {
        List<WorkSummaryExtended> featured = createWorkSummariesExtended(2);

        when(workDao.getWorksByOrcid(ORCID_1, true)).thenReturn(new ArrayList<>());

        List<WorkSummaryExtended> result = workManagerReadOnly.getFeaturedWorksSummaryExtended(ORCID_1);

        assertNotNull(result);
    }

    @Test
    public void testGetAllExternalIDs() {
        List<WorkSummary> summaries = createWorkSummaries(2);

        when(workEntityCacheManager.retrieveMinimizedWorks(ORCID_1, 0L)).thenReturn(new ArrayList<>());
        when(jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(any())).thenReturn(summaries);

        ExternalIDs result = workManagerReadOnly.getAllExternalIDs(ORCID_1);

        assertNotNull(result);
    }

    // Helper methods to create test data

    private List<MinimizedWorkEntity> createMinimizedWorkEntities(int count) {
        List<MinimizedWorkEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MinimizedWorkEntity entity = new MinimizedWorkEntity();
            entity.setId((long) i + 1);
            entities.add(entity);
        }
        return entities;
    }

    private List<WorkEntity> createWorkEntities(int count) {
        List<WorkEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            WorkEntity entity = createWorkEntity((long) i + 1);
            entities.add(entity);
        }
        return entities;
    }

    private WorkEntity createWorkEntity(Long id) {
        WorkEntity entity = new WorkEntity();
        entity.setId(id);
        entity.setTitle("Test Work " + id);
        entity.setVisibility(Visibility.PUBLIC.value());
        return entity;
    }

    private List<Work> createWorks(int count) {
        List<Work> works = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            works.add(createWork());
        }
        return works;
    }

    private Work createWork() {
        Work work = new Work();
        work.setPutCode(1L);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new org.orcid.jaxb.model.v3.release.common.Title("Test Work"));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.JOURNAL_ARTICLE);
        work.setVisibility(Visibility.PUBLIC);
        return work;
    }

    private List<WorkSummary> createWorkSummaries(int count) {
        List<WorkSummary> summaries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            summaries.add(createWorkSummary());
        }
        return summaries;
    }

    private WorkSummary createWorkSummary() {
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(1L);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new org.orcid.jaxb.model.v3.release.common.Title("Test Work"));
        summary.setTitle(workTitle);
        summary.setType(WorkType.JOURNAL_ARTICLE);
        summary.setVisibility(Visibility.PUBLIC);
        summary.setExternalIdentifiers(new ExternalIDs());
        return summary;
    }

    private List<WorkSummaryExtended> createWorkSummariesExtended(int count) {
        List<WorkSummaryExtended> summaries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            WorkSummaryExtended summary = new WorkSummaryExtended.WorkSummaryExtendedBuilder(
                    BigInteger.valueOf(i + 1),
                    WorkType.JOURNAL_ARTICLE.name(),
                    "Test Work " + i,
                    "sourceId",
                    "clientSourceId",
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis())
            ).visibility("PUBLIC").displayIndex(BigInteger.ZERO).build();
            summaries.add(summary);
        }
        return summaries;
    }
}
