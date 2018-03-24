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
