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
package org.orcid.frontend.web.forms;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.springframework.util.AutoPopulatingList;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SearchForDelegatesForm {

    private List<SearchForDelegatesResult> results;

    public SearchForDelegatesForm() {
    }

    public SearchForDelegatesForm(OrcidSearchResults orcidSearchResults) {
        List<SearchForDelegatesResult> results = new ArrayList<SearchForDelegatesResult>();
        for (OrcidSearchResult orcidSearchResult : orcidSearchResults.getOrcidSearchResult()) {
            SearchForDelegatesResult result = new SearchForDelegatesResult();
            result.setOrcid(orcidSearchResult.getOrcidProfile().getOrcidIdentifier().getPath());
            OrcidBio orcidBio = orcidSearchResult.getOrcidProfile().getOrcidBio();
            result.setCreditName(orcidBio.getPersonalDetails().getCreditName().getContent());
            result.setEmail(orcidBio.getContactDetails().retrievePrimaryEmail().getValue());
            results.add(result);
        }
        setResults(results);
    }

    public List<SearchForDelegatesResult> getResults() {
        return results;
    }

    public void setResults(List<SearchForDelegatesResult> results) {
        this.results = new AutoPopulatingList<SearchForDelegatesResult>(results, SearchForDelegatesResult.class);
    }

}
