package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.jsonidentifier.JSONWorkPutCodes;
import org.orcid.core.manager.v3.impl.GroupingSuggestionManagerImpl;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.rc1.common.Title;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkTitle;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.orcid.persistence.dao.GroupingSuggestionDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.WorkGroupingSuggestion;

public class GroupingSuggestionManagerTest {

    @Mock
    private GroupingSuggestionDao groupingSuggestionDao;

    @Mock
    private WorkDao workDao;

    @InjectMocks
    private GroupingSuggestionManagerImpl groupingSuggestionManager;

    @Before
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
        groupingSuggestionManager.setGroupingSuggestionDao(groupingSuggestionDao);
    }

    @Test
    public void testGenerateGroupingSuggestionsForProfile() {
        Work work = getWork("work title");
        Works works = new Works();
        WorkGroup workGroup = new WorkGroup();
        WorkSummary summary = getWorkSummary("no match", 2L);
        workGroup.getWorkSummary().add(summary);
        WorkGroup secondGroup = new WorkGroup();
        WorkSummary matchingSummary = getWorkSummary("WORKTITLE", 3L);
        secondGroup.getWorkSummary().add(matchingSummary);
        works.getWorkGroup().add(workGroup);
        works.getWorkGroup().add(secondGroup);

        Mockito.when(groupingSuggestionDao.findGroupingSuggestionIdByPutCodes(Mockito.eq("orcid"), Mockito.anyLong(), Mockito.anyList())).thenReturn(null);
        Mockito.doNothing().when(groupingSuggestionDao).persist(Mockito.any(GroupingSuggestionEntity.class));

        groupingSuggestionManager.generateGroupingSuggestionsForProfile("orcid", work, works);

        ArgumentCaptor<GroupingSuggestionEntity> captor = ArgumentCaptor.forClass(GroupingSuggestionEntity.class);
        Mockito.verify(groupingSuggestionDao).persist(captor.capture());
        GroupingSuggestionEntity entity = captor.getValue();

        assertEquals("orcid", entity.getOrcid());
        assertNull(entity.getDismissedDate());

        JSONWorkPutCodes putCodes = JsonUtils.readObjectFromJsonString(entity.getWorkPutCodes(), JSONWorkPutCodes.class);
        assertEquals(2, putCodes.getWorkPutCodes().length);
        assertTrue(Long.valueOf(1L).equals(putCodes.getWorkPutCodes()[0]) || Long.valueOf(3L).equals(putCodes.getWorkPutCodes()[0]));
        assertTrue(Long.valueOf(1L).equals(putCodes.getWorkPutCodes()[1]) || Long.valueOf(3L).equals(putCodes.getWorkPutCodes()[1]));
    }

    @Test
    public void testGenerateGroupingSuggestionsForProfileWithMultipleSummariesInGroup() {
        Work work = getWork("work title");
        Works works = new Works();
        WorkGroup workGroup = new WorkGroup();
        WorkSummary summary = getWorkSummary("no match", 2L);
        workGroup.getWorkSummary().add(summary);
        WorkGroup secondGroup = new WorkGroup();
        WorkSummary matchingSummary = getWorkSummary("WORKTITLE", 3L);
        WorkSummary extraSummary = getWorkSummary("w o R k T i T l E", 4L);
        secondGroup.getWorkSummary().add(matchingSummary);
        secondGroup.getWorkSummary().add(extraSummary);
        works.getWorkGroup().add(workGroup);
        works.getWorkGroup().add(secondGroup);

        Mockito.when(groupingSuggestionDao.findGroupingSuggestionIdByPutCodes(Mockito.eq("orcid"), Mockito.anyLong(), Mockito.anyList())).thenReturn(null);
        Mockito.doNothing().when(groupingSuggestionDao).persist(Mockito.any(GroupingSuggestionEntity.class));

        groupingSuggestionManager.generateGroupingSuggestionsForProfile("orcid", work, works);

        ArgumentCaptor<GroupingSuggestionEntity> captor = ArgumentCaptor.forClass(GroupingSuggestionEntity.class);
        Mockito.verify(groupingSuggestionDao).persist(captor.capture());
        GroupingSuggestionEntity entity = captor.getValue();

        assertEquals("orcid", entity.getOrcid());
        assertNull(entity.getDismissedDate());

        JSONWorkPutCodes putCodes = JsonUtils.readObjectFromJsonString(entity.getWorkPutCodes(), JSONWorkPutCodes.class);
        assertEquals(3, putCodes.getWorkPutCodes().length);
        assertTrue(Long.valueOf(1L).equals(putCodes.getWorkPutCodes()[0]) || Long.valueOf(3L).equals(putCodes.getWorkPutCodes()[0])
                || Long.valueOf(4L).equals(putCodes.getWorkPutCodes()[0]));
        assertTrue(Long.valueOf(1L).equals(putCodes.getWorkPutCodes()[1]) || Long.valueOf(3L).equals(putCodes.getWorkPutCodes()[1])
                || Long.valueOf(4L).equals(putCodes.getWorkPutCodes()[1]));
        assertTrue(Long.valueOf(1L).equals(putCodes.getWorkPutCodes()[2]) || Long.valueOf(3L).equals(putCodes.getWorkPutCodes()[2])
                || Long.valueOf(4L).equals(putCodes.getWorkPutCodes()[2]));
    }

    @Test
    public void testFilterSuggestionsNoLongerApplicable() {
        Mockito.when(workDao.find(Mockito.eq(1L))).thenReturn(getWorkEntity("w o R K T I t le"));
        Mockito.when(workDao.find(Mockito.eq(2L))).thenReturn(getWorkEntity("worktitle"));
        Mockito.when(workDao.find(Mockito.eq(3L))).thenReturn(null);
        Mockito.when(workDao.find(Mockito.eq(4L))).thenReturn(getWorkEntity("work title has changed"));
        Mockito.doNothing().when(groupingSuggestionDao).remove(Mockito.anyLong());

        WorkGroupingSuggestion suggestion1 = new WorkGroupingSuggestion(new JSONWorkPutCodes(new Long[] { 1L, 2L })); // valid
        suggestion1.setId(1L);
        WorkGroupingSuggestion suggestion2 = new WorkGroupingSuggestion(new JSONWorkPutCodes(new Long[] { 1L, 3L })); // invalid
        suggestion2.setId(2L);
        WorkGroupingSuggestion suggestion3 = new WorkGroupingSuggestion(new JSONWorkPutCodes(new Long[] { 1L, 4L })); // invalid
        suggestion3.setId(3L);
        WorkGroupingSuggestion suggestion4 = new WorkGroupingSuggestion(new JSONWorkPutCodes(new Long[] { 2L, 4L })); // invalid
        suggestion4.setId(4L);
        WorkGroupingSuggestion suggestion5 = new WorkGroupingSuggestion(new JSONWorkPutCodes(new Long[] { 1L, 2L, 3L, 4L })); // invalid
        suggestion5.setId(5L);
        
        List<WorkGroupingSuggestion> suggestions = groupingSuggestionManager
                .filterSuggestionsNoLongerApplicable(Arrays.asList(suggestion1, suggestion2, suggestion3, suggestion4, suggestion5), new Works());
        assertEquals(1, suggestions.size());
        WorkGroupingSuggestion remainingSuggestion = suggestions.get(0);
        assertEquals(2, remainingSuggestion.getPutCodes().getWorkPutCodes().length);
        assertEquals(Long.valueOf(1l), remainingSuggestion.getPutCodes().getWorkPutCodes()[0]);
        assertEquals(Long.valueOf(2l), remainingSuggestion.getPutCodes().getWorkPutCodes()[1]);

        Mockito.verify(groupingSuggestionDao, Mockito.times(4)).remove(Mockito.anyLong());
    }

    @Test
    public void testFilterSuggestionsNoLongerApplicableWhereSomeAlreadyGrouped() {
        Mockito.when(workDao.find(Mockito.eq(1L))).thenReturn(getWorkEntity("w o R K T I t le"));
        Mockito.when(workDao.find(Mockito.eq(2L))).thenReturn(getWorkEntity("worktitle"));
        Mockito.when(workDao.find(Mockito.eq(3L))).thenReturn(getWorkEntity("work title"));

        Mockito.doNothing().when(groupingSuggestionDao).remove(Mockito.anyLong());

        WorkGroupingSuggestion suggestion1 = new WorkGroupingSuggestion(new JSONWorkPutCodes(new Long[] { 1L, 2L, 3L })); // valid
        suggestion1.setId(1L);
        
        WorkSummary summary1 = getWorkSummary("worktitle", 1L);
        WorkSummary summary2 = getWorkSummary("worktitle", 2L);
        WorkSummary summary3 = getWorkSummary("worktitle", 3L);

        Works works = new Works();
        WorkGroup workGroup = new WorkGroup();
        workGroup.getWorkSummary().add(summary1);
        workGroup.getWorkSummary().add(summary2);
        workGroup.getWorkSummary().add(summary3);
        works.getWorkGroup().add(workGroup);

        List<WorkGroupingSuggestion> suggestions = groupingSuggestionManager.filterSuggestionsNoLongerApplicable(Arrays.asList(suggestion1), works);
        assertEquals(0, suggestions.size());

        Mockito.verify(groupingSuggestionDao, Mockito.times(1)).remove(Mockito.anyLong());
    }

    @Test
    public void testGetGroupingSuggestions() {
        List<GroupingSuggestionEntity> entities = Arrays.asList(getGroupingSuggestionEntity(1L, new Long[] { 1L, 2L }),
                getGroupingSuggestionEntity(2L, new Long[] { 5L, 6L }), getGroupingSuggestionEntity(3L, new Long[] { 3L, 4L }));
        Mockito.when(groupingSuggestionDao.getGroupingSuggestions(Mockito.eq("orcid"))).thenReturn(entities);
        List<WorkGroupingSuggestion> retrieved = groupingSuggestionManager.getGroupingSuggestions("orcid");
        assertEquals(3, retrieved.size());
        assertEquals(Long.valueOf(1l), retrieved.get(0).getId());
        assertEquals(Long.valueOf(1l), retrieved.get(0).getPutCodes().getWorkPutCodes()[0]);
        assertEquals(Long.valueOf(2l), retrieved.get(0).getPutCodes().getWorkPutCodes()[1]);
        assertEquals(Long.valueOf(2l), retrieved.get(1).getId());
        assertEquals(Long.valueOf(5l), retrieved.get(1).getPutCodes().getWorkPutCodes()[0]);
        assertEquals(Long.valueOf(6l), retrieved.get(1).getPutCodes().getWorkPutCodes()[1]);
        assertEquals(Long.valueOf(3l), retrieved.get(2).getId());
        assertEquals(Long.valueOf(3l), retrieved.get(2).getPutCodes().getWorkPutCodes()[0]);
        assertEquals(Long.valueOf(4l), retrieved.get(2).getPutCodes().getWorkPutCodes()[1]);
    }
    
    @Test
    public void testMarkGroupingSuggestionAsAccepted() {
        GroupingSuggestionEntity entity = getGroupingSuggestionEntity(1L, new Long[] {1L, 2L});
        Mockito.when(groupingSuggestionDao.find(Mockito.anyLong())).thenReturn(entity);
        Mockito.when(groupingSuggestionDao.merge(Mockito.any(GroupingSuggestionEntity.class))).thenReturn(null);
        groupingSuggestionManager.markGroupingSuggestionAsAccepted(1L);
        assertNotNull(entity.getAcceptedDate());
        Mockito.verify(groupingSuggestionDao, Mockito.times(1)).find(Mockito.eq(1L));
        Mockito.verify(groupingSuggestionDao, Mockito.times(1)).merge(Mockito.any(GroupingSuggestionEntity.class));
    }

    private GroupingSuggestionEntity getGroupingSuggestionEntity(Long id, Long[] putCodes) {
        GroupingSuggestionEntity entity = new GroupingSuggestionEntity();
        entity.setId(id);
        JSONWorkPutCodes workPutCodes = new JSONWorkPutCodes(putCodes);
        entity.setWorkPutCodes(JsonUtils.convertToJsonString(workPutCodes));
        return entity;
    }

    private WorkEntity getWorkEntity(String title) {
        WorkEntity workEntity = new WorkEntity();
        workEntity.setTitle(title);
        return workEntity;
    }

    private Work getWork(String title) {
        Work work = new Work();
        work.setPutCode(1L);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.BOOK);
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setValue("ext-id-value");
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);
        work.setVisibility(Visibility.PUBLIC);
        return work;
    }

    private WorkSummary getWorkSummary(String titleValue, Long putCode) {
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(putCode);
        summary.setDisplayIndex("1");
        Title title = new Title(titleValue);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(title);
        summary.setTitle(workTitle);
        summary.setType(WorkType.ARTISTIC_PERFORMANCE);
        summary.setVisibility(Visibility.PUBLIC);
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setValue("extIdValue");
        extIds.getExternalIdentifier().add(extId);
        summary.setExternalIdentifiers(extIds);
        return summary;
    }

}
