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

import java.util.List;

import org.orcid.persistence.jpa.entities.OrgEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface OrgManager {

    List<OrgEntity> getAmbiguousOrgs();
    
    List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults);
    
    OrgEntity createUpdate(OrgEntity org);
    
    OrgEntity createUpdate(OrgEntity org, Integer orgDisambiguatedId);
    
}
