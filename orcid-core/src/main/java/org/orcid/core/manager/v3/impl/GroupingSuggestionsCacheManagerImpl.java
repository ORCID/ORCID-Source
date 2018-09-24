package org.orcid.core.manager.v3.impl;

import java.util.List;

import javax.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.v3.GroupingSuggestionsCacheManager;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionsCacheManagerImpl implements GroupingSuggestionsCacheManager {

    @Resource(name = "groupingSuggestionsCache")
    private Cache<String, List<WorkGroupingSuggestion>> cache;

    @Override
    public WorkGroupingSuggestion getGroupingSuggestion(String orcid) {
        List<WorkGroupingSuggestion> suggestions = cache.get(orcid);
        if (suggestions != null && !suggestions.isEmpty()) {
            return suggestions.remove(0);
        }
        return null;
    }

    @Override
    public void putGroupingSuggestions(String orcid, List<WorkGroupingSuggestion> suggestions) {
        cache.put(orcid, suggestions);
    }

}
