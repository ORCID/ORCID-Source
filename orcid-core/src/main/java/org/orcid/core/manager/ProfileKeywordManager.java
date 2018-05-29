package org.orcid.core.manager;

import org.orcid.core.manager.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;

public interface ProfileKeywordManager extends ProfileKeywordManagerReadOnly {
    boolean deleteKeyword(String orcid, Long putCode, boolean checkSource);
    
    Keyword createKeyword(String orcid, Keyword keyword, boolean isApiRequest);

    Keyword updateKeyword(String orcid, Long putCode, Keyword keyword, boolean isApiRequest);

    Keywords updateKeywords(String orcid, Keywords keywords);
    
    /**
     * Removes all keywords that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all keywords will be
     *            removed.
     */
    void removeAllKeywords(String orcid);
}
