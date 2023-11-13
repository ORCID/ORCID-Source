package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.pojo.OrgDisambiguated;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgDisambiguatedManager {

    void processOrgsForIndexing();
    
    void markOrgsForIndexingAsGroup();

    void processOrgsWithIncorrectPopularity();
    
    public List<OrgDisambiguated> searchOrgsFromSolr(String searchTerm, int firstResult, int maxResult, boolean fundersOnly);
    
    List<OrgDisambiguated> searchOrgsFromSolrForSelfService(String searchTerm, int firstResult, int maxResult);
    
    public OrgDisambiguated findInDB(Long id);
    
    public OrgDisambiguated findInDB(String idValue, String idType);

    OrgDisambiguatedEntity updateOrgDisambiguated(OrgDisambiguatedEntity orgDisambiguatedEntity);

    OrgDisambiguatedEntity createOrgDisambiguated(OrgDisambiguatedEntity orgDisambiguatedEntity);

    void updateOrgDisambiguatedExternalIdentifier(OrgDisambiguatedExternalIdentifierEntity identifier);

    void createOrgDisambiguatedExternalIdentifier(OrgDisambiguatedExternalIdentifierEntity identifier);
    
    public List<OrgDisambiguated> findOrgDisambiguatedIdsForSameExternalIdentifier(String identifier, String type);
    
    public void cleanDuplicatedExternalIdentifiersForOrgDisambiguated(OrgDisambiguatedEntity orgDisambiguatedEntity);

}
