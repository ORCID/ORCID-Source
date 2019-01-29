package org.orcid.core.manager.v3;

import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.v3.rc2.search.Search;

/**
 * Class to retrieve OrcidMessage objects (with nested Search Results) relating
 * to all the information that could be found given the search criteria found.
 * The info needs to exist in both the search index and the Orcid DB.
 * 
 * 
 * @author jamesb
 * @See OrcidMessage
 * @See OrcidSearchResults
 * 
 */
public interface OrcidSearchManager {

    static final int DEFAULT_SEARCH_ROWS = 100;
    
    static final int MAX_SEARCH_START = 10000;      

    static final int MAX_SEARCH_ROWS = 200; 
    
    Search findOrcidIds(Map<String, List<String>> queryParameters);
    
    Search findOrcidsByQuery(String query, Integer start, Integer rows);
}
