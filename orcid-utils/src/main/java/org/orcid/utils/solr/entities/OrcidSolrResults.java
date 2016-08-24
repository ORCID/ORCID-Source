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
package org.orcid.utils.solr.entities;

import java.util.List;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidSolrResults {

    private long numFound;
    private List<OrcidSolrResult> results;

    public long getNumFound() {
        return numFound;
    }

    public void setNumFound(long numFound) {
        this.numFound = numFound;
    }

    public List<OrcidSolrResult> getResults() {
        return results;
    }

    public void setResults(List<OrcidSolrResult> results) {
        this.results = results;
    }

}
