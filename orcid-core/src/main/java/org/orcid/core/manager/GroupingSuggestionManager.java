package org.orcid.core.manager;

import org.orcid.core.manager.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Work;

public interface GroupingSuggestionManager extends GroupingSuggestionManagerReadOnly {

    void generateGroupingSuggestionsForProfile(String orcid, Work work, Works groupedWorks);

}
