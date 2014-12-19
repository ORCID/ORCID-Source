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

import org.orcid.jaxb.model.message.OrcidProfile;

public interface OrcidProfileCacheManager {

    public OrcidProfile retrievePublic(String orcid);
    
    public OrcidProfile retrieve(String orcid);
    
    @Deprecated 
    public void put(String orcid, OrcidProfile orcidProfile);
    
    public void put(OrcidProfile orcidProfile);
    
    public void removeAll();
    
    public void remove(String orcid);
    
}
