package org.orcid.core.manager.v3.read_only;

import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public interface GroupingSuggestionManagerReadOnly {
    
    WorkGroupingSuggestion getGroupingSuggestion(String orcid);

}
