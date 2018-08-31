package org.orcid.core.manager.v3.read_only.impl;

import org.orcid.core.adapter.jsonidentifier.JSONWorkPutCodes;
import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.GroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionManagerReadOnlyImpl implements GroupingSuggestionManagerReadOnly {

    protected GroupingSuggestionDao groupingSuggestionDao;

    @Override
    public WorkGroupingSuggestion getGroupingSuggestion(String orcid) {
        GroupingSuggestionEntity suggestionEntity = groupingSuggestionDao.getNextGroupingSuggestion(orcid);
        if (suggestionEntity != null) {
            WorkGroupingSuggestion suggestionPojo = new WorkGroupingSuggestion();
            JSONWorkPutCodes putCodes = JsonUtils.readObjectFromJsonString(suggestionEntity.getWorkPutCodes(), JSONWorkPutCodes.class);
            suggestionPojo.setId(suggestionEntity.getId());
            suggestionPojo.setOrcid(orcid);
            suggestionPojo.setPutCodes(putCodes);
            return suggestionPojo;
        } else {
            return null;
        }
    }

    public void setGroupingSuggestionDao(GroupingSuggestionDao groupingSuggestionDao) {
        this.groupingSuggestionDao = groupingSuggestionDao;
    }

}
