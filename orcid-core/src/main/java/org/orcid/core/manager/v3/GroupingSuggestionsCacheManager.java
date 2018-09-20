package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public interface GroupingSuggestionsCacheManager {
    
    WorkGroupingSuggestion getGroupingSuggestion(String orcid);
    
    void putGroupingSuggestions(String orcid, List<WorkGroupingSuggestion> suggestions);

}
