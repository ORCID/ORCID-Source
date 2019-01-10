package org.orcid.core.manager.v3.impl;

import org.orcid.core.manager.v3.GroupingSuggestionManager;
import org.orcid.core.manager.v3.read_only.impl.GroupingSuggestionManagerReadOnlyImpl;
import org.orcid.persistence.jpa.entities.RejectedGroupingSuggestionEntity;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionManagerImpl extends GroupingSuggestionManagerReadOnlyImpl implements GroupingSuggestionManager {

    @Override
    public void markGroupingSuggestionAsRejected(WorkGroupingSuggestion suggestion) {
        RejectedGroupingSuggestionEntity entity = getGroupingSuggestionEntity(suggestion);
        rejectedGroupingSuggestionDao.persist(entity);
    }

    private RejectedGroupingSuggestionEntity getGroupingSuggestionEntity(WorkGroupingSuggestion suggestion) {
        RejectedGroupingSuggestionEntity entity = new RejectedGroupingSuggestionEntity();
        entity.setOrcid(suggestion.getOrcid());
        entity.setId(suggestion.getPutCodesAsString());
        return entity;
    }
}
