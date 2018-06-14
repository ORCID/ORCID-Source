package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;

public interface GroupingSuggestionManager extends GroupingSuggestionManagerReadOnly {

    void generateGroupingSuggestionsForProfile(String orcid, Work work, Works groupedWorks);

}
