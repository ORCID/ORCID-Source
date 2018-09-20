package org.orcid.core.manager.v3.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.v3.GroupingSuggestionsCacheManager;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionsCacheManagerImpl implements GroupingSuggestionsCacheManager {
    
    @Resource(name = "groupingSuggestionsCache")
    private Cache<String, Optional<List<WorkGroupingSuggestion>>> cache;   

    @Override
    public WorkGroupingSuggestion getGroupingSuggestion(String orcid) {
        Optional<List<WorkGroupingSuggestion>> entry = cache.get(orcid);
        if (entry.isPresent()) {
            List<WorkGroupingSuggestion> suggestions = entry.get();
            WorkGroupingSuggestion suggestion = suggestions.remove(0);
            if (suggestions.isEmpty()) {
                cache.put(orcid, Optional.empty());
            } else {
                cache.put(orcid, Optional.of(suggestions));
            }
            return suggestion;
        }
        return null;
    }

    @Override
    public void putGroupingSuggestions(String orcid, List<WorkGroupingSuggestion> suggestions) {
        cache.put(orcid, Optional.of(suggestions));
    }
    
}
