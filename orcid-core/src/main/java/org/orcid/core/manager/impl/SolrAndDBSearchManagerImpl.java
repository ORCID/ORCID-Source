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
package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.SolrAndDBSearchManager;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.Visibility;

public class SolrAndDBSearchManagerImpl implements SolrAndDBSearchManager {

    @Resource
    private VisibilityFilter visibilityFilter;

    @Resource
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    public void setOrcidSearchManager(OrcidSearchManager orcidSearchManager) {
        this.orcidSearchManager = orcidSearchManager;
    }

    public void setOrcidProfileManager(OrcidProfileManager orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }
    
    @Override
    public OrcidMessage findFilteredOrcidsBasedOnQuery(String query, Integer start, Integer rows) {

        OrcidMessage solrSearchResults = orcidSearchManager.findOrcidsByQuery(query, start, rows);
        OrcidMessage filteredMessage = new OrcidMessage();
        OrcidSearchResults orcidSearchResults = solrSearchResults.getOrcidSearchResults();

        if (orcidSearchResults != null && orcidSearchResults.getOrcidSearchResult() != null) {

            OrcidSearchResults searchResultsToFilter = new OrcidSearchResults();

            for (OrcidSearchResult solrSearchResult : orcidSearchResults.getOrcidSearchResult()) {
                OrcidProfile unfilteredOrcidProfile = orcidProfileManager.retrieveOrcidProfile(solrSearchResult.getOrcidProfile().getOrcid().getValue());

                if (unfilteredOrcidProfile != null) {
                    OrcidSearchResult consolidatedSearchResult = new OrcidSearchResult();
                    consolidatedSearchResult.setRelevancyScore(solrSearchResult.getRelevancyScore());
                    consolidatedSearchResult.setOrcidProfile(unfilteredOrcidProfile);
                    searchResultsToFilter.getOrcidSearchResult().add(consolidatedSearchResult);
                }

                continue;

            }

            filteredMessage.setOrcidSearchResults(searchResultsToFilter);
        }
        return visibilityFilter.filter(filteredMessage, Visibility.PUBLIC);
    }

}
