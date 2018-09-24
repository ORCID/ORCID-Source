package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public interface GroupingSuggestionManager extends GroupingSuggestionManagerReadOnly {

    void markGroupingSuggestionAsRejected(WorkGroupingSuggestion suggestion);

}
