package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.impl.GroupingSuggestionManagerImpl;
import org.orcid.persistence.dao.RejectedGroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.RejectedGroupingSuggestionEntity;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionManagerTest {

    @Mock
    private RejectedGroupingSuggestionDao groupingSuggestionDao;
    
    @Mock
    private GroupingSuggestionsCacheManager groupingSuggestionsCacheManager;

    @InjectMocks
    private GroupingSuggestionManagerImpl groupingSuggestionManager;

    @Before
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetGroupingSuggestionNoRejection() {
        WorkGroupingSuggestion suggestion = getWorkGroupingSuggestion(new Long[] { 1L, 2L });
        Mockito.when(groupingSuggestionsCacheManager.getGroupingSuggestion(Mockito.eq("orcid"))).thenReturn(suggestion);
        Mockito.when(groupingSuggestionDao.findGroupingSuggestionIdAndOrcid(Mockito.eq("orcid"), Mockito.eq("1,2"))).thenReturn(null);
        WorkGroupingSuggestion retrieved = groupingSuggestionManager.getGroupingSuggestion("orcid");
        assertNotNull(retrieved);
        assertEquals("1,2", retrieved.getPutCodesAsString());
    }
    
    @Test
    public void testGetGroupingSuggestionOneRejection() {
        WorkGroupingSuggestion first = getWorkGroupingSuggestion(new Long[] { 1L, 2L });
        WorkGroupingSuggestion second = getWorkGroupingSuggestion(new Long[] { 3L, 4L });
        Mockito.when(groupingSuggestionsCacheManager.getGroupingSuggestion(Mockito.eq("orcid"))).thenReturn(first).thenReturn(second);
        Mockito.when(groupingSuggestionDao.findGroupingSuggestionIdAndOrcid(Mockito.eq("orcid"), Mockito.eq("1,2"))).thenReturn(getRejectedGroupingSuggestion("orcid", "1,2"));
        Mockito.when(groupingSuggestionDao.findGroupingSuggestionIdAndOrcid(Mockito.eq("orcid"), Mockito.eq("3,4"))).thenReturn(null);
        WorkGroupingSuggestion retrieved = groupingSuggestionManager.getGroupingSuggestion("orcid");
        assertNotNull(retrieved);
        assertEquals("3,4", retrieved.getPutCodesAsString());
    }
    
    @Test
    public void testGetGroupingSuggestionAllRejected() {
        WorkGroupingSuggestion first = getWorkGroupingSuggestion(new Long[] { 1L, 2L });
        WorkGroupingSuggestion second = getWorkGroupingSuggestion(new Long[] { 3L, 4L });
        WorkGroupingSuggestion third = getWorkGroupingSuggestion(new Long[] { 5L, 6L });
        
        Mockito.when(groupingSuggestionsCacheManager.getGroupingSuggestion(Mockito.eq("orcid"))).thenReturn(first).thenReturn(second).thenReturn(third).thenReturn(null);
        Mockito.when(groupingSuggestionDao.findGroupingSuggestionIdAndOrcid(Mockito.eq("orcid"), Mockito.eq("1,2"))).thenReturn(getRejectedGroupingSuggestion("orcid", "1,2"));
        Mockito.when(groupingSuggestionDao.findGroupingSuggestionIdAndOrcid(Mockito.eq("orcid"), Mockito.eq("3,4"))).thenReturn(getRejectedGroupingSuggestion("orcid", "3,4"));
        Mockito.when(groupingSuggestionDao.findGroupingSuggestionIdAndOrcid(Mockito.eq("orcid"), Mockito.eq("5,6"))).thenReturn(getRejectedGroupingSuggestion("orcid", "5,6"));
        WorkGroupingSuggestion retrieved = groupingSuggestionManager.getGroupingSuggestion("orcid");
        assertNull(retrieved);
    }
    
    @Test
    public void testMarkGroupingSuggestionAsRejected() {
        WorkGroupingSuggestion suggestion = getWorkGroupingSuggestion(new Long[] {1L, 2L});
        Mockito.when(groupingSuggestionDao.merge(Mockito.any(RejectedGroupingSuggestionEntity.class))).thenReturn(null);
        groupingSuggestionManager.markGroupingSuggestionAsRejected(suggestion);
        
        ArgumentCaptor<RejectedGroupingSuggestionEntity> captor = ArgumentCaptor.forClass(RejectedGroupingSuggestionEntity.class);
        Mockito.verify(groupingSuggestionDao, Mockito.times(1)).persist(captor.capture());
        RejectedGroupingSuggestionEntity entity = captor.getValue();
        assertNotNull(entity);
        assertEquals("1,2", entity.getId());
        assertEquals("orcid", entity.getOrcid());
    }

    private WorkGroupingSuggestion getWorkGroupingSuggestion(Long[] putCodes) {
        WorkGroupingSuggestion suggestion = new WorkGroupingSuggestion(Arrays.asList(putCodes));
        suggestion.setOrcid("orcid");
        return suggestion;
    }
    
    private RejectedGroupingSuggestionEntity getRejectedGroupingSuggestion(String orcid, String putCodes) {
        RejectedGroupingSuggestionEntity entity = new RejectedGroupingSuggestionEntity();
        entity.setId(putCodes);
        entity.setOrcid(orcid);
        return entity;
    }

}
