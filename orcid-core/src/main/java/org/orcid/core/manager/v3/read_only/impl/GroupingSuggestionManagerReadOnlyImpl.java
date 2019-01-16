package org.orcid.core.manager.v3.read_only.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.GroupingSuggestionsCacheManager;
import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionManagerReadOnlyImpl implements GroupingSuggestionManagerReadOnly {

    @Resource
    protected GroupingSuggestionsCacheManager groupingSuggestionsCacheManager;

    @Override
    public WorkGroupingSuggestion getGroupingSuggestion(String orcid) {
        if (!Features.GROUPING_SUGGESTIONS.isActive()) {
            return null;
        }
        return groupingSuggestionsCacheManager.getGroupingSuggestion(orcid);
    }
    
}
