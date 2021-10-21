package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.jaxb.model.v3.release.record.Work;

public interface BibtexManager {

    public String generateBibtexReferenceList(String orcid);
    
    public String generateBibtexReferenceList(String orcid, List<Long> workIds);

    String generateBibtex(String orcid, Work work);
    
}
