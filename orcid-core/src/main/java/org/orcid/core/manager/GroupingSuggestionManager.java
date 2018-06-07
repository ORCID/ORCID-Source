package org.orcid.core.manager;

import java.util.List;

import org.orcid.pojo.WorkGroupingSuggestion;

public interface GroupingSuggestionManager {
    
    List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid);
    
    void generateGroupingSuggestionsForProfile(String orcid);

}
