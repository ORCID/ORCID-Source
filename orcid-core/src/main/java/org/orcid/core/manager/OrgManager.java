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

import java.io.Writer;
import java.util.List;

import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgManager {

    List<AmbiguousOrgEntity> getAmbiguousOrgs(int firstResult, int maxResults);

    void writeAmbiguousOrgs(Writer writer);

    void writeDisambiguatedOrgs(Writer writer);

    List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults);
    
    List<OrgEntity> getOrgsByName(String searchTerm);

    OrgEntity createUpdate(OrgEntity org);

    OrgEntity createUpdate(OrgEntity org, Long orgDisambiguatedId);

}
