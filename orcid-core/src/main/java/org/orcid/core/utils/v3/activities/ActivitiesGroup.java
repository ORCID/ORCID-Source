package org.orcid.core.utils.v3.activities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.v3.dev1.record.summary.PeerReviewGroupKey;
import org.orcid.jaxb.model.v3.dev1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.dev1.record.ExternalIdentifiersContainer;
import org.orcid.jaxb.model.v3.dev1.record.GroupAble;
import org.orcid.jaxb.model.v3.dev1.record.GroupableActivity;

public class ActivitiesGroup {
    private Set<GroupAble> groupKeys;
    private Set<GroupableActivity> activities;         
    
    public ActivitiesGroup(GroupableActivity activity) {        
        groupKeys = new HashSet<GroupAble>();        
        activities = new HashSet<GroupableActivity>();
        
        if(activity != null)
            if(PeerReviewSummary.class.isAssignableFrom(activity.getClass())) {
                PeerReviewSummary peerReviewSummary = (PeerReviewSummary) activity;
                PeerReviewGroupKey prgk = new PeerReviewGroupKey(); 
                prgk.setGroupId(peerReviewSummary.getGroupId());                
                groupKeys.add(prgk);                               
            } else if (activity.getExternalIdentifiers() != null)
                for (GroupAble extId : activity.getExternalIdentifiers().getExternalIdentifier())
                    // Dont add grouping keys that dont pass the validation
                    if (extId.isGroupAble())
                        groupKeys.add(extId);
        activities.add(activity);
    }
            
    public Set<GroupAble> getGroupKeys() {
        if(groupKeys == null)
            groupKeys = new HashSet<GroupAble>();
        return groupKeys;
    }

    public Set<GroupableActivity> getActivities() {
        if(activities == null)
            activities = new HashSet<GroupableActivity>();
        return activities;
    }

    public void add(GroupableActivity activity) {    
        if(PeerReviewSummary.class.isAssignableFrom(activity.getClass())) {
            //For peer review there is only one grouping key, so we dont need to add more keys to the groupKeys set
        }  else {
            //Add new grouping keys
            ExternalIdentifiersContainer container = activity.getExternalIdentifiers();
            if(container != null) {
                List<? extends GroupAble> extIds = (List<? extends GroupAble>)container.getExternalIdentifier();
                for(GroupAble extId : extIds) {
                    // Dont add grouping keys that dont pass the grouping
                    // validation
                    if (extId.isGroupAble()) {
                        boolean hasId = false;
                        for (GroupAble groupKey : groupKeys)
                            if (groupKey.getGroupId() != null && groupKey.getGroupId().equals(extId.getGroupId()))
                                hasId = true;
                        if (!hasId)
                            groupKeys.add(extId);
                    }
                }
            }
        }
        
        //Add activity
        activities.add(activity);
    }
    
    @Deprecated
    /** This method is only used by tests to confirm accuracy of ActivitiesGroupGenerator and should not be used in production
     * 
     * @param activity
     * @return
     */
    public boolean belongsToGroup(GroupableActivity activity) {
        boolean isPeerReview = PeerReviewSummary.class.isAssignableFrom(activity.getClass());
        //If there are no grouping keys
        if(groupKeys == null || groupKeys.isEmpty()) {            
            if(isPeerReview) {
                return false;
            } else {
                if(activity.getExternalIdentifiers() == null || activity.getExternalIdentifiers().getExternalIdentifier() == null || activity.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {            
                    //Check if the activity dont have grouping keys
                    //If the activity doesn't have any external identifier, check if the activity is in the group
                    if(activities.contains(activity))
                        return true;
                    else 
                        return false;                            
                } else {
                    //If any of the activities pass the grouping validation, the activity must belong to other group
                    for(GroupAble extId : activity.getExternalIdentifiers().getExternalIdentifier()) {
                        if(extId.isGroupAble())
                            return false;
                    }
                    
                    //If none of the activities pass the groupings validation, so, lets check if the group actually contains the activity
                    if(activities.contains(activity))
                        return true;
                    else 
                        return false;                
                }
            }
        }                        
        
        if(isPeerReview) {
            PeerReviewSummary peerReviewSummary = (PeerReviewSummary) activity;
            PeerReviewGroupKey prgk = new PeerReviewGroupKey(); 
            prgk.setGroupId(peerReviewSummary.getGroupId());  
            if(prgk.isGroupAble()) {
                if(groupKeys.contains(prgk)) {
                    return true;
                }
            }
        } else {
            //Check existing keys
            ExternalIdentifiersContainer container = activity.getExternalIdentifiers();
            if(container != null) {
                List<? extends GroupAble> extIds = (List<? extends GroupAble>)container.getExternalIdentifier();
                for(GroupAble extId : extIds) {
                    //First check keys restrictions
                    if(extId.isGroupAble()) {
                        //If any of the keys already exists on this group, return true
                        if(containsKey(extId))
                            return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public void merge(ActivitiesGroup group) {
        Set<GroupableActivity> otherActivities = group.getActivities();
        Set<GroupAble> otherKeys = group.getGroupKeys();
        
        //The incoming groups should always contain at least one key, we should not merge activities without keys
        if(otherKeys.isEmpty()) 
            throw new IllegalArgumentException("Unable to merge a group without external identifiers");
        
        //The incoming group should always contains at least one activity, we should not merge empty activities        
        //Merge group keys
        for(GroupAble otherKey: otherKeys) {
            if(!groupKeys.contains(otherKey))
                groupKeys.add(otherKey);
        }
        
        //Merge activities
        for(GroupableActivity activity : otherActivities) {
            //We assume the activity is not already there, anyway it is a set
            activities.add(activity);
        }
    }
    
    private boolean containsKey(GroupAble key) {
        for (GroupAble existingKey : groupKeys)
            if (existingKey.getGroupId() != null)
                if (existingKey.getGroupId().equals(key.getGroupId()))
                    return true;
        return false;
    }
    
}
