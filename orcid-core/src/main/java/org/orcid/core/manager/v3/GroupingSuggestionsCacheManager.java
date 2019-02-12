package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public interface GroupingSuggestionsCacheManager {
    
    List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid, int max);
    
    void putGroupingSuggestions(String orcid, List<WorkGroupingSuggestion> suggestions);
    
    int getGroupingSuggestionCount(String orcid);

    void removeGroupingSuggestion(WorkGroupingSuggestion suggestion);

}
