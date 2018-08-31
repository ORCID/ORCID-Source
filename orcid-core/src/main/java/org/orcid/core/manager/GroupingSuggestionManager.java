package org.orcid.core.manager;

import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Work;

public interface GroupingSuggestionManager  {

    void generateGroupingSuggestionsForProfile(String orcid, Work work, Works groupedWorks);

}
