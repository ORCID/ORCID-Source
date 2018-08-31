package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.orcid.core.adapter.jsonidentifier.JSONWorkPutCodes;
import org.orcid.core.manager.GroupingSuggestionManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.persistence.dao.GroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;

public class GroupingSuggestionManagerImpl implements GroupingSuggestionManager {

    private GroupingSuggestionDao groupingSuggestionDao;

    @Override
    public void generateGroupingSuggestionsForProfile(String orcid, Work work, Works groupedWorks) {
        for (WorkGroup workGroup : groupedWorks.getWorkGroup()) {
            GroupingSuggestionEntity suggestion = getGroupingSuggestionForGroup(orcid, work, workGroup); 
            if (suggestion != null) {
                List<Long> putCodes = new ArrayList<>();
                for (WorkSummary workSummary : workGroup.getWorkSummary()) {
                    putCodes.add(workSummary.getPutCode());
                }
                GroupingSuggestionEntity existingSuggestion = groupingSuggestionDao.findGroupingSuggestionIdByPutCodes(orcid, work.getPutCode(), putCodes);

                // existing suggestion is superseded if exists and hasn't been dismissed. if has been dismissed don't create again
                if (existingSuggestion != null && existingSuggestion.getDismissedDate() == null) {
                    groupingSuggestionDao.remove(existingSuggestion.getId());
                    groupingSuggestionDao.persist(suggestion);
                } else if (existingSuggestion == null) {
                    groupingSuggestionDao.persist(suggestion);
                }
            }
        }
    }

    private GroupingSuggestionEntity getGroupingSuggestionForGroup(String orcid, Work work, WorkGroup workGroup) {
        List<Long> groupPutCodes = new ArrayList<>();
        groupPutCodes.add(work.getPutCode());
        boolean suggestGrouping = false;
        for (WorkSummary workSummary : workGroup.getWorkSummary()) {
            if (work.getPutCode().equals(workSummary.getPutCode())) {
                // work is already part of this group
                return null;
            }

            groupPutCodes.add(workSummary.getPutCode());
            if (!suggestGrouping && appearEqual(work, workSummary)) {
                suggestGrouping = true;
            }
        }
        return suggestGrouping ? getGroupingSuggestionEntity(orcid, groupPutCodes) : null;
    }

    private GroupingSuggestionEntity getGroupingSuggestionEntity(String orcid, List<Long> groupPutCodes) {
        JSONWorkPutCodes jsonPutCodes = new JSONWorkPutCodes();
        jsonPutCodes.setWorkPutCodes(groupPutCodes.toArray(new Long[0]));
        
        GroupingSuggestionEntity entity = new GroupingSuggestionEntity();
        entity.setDateCreated(new Date());
        entity.setLastModified(entity.getDateCreated());
        entity.setWorkPutCodes(JsonUtils.convertToJsonString(jsonPutCodes));
        entity.setOrcid(orcid);
        return entity;
    }

    private boolean appearEqual(Work work, WorkSummary workSummary) {
        if (workTitleEmpty(work.getWorkTitle()) || workTitleEmpty(workSummary.getTitle())) {
            return false;
        }
        
        String a = work.getWorkTitle().getTitle().getContent();
        String b = workSummary.getTitle().getTitle().getContent();
        return transformForTitleComparison(a).equals(transformForTitleComparison(b));
    }
    
    private String transformForTitleComparison(String titleContent) {
        return titleContent.toLowerCase().replaceAll("\\s", "");
    }

    private boolean workTitleEmpty(WorkTitle workTitle) {
        return workTitle == null || workTitle.getTitle() == null || workTitle.getTitle().getContent() == null || workTitle.getTitle().getContent().isEmpty();
    }

    public void setGroupingSuggestionDao(GroupingSuggestionDao groupingSuggestionDao) {
        this.groupingSuggestionDao = groupingSuggestionDao;
    }
}
