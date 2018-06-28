package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;

public interface GroupingSuggestionDao extends GenericDao<GroupingSuggestionEntity, Long> {

    List<GroupingSuggestionEntity> getGroupingSuggestions(String orcid);

    GroupingSuggestionEntity findGroupingSuggestionIdByPutCodes(String orcid, Long workPutCode, List<Long> comparisonPutCodes);
    
}
