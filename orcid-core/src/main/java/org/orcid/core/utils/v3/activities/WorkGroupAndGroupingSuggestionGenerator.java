package org.orcid.core.utils.v3.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.GroupableActivity;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkGroupAndGroupingSuggestionGenerator extends ActivitiesGroupGenerator {
    
    private static final Logger LOG = LoggerFactory.getLogger(WorkGroupAndGroupingSuggestionGenerator.class);

    private Map<String, List<ActivitiesGroup>> potentialGroupingSuggestions = new HashMap<>();

    public void group(GroupableActivity activity) {
        if (!(activity instanceof WorkSummary)) {
            throw new IllegalArgumentException("Argument must be of type WorkSummary");
        }
        WorkSummary workSummary = (WorkSummary) activity;
        
        if (Features.WORKS_FAILURE_DEBUG.isActive()) {
            LOG.info("Grouping work {}", workSummary.getPutCode());
        }

        if (groups.isEmpty()) {
            // If it is the first activity, create a new group for it
            ActivitiesGroup newGroup = createNewGroup(activity);
            mapGroupToTitle(newGroup, workSummary);
        } else {
            // If it is not the first activity, check which groups it belongs to
            List<ActivitiesGroup> belongsTo = generateBelongsToList(activity);

            // If it doesnt belong to any group, create a new group for it
            if (belongsTo.isEmpty()) {
                ActivitiesGroup newGroup = createNewGroup(activity);
                mapGroupToTitle(newGroup, workSummary);
            } else {
                // Get the first group it belongs to
                ActivitiesGroup firstGroup = belongsTo.get(0);
                firstGroup.add(activity);
                mapGroupToTitle(firstGroup, workSummary);

                // If it belongs to other groups, merge them into the first one
                if (belongsTo.size() > 1) {
                    for (int i = 1; i < belongsTo.size(); i++) {
                        // Merge the group
                        mergeAndRemoveGroup(firstGroup, belongsTo.get(i));
                        switchGroup(belongsTo.get(i), firstGroup);
                    }
                }
                updateLookupKeys(firstGroup);
            }
        }
    }

    public List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid) {
        List<WorkGroupingSuggestion> suggestions = new ArrayList<>();
        for (String title : potentialGroupingSuggestions.keySet()) {
            List<ActivitiesGroup> groups = potentialGroupingSuggestions.get(title);
            if (groups.size() > 1) {
                WorkGroupingSuggestion suggestion = new WorkGroupingSuggestion();
                suggestion.setOrcid(orcid);
                List<Long> putCodes = new ArrayList<>();

                boolean groupableExternalIdFound = false;
                for (ActivitiesGroup group : groups) {
                    for (GroupableActivity activity : group.getActivities()) {
                        WorkSummary workSummary = (WorkSummary) activity;
                        putCodes.add(workSummary.getPutCode());
                        if (!groupableExternalIdFound) {
                            for (ExternalID externalId : workSummary.getExternalIdentifiers().getExternalIdentifier()) {
                                if (externalId.isGroupAble()) {
                                    groupableExternalIdFound = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                // without at least one groupable external id present works can't be grouped
                if (groupableExternalIdFound) {
                    suggestion.setPutCodes(putCodes);
                    suggestions.add(suggestion);
                }
            }
        }
        return suggestions;
    }

    private void switchGroup(ActivitiesGroup oldGroup, ActivitiesGroup newGroup) {
        for (String title : potentialGroupingSuggestions.keySet()) {
            List<ActivitiesGroup> mappedGroups = potentialGroupingSuggestions.get(title);
            if (mappedGroups.contains(oldGroup)) {
                mappedGroups.remove(oldGroup);
                if (!mappedGroups.contains(newGroup)) {
                    mappedGroups.add(newGroup);
                }
            }
        }
    }

    private void mapGroupToTitle(ActivitiesGroup group, WorkSummary workSummary) {
        if (!workTitleEmpty(workSummary.getTitle())) {
            String title = transformForTitleComparison(workSummary.getTitle().getTitle().getContent());
            List<ActivitiesGroup> groups = potentialGroupingSuggestions.get(title);
            if (groups == null) {
                groups = new ArrayList<>();
            }
            if (!groups.contains(group)) {
                groups.add(group);
            }
            potentialGroupingSuggestions.put(title, groups);
        }
    }

    private String transformForTitleComparison(String titleContent) {
        return titleContent.toLowerCase().replaceAll("\\s", "");
    }

    private boolean workTitleEmpty(WorkTitle workTitle) {
        return workTitle == null || workTitle.getTitle() == null || workTitle.getTitle().getContent() == null || workTitle.getTitle().getContent().isEmpty();
    }

    public List<ActivitiesGroup> getGroups() {
        return groups;
    }

}
