package org.orcid.core.manager.read_only;

import java.util.List;

import org.orcid.pojo.WorkGroupingSuggestion;

public interface GroupingSuggestionManagerReadOnly {
    
    List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid);

}
