package org.orcid.core.manager;

import org.orcid.jaxb.model.record_v2.Work;

public interface BibtexManager {

    public String generateBibtexReferenceList(String orcid);

    String generateBibtex(String orcid, Work work);
    
}
