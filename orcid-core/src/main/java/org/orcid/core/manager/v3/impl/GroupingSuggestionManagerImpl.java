package org.orcid.core.manager.v3.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.GroupingSuggestionManager;
import org.orcid.core.manager.v3.read_only.impl.GroupingSuggestionManagerReadOnlyImpl;
import org.orcid.persistence.dao.RejectedGroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.RejectedGroupingSuggestionEntity;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionManagerImpl extends GroupingSuggestionManagerReadOnlyImpl implements GroupingSuggestionManager {

    @Resource
    private RejectedGroupingSuggestionDao rejectedGroupingSuggestionDao;

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

    @Override
    public void cacheGroupingSuggestions(String orcid, List<WorkGroupingSuggestion> suggestions) {
        suggestions = suggestions.stream().filter(s -> {
            RejectedGroupingSuggestionEntity rejection = rejectedGroupingSuggestionDao.findGroupingSuggestionIdAndOrcid(s.getOrcid(), s.getPutCodesAsString());
            return rejection == null;
        }).collect(Collectors.toList());
        groupingSuggestionsCacheManager.putGroupingSuggestions(orcid, suggestions);
    }
    
    public void setRejectedGroupingSuggestionDao(RejectedGroupingSuggestionDao rejectedGroupingSuggestionDao) {
        this.rejectedGroupingSuggestionDao = rejectedGroupingSuggestionDao;
    }
}
