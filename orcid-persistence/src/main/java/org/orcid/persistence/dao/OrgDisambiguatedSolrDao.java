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
package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.solr.entities.OrgDisambiguatedSolrDocument;

public interface OrgDisambiguatedSolrDao {

    public void persist(OrgDisambiguatedSolrDocument orgDisambiguatedSolrDocument);

    public OrgDisambiguatedSolrDocument findById(Long id);

    public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult);
    public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult, boolean fundersOnly);    

}
