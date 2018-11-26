package org.orcid.core.manager;

import java.util.List;

import org.orcid.pojo.OrgDisambiguated;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgDisambiguatedManager {

    void processOrgsForIndexing();

    void processOrgsWithIncorrectPopularity();
    
    public List<OrgDisambiguated> searchOrgsFromSolr(String searchTerm, int firstResult, int maxResult, boolean fundersOnly);
    
    List<OrgDisambiguated> searchOrgsFromSolrForSelfService(String searchTerm, int firstResult, int maxResult);
    
    public OrgDisambiguated findInDB(Long id);
    
    public OrgDisambiguated findInDB(String idValue, String idType);

}
