package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public interface GroupingSuggestionManager extends GroupingSuggestionManagerReadOnly {

    void generateGroupingSuggestionsForProfile(String orcid, Work work, Works groupedWorks);
    
    void markGroupingSuggestionAsAccepted(String orcid, Long id);
    
    void markGroupingSuggestionAsRejected(String orcid, Long id);

    boolean suggestionValid(WorkGroupingSuggestion suggestion, Works workGroups);
    
    void removeSuggestion(WorkGroupingSuggestion workGroupingSuggestion);

}
