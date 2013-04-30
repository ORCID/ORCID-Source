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

import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public interface ResearcherUrlManager {
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid);
    public boolean deleteResearcherUrl(long id);
    public ResearcherUrlEntity getResearcherUrl(long id);
    public void addResearcherUrls(String orcid, String url, String urlName);
    public boolean updateResearcherUrls(String orcid, ResearcherUrls researcherUrls);
}
