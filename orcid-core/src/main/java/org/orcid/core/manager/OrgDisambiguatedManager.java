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

import java.util.List;
import java.util.Locale;

import org.orcid.pojo.OrgDisambiguated;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgDisambiguatedManager {

    void processOrgsForIndexing();

    void processOrgsWithIncorrectPopularity();
    
    public List<OrgDisambiguated> searchOrgsFromSolr(String searchTerm, int firstResult, int maxResult, boolean fundersOnly);
    
    public OrgDisambiguated findInDB(Long id);

}
