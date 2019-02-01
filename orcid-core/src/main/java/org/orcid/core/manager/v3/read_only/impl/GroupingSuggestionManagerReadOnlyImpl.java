package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.GroupingSuggestionsCacheManager;
import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;
import org.orcid.pojo.grouping.WorkGroupingSuggestions;

public class GroupingSuggestionManagerReadOnlyImpl implements GroupingSuggestionManagerReadOnly {

    @Resource
    protected GroupingSuggestionsCacheManager groupingSuggestionsCacheManager;

    @Override
    public WorkGroupingSuggestions getGroupingSuggestions(String orcid) {
        if (!Features.GROUPING_SUGGESTIONS.isActive()) {
            return null;
        }
        boolean more = groupingSuggestionsCacheManager.getGroupingSuggestionCount(orcid) > SUGGESTION_BATCH_SIZE;
        List<WorkGroupingSuggestion> suggestions = groupingSuggestionsCacheManager.getGroupingSuggestions(orcid, SUGGESTION_BATCH_SIZE);
        return getWorkGroupingSuggestions(suggestions, more);
    }

    private WorkGroupingSuggestions getWorkGroupingSuggestions(List<WorkGroupingSuggestion> suggestions, boolean more) {
        WorkGroupingSuggestions workGroupingSuggestions = new WorkGroupingSuggestions();
        workGroupingSuggestions.setMoreAvailable(more);
        workGroupingSuggestions.setSuggestions(suggestions);
        return workGroupingSuggestions;
    }
    
}
