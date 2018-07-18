package org.orcid.core.manager.v3.read_only.impl;

import java.util.ArrayList;
import java.util.List;

import org.orcid.core.adapter.jsonidentifier.JSONWorkPutCodes;
import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.GroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class GroupingSuggestionManagerReadOnlyImpl implements GroupingSuggestionManagerReadOnly {

    protected GroupingSuggestionDao groupingSuggestionDao;
    
    @Override
    public List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid) {
        List<WorkGroupingSuggestion> suggestionPojos = new ArrayList<WorkGroupingSuggestion>();
        List<GroupingSuggestionEntity> groupingSuggestionEntities = groupingSuggestionDao.getGroupingSuggestions(orcid);
        for (GroupingSuggestionEntity suggestionEntity : groupingSuggestionEntities) {
            WorkGroupingSuggestion suggestionPojo = new WorkGroupingSuggestion();
            JSONWorkPutCodes putCodes = JsonUtils.readObjectFromJsonString(suggestionEntity.getWorkPutCodes(), JSONWorkPutCodes.class);
            suggestionPojo.setId(suggestionEntity.getId());
            suggestionPojo.setOrcid(orcid);
            suggestionPojo.setPutCodes(putCodes);
            suggestionPojos.add(suggestionPojo);
        }
        return suggestionPojos;
    }

    public void setGroupingSuggestionDao(GroupingSuggestionDao groupingSuggestionDao) {
        this.groupingSuggestionDao = groupingSuggestionDao;
    }
    
}
