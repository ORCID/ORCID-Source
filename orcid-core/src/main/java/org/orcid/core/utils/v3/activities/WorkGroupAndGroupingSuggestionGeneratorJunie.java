package org.orcid.core.utils.v3.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.GroupableActivity;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.pojo.WorkSummaryExtended;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class WorkGroupAndGroupingSuggestionGeneratorJunie extends ActivitiesGroupGenerator {

    // Optimized mapping: group -> set of titles it is associated with
    private Map<ActivitiesGroup, Set<String>> groupToTitles = new HashMap<>();
    // Optimized mapping: title -> set of groups associated with it
    private Map<String, Set<ActivitiesGroup>> titleToGroups = new HashMap<>();

    @Override
    public void group(GroupableActivity activity) {
        if (!(activity instanceof WorkSummary || activity instanceof WorkSummaryExtended)) {
            throw new IllegalArgumentException("Argument must be of type WorkSummary");
        }

        WorkSummary workSummary = null;
        if (activity instanceof WorkSummaryExtended) {
            workSummary = (WorkSummaryExtended) activity;
        } else if (activity instanceof WorkSummary) {
            workSummary = (WorkSummary) activity;
        }

        ActivitiesGroup targetGroup;
        if (groups.isEmpty()) {
            targetGroup = createNewGroup(activity);
        } else {
            List<ActivitiesGroup> belongsTo = generateBelongsToList(activity);

            if (belongsTo.isEmpty()) {
                targetGroup = createNewGroup(activity);
            } else {
                targetGroup = belongsTo.get(0);
                targetGroup.add(activity);

                if (belongsTo.size() > 1) {
                    for (int i = 1; i < belongsTo.size(); i++) {
                        ActivitiesGroup groupToMerge = belongsTo.get(i);
                        mergeAndRemoveGroup(targetGroup, groupToMerge);
                        fastSwitchGroup(groupToMerge, targetGroup);
                    }
                }
                updateLookupKeys(targetGroup);
            }
        }
        mapGroupToTitle(targetGroup, workSummary);
    }

    public List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid) {
        List<WorkGroupingSuggestion> suggestions = new ArrayList<>();
        for (Map.Entry<String, Set<ActivitiesGroup>> entry : titleToGroups.entrySet()) {
            Set<ActivitiesGroup> groupsForTitle = entry.getValue();
            if (groupsForTitle.size() > 1) {
                WorkGroupingSuggestion suggestion = new WorkGroupingSuggestion();
                suggestion.setOrcid(orcid);
                List<Long> putCodes = new ArrayList<>();

                boolean groupableExternalIdFound = false;
                for (ActivitiesGroup group : groupsForTitle) {
                    for (GroupableActivity activity : group.getActivities()) {
                        WorkSummary workSummary = (WorkSummary) activity;
                        putCodes.add(workSummary.getPutCode());
                        if (!groupableExternalIdFound && workSummary.getExternalIdentifiers() != null) {
                            for (ExternalID externalId : workSummary.getExternalIdentifiers().getExternalIdentifier()) {
                                if (externalId.isGroupAble()) {
                                    groupableExternalIdFound = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (groupableExternalIdFound) {
                    suggestion.setPutCodes(putCodes);
                    suggestions.add(suggestion);
                }
            }
        }
        return suggestions;
    }

    private void fastSwitchGroup(ActivitiesGroup oldGroup, ActivitiesGroup newGroup) {
        Set<String> titles = groupToTitles.remove(oldGroup);
        if (titles != null) {
            Set<String> newGroupTitles = groupToTitles.computeIfAbsent(newGroup, k -> new HashSet<>());
            for (String title : titles) {
                Set<ActivitiesGroup> groupsForTitle = titleToGroups.get(title);
                if (groupsForTitle != null) {
                    groupsForTitle.remove(oldGroup);
                    groupsForTitle.add(newGroup);
                }
                newGroupTitles.add(title);
            }
        }
    }

    private void mapGroupToTitle(ActivitiesGroup group, WorkSummary workSummary) {
        if (!workTitleEmpty(workSummary.getTitle())) {
            String title = transformForTitleComparison(workSummary.getTitle().getTitle().getContent());
            
            titleToGroups.computeIfAbsent(title, k -> new HashSet<>()).add(group);
            groupToTitles.computeIfAbsent(group, k -> new HashSet<>()).add(title);
        }
    }

    private String transformForTitleComparison(String titleContent) {
        return titleContent.toLowerCase().replaceAll("\\s", "");
    }

    private boolean workTitleEmpty(WorkTitle workTitle) {
        return workTitle == null || workTitle.getTitle() == null || workTitle.getTitle().getContent() == null || workTitle.getTitle().getContent().isEmpty();
    }
    
    @Override
    protected void mergeAndRemoveGroup(ActivitiesGroup keep, ActivitiesGroup discard) {
        super.mergeAndRemoveGroup(keep, discard);
        // We don't remove from titleToGroups/groupToTitles here because fastSwitchGroup handles it
        // and it's called immediately after in group()
    }
}
