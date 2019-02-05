package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.core.manager.v3.read_only.impl.GroupingSuggestionManagerReadOnlyImpl;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;
import org.orcid.pojo.grouping.WorkGroupingSuggestions;

public class GroupingSuggestionManagerReadOnlyTest {

    @Mock
    private GroupingSuggestionsCacheManager mockGroupingSuggestionCacheManager;

    @InjectMocks
    private GroupingSuggestionManagerReadOnlyImpl groupingSuggestionManagerReadOnly;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetGroupingSuggestions() {
        Mockito.when(mockGroupingSuggestionCacheManager.getGroupingSuggestionCount(Mockito.eq("orcid")))
                .thenReturn(GroupingSuggestionManagerReadOnly.SUGGESTION_BATCH_SIZE + 30);
        Mockito.when(mockGroupingSuggestionCacheManager.getGroupingSuggestions(Mockito.eq("orcid"), Mockito.eq(GroupingSuggestionManagerReadOnly.SUGGESTION_BATCH_SIZE)))
                .thenReturn(getSuggestions("orcid", GroupingSuggestionManagerReadOnly.SUGGESTION_BATCH_SIZE));

        WorkGroupingSuggestions suggestions = groupingSuggestionManagerReadOnly.getGroupingSuggestions("orcid");
        assertNotNull(suggestions);
        assertEquals(true, suggestions.isMoreAvailable());
        assertEquals(GroupingSuggestionManagerReadOnly.SUGGESTION_BATCH_SIZE, suggestions.getSuggestions().size());

        Mockito.when(mockGroupingSuggestionCacheManager.getGroupingSuggestionCount(Mockito.eq("orcid"))).thenReturn(70);
        Mockito.when(mockGroupingSuggestionCacheManager.getGroupingSuggestions(Mockito.eq("orcid"), Mockito.eq(GroupingSuggestionManagerReadOnly.SUGGESTION_BATCH_SIZE)))
                .thenReturn(getSuggestions("orcid", 70));

        suggestions = groupingSuggestionManagerReadOnly.getGroupingSuggestions("orcid");
        assertNotNull(suggestions);
        assertEquals(false, suggestions.isMoreAvailable());
        assertEquals(70, suggestions.getSuggestions().size());

        Mockito.when(mockGroupingSuggestionCacheManager.getGroupingSuggestionCount(Mockito.eq("orcid"))).thenReturn(0);
        Mockito.when(mockGroupingSuggestionCacheManager.getGroupingSuggestions(Mockito.eq("orcid"), Mockito.eq(GroupingSuggestionManagerReadOnly.SUGGESTION_BATCH_SIZE)))
                .thenReturn(getSuggestions("orcid", 0));

        suggestions = groupingSuggestionManagerReadOnly.getGroupingSuggestions("orcid");
        assertNotNull(suggestions);
        assertEquals(false, suggestions.isMoreAvailable());
        assertEquals(0, suggestions.getSuggestions().size());
    }

    private List<WorkGroupingSuggestion> getSuggestions(String orcid, int size) {
        List<WorkGroupingSuggestion> suggestions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            WorkGroupingSuggestion suggestion = new WorkGroupingSuggestion();
            suggestion.setOrcid(orcid);
            suggestions.add(suggestion);
        }
        return suggestions;
    }

}
