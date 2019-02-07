package org.orcid.core.manager.v3.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.v3.GroupingSuggestionsCacheManager;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionsCacheManagerImpl implements GroupingSuggestionsCacheManager {

    @Resource(name = "groupingSuggestionsCache")
    private Cache<String, List<WorkGroupingSuggestion>> cache;

    @Override
    public List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid, int max) {
        List<WorkGroupingSuggestion> suggestions = cache.get(orcid);
        List<WorkGroupingSuggestion> subList = new ArrayList<>();
        if (suggestions != null) {
            for (int i = 0; !suggestions.isEmpty() && i < max; i++) {
                subList.add(suggestions.remove(0));
            }
            putGroupingSuggestions(orcid, suggestions);
        }
        return subList;
    }

    @Override
    public void putGroupingSuggestions(String orcid, List<WorkGroupingSuggestion> suggestions) {
        cache.put(orcid, suggestions);
    }

    @Override
    public int getGroupingSuggestionCount(String orcid) {
        return cache.get(orcid) != null ? cache.get(orcid).size() : 0;
    }

}
