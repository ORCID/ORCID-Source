package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.core.exception.MissingGroupableExternalIDException;
import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public interface GroupingSuggestionManager extends GroupingSuggestionManagerReadOnly {

    void markGroupingSuggestionAsRejected(WorkGroupingSuggestion suggestion);
    
    void cacheGroupingSuggestions(String orcid, List<WorkGroupingSuggestion> suggestions);

    void markGroupingSuggestionAsAccepted(WorkGroupingSuggestion suggestion) throws MissingGroupableExternalIDException;

}
