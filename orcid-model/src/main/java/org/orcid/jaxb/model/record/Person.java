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
package org.orcid.jaxb.model.record;

import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = -8325786136224198780L;
    ResearcherUrls resercherUrls;

    public ResearcherUrls getResercherUrls() {
        return resercherUrls;
    }

    public void setResercherUrls(ResearcherUrls resercherUrls) {
        this.resercherUrls = resercherUrls;
    }        
}
