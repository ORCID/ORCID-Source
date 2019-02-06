package org.orcid.core.manager.v3.read_only;

import org.orcid.pojo.grouping.WorkGroupingSuggestions;
import org.orcid.pojo.grouping.WorkGroupingSuggestionsCount;

public interface GroupingSuggestionManagerReadOnly {
    
    static final int SUGGESTION_BATCH_SIZE = 100;
    
    WorkGroupingSuggestions getGroupingSuggestions(String orcid);
    
    WorkGroupingSuggestionsCount getGroupingSuggestionCount(String currentUserOrcid);

}
