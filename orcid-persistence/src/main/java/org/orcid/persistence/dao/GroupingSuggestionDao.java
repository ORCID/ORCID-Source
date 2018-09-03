package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;

public interface GroupingSuggestionDao extends GenericDao<GroupingSuggestionEntity, Long> {

    /**
     * Returns most recently generated grouping suggestion
     * @param orcid
     * @return
     */
    GroupingSuggestionEntity getNextGroupingSuggestion(String orcid);

    GroupingSuggestionEntity findGroupingSuggestionIdByPutCodes(String orcid, Long workPutCode, List<Long> comparisonPutCodes);

    GroupingSuggestionEntity getGroupingSuggestion(String orcid, Long id);
    
}
