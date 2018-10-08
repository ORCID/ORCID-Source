package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.RejectedGroupingSuggestionEntity;

public interface RejectedGroupingSuggestionDao extends GenericDao<RejectedGroupingSuggestionEntity, String> {

    RejectedGroupingSuggestionEntity findGroupingSuggestionIdAndOrcid(String orcid, String putCodes);
    
}
