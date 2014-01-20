/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.HashMap;

import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.ajaxForm.Work;

public interface ActivityCacheManager {

    public HashMap<String, Work> pubMinWorksMap(OrcidProfile profile);
    
    public HashMap<String, Funding> fundingMap(OrcidProfile profile);
    
    public HashMap<String, Affiliation> affiliationMap(OrcidProfile profile);
    
}
