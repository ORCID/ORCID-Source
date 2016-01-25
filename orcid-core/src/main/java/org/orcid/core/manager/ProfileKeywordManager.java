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
package org.orcid.core.manager;

import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;

public interface ProfileKeywordManager {
    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    Keywords getKeywords(String orcid, long lastModified);
    
    /**
     * Return the list of public keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    Keywords getPublicKeywords(String orcid, long lastModified);
    
    Keyword getKeyword(String orcid, Long putCode);

    boolean deleteKeyword(String orcid, Long putCode, boolean checkSource);
    
    boolean updateKeywordsVisibility(String orcid, Visibility defaultVisiblity);

    Keyword createKeyword(String orcid, Keyword keyword);

    Keyword updateKeyword(String orcid, Long putCode, Keyword keyword);

    Keywords updateKeywords(String orcid, Keywords keywords, Visibility defaultVisiblity);
}
