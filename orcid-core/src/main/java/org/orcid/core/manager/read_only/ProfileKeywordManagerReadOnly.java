package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;

public interface ProfileKeywordManagerReadOnly { 
    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    Keywords getKeywords(String orcid);
    
    /**
     * Return the list of public keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    Keywords getPublicKeywords(String orcid);
    
    Keyword getKeyword(String orcid, Long putCode);
}
