package org.orcid.core.manager.impl;

import org.orcid.core.manager.GroupingSuggestionManager;
import org.orcid.core.manager.read_only.impl.GroupingSuggestionManagerReadOnlyImpl;
import org.orcid.persistence.dao.GroupingSuggestionDao;

public class GroupingSuggestionManagerImpl extends GroupingSuggestionManagerReadOnlyImpl implements GroupingSuggestionManager {

    private GroupingSuggestionDao groupingSuggestionDao;

    @Override
    public void generateGroupingSuggestionsForProfile(String orcid) {
        // TODO Auto-generated method stub

    }

    public void setGroupingSuggestionDao(GroupingSuggestionDao groupingSuggestionDao) {
        this.groupingSuggestionDao = groupingSuggestionDao;
    }
}
