package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.jsonidentifier.JSONWorkPutCodes;
import org.orcid.core.manager.impl.GroupingSuggestionManagerImpl;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.dao.GroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;

public class GroupingSuggestionManagerTest {
    
    @Before
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Mock
    private GroupingSuggestionDao groupingSuggestionDao;
    
    @InjectMocks
    private GroupingSuggestionManagerImpl groupingSuggestionManager;
    
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
        assertTrue(Long.valueOf(1L).equals(putCodes.getWorkPutCodes()[0]) || Long.valueOf(3L).equals(putCodes.getWorkPutCodes()[0]) || Long.valueOf(4L).equals(putCodes.getWorkPutCodes()[0]));
        assertTrue(Long.valueOf(1L).equals(putCodes.getWorkPutCodes()[1]) || Long.valueOf(3L).equals(putCodes.getWorkPutCodes()[1]) || Long.valueOf(4L).equals(putCodes.getWorkPutCodes()[1]));
        assertTrue(Long.valueOf(1L).equals(putCodes.getWorkPutCodes()[2]) || Long.valueOf(3L).equals(putCodes.getWorkPutCodes()[2]) || Long.valueOf(4L).equals(putCodes.getWorkPutCodes()[2]));
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
