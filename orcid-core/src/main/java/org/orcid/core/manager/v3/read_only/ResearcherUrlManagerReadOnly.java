package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;

public interface ResearcherUrlManagerReadOnly {
    /**
     * Return the list of public researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of public researcher urls associated with the orcid profile
     * */
    ResearcherUrls getPublicResearcherUrls(String orcid);
    
    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    ResearcherUrls getResearcherUrls(String orcid);
    
    /**
     * Retrieve a researcher url from database
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    ResearcherUrl getResearcherUrl(String orcid, long id);
}
