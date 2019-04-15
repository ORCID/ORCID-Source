package org.orcid.core.manager.v3;

import org.orcid.jaxb.model.v3.release.record.Work;

public interface BibtexManager {

    public String generateBibtexReferenceList(String orcid);

    String generateBibtex(String orcid, Work work);
    
}
