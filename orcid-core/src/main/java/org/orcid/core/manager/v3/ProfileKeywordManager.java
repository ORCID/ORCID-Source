/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.jaxb.model.v3.dev1.record.Keywords;

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
