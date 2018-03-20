package org.orcid.core.manager;

import java.util.List;

import org.orcid.core.crossref.CrossRefMetadata;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface CrossRefManager {

    List<CrossRefMetadata> searchForMetadata(String searchTerms);

    String searchForMetadataAsString(String searchTerms);

}
