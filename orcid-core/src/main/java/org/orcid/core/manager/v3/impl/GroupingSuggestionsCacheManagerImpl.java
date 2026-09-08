package org.orcid.core.manager.v3.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.github.benmanes.caffeine.cache.Cache;
import org.orcid.core.manager.v3.GroupingSuggestionsCacheManager;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionsCacheManagerImpl implements GroupingSuggestionsCacheManager {

    @Resource(name = "groupingSuggestionsCache")
    private Cache<String, List<WorkGroupingSuggestion>> cache;

    @Override
    public List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid, int max) {
        List<WorkGroupingSuggestion> suggestions = cache.getIfPresent(orcid);
        if (suggestions == null) {
            // could be null if never generated (feature switched on before caches refreshed)
            return new ArrayList<>();
        }
        return suggestions.size() > max ? suggestions.subList(0, max) : suggestions;
    }

    @Override
    public void putGroupingSuggestions(String orcid, List<WorkGroupingSuggestion> suggestions) {
        cache.put(orcid, suggestions);
    }

    @Override
    public int getGroupingSuggestionCount(String orcid) {
        List<WorkGroupingSuggestion> suggestions = cache.getIfPresent(orcid);
        return suggestions != null ? suggestions.size() : 0;
    }
    
    @Override
    public void removeGroupingSuggestion(WorkGroupingSuggestion suggestion) {
        List<WorkGroupingSuggestion> suggestions = cache.getIfPresent(suggestion.getOrcid());
        if (suggestions != null) {
            List<WorkGroupingSuggestion> filtered = suggestions.stream().filter(s -> !s.getPutCodesAsString().equals(suggestion.getPutCodesAsString())).collect(Collectors.toList());
            cache.put(suggestion.getOrcid(), filtered);
        }
    }

}
