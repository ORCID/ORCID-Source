package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public interface GroupingSuggestionManager extends GroupingSuggestionManagerReadOnly {

    void generateGroupingSuggestionsForProfile(String orcid, Work work, Works groupedWorks);
    
    List<WorkGroupingSuggestion> filterSuggestionsNoLongerApplicable(List<WorkGroupingSuggestion> suggestions, Works workGroups);
    
    void markGroupingSuggestionAsAccepted(Long id);

}
