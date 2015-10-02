package org.orcid.api.common.util;

import org.orcid.jaxb.model.record.ResearcherUrls;

public class ElementUtils {

    public static void setPathToResearcherUrls(ResearcherUrls researcherUrls, String orcid) {
        researcherUrls.setPath('/' + orcid + "/researcher-urls" );
    }

}
