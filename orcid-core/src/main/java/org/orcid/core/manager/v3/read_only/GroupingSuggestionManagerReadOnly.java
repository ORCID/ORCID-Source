package org.orcid.core.manager.v3.read_only;

import org.orcid.pojo.grouping.WorkGroupingSuggestions;

public interface GroupingSuggestionManagerReadOnly {
    
    static final int SUGGESTION_BATCH_SIZE = 100;
    
    WorkGroupingSuggestions getGroupingSuggestions(String orcid);

}
