package org.orcid.core.manager.v3.read_only.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.GroupingSuggestionsCacheManager;
import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.persistence.dao.RejectedGroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.RejectedGroupingSuggestionEntity;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionManagerReadOnlyImpl implements GroupingSuggestionManagerReadOnly {

    protected RejectedGroupingSuggestionDao rejectedGroupingSuggestionDao;

    @Resource
    private GroupingSuggestionsCacheManager groupingSuggestionsCacheManager;

    @Override
    public WorkGroupingSuggestion getGroupingSuggestion(String orcid) {
        if (!Features.GROUPING_SUGGESTIONS.isActive()) {
            return null;
        }

        WorkGroupingSuggestion suggestion = groupingSuggestionsCacheManager.getGroupingSuggestion(orcid);
        if (suggestion != null) {
            RejectedGroupingSuggestionEntity rejection = rejectedGroupingSuggestionDao.findGroupingSuggestionIdAndOrcid(suggestion.getOrcid(),
                    suggestion.getPutCodesAsString());
            while (rejection != null) {
                suggestion = groupingSuggestionsCacheManager.getGroupingSuggestion(orcid);
                if (suggestion == null) {
                    return null;
                }
                rejection = rejectedGroupingSuggestionDao.findGroupingSuggestionIdAndOrcid(suggestion.getOrcid(), suggestion.getPutCodesAsString());
            }
        }
        return suggestion;
    }

    public void setRejectedGroupingSuggestionDao(RejectedGroupingSuggestionDao rejectedGroupingSuggestionDao) {
        this.rejectedGroupingSuggestionDao = rejectedGroupingSuggestionDao;
    }

}
