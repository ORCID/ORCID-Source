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
package org.orcid.api.common.util;

import org.orcid.jaxb.model.record_rc2.ResearcherUrls;

public class ElementUtils {

    public static void setPathToResearcherUrls(ResearcherUrls researcherUrls, String orcid) {
        researcherUrls.setPath('/' + orcid + "/researcher-urls" );
    }

}
