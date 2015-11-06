/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.utils.activities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.record.summary_rc1.PeerReviewGroupKey;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary;
import org.orcid.jaxb.model.record_rc1.ExternalIdentifiersContainer;
import org.orcid.jaxb.model.record_rc1.GroupKey;
import org.orcid.jaxb.model.record_rc1.GroupableActivity;

public class ActivitiesGroup {
    private Set<GroupKey> groupKeys;
    private Set<GroupableActivity> activities;         
    
    public ActivitiesGroup(GroupableActivity activity) {        
        groupKeys = new HashSet<GroupKey>();        
        activities = new HashSet<GroupableActivity>();
        
        if(activity != null) {
            if(PeerReviewSummary.class.isAssignableFrom(activity.getClass())) {
                PeerReviewSummary peerReviewSummary = (PeerReviewSummary) activity;
                PeerReviewGroupKey prgk = new PeerReviewGroupKey(); 
                prgk.setGroupId(peerReviewSummary.getGroupId());                
                groupKeys.add(prgk);                               
            } else {
                ExternalIdentifiersContainer container = activity.getExternalIdentifiers();
                if(container != null) {
                    List<? extends GroupKey> extIds = (List<? extends GroupKey>)container.getExternalIdentifier();
                    for(GroupKey extId : extIds) {
                        //Dont add grouping keys  that dont pass the validation
                        if(extId.passGroupingValidation())
                            groupKeys.add(extId);
                    }
                }
            }            
        }
        
        activities.add(activity);
    }
            
    public Set<GroupKey> getGroupKeys() {
        if(groupKeys == null)
            groupKeys = new HashSet<GroupKey>();
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
                List<? extends GroupKey> extIds = (List<? extends GroupKey>)container.getExternalIdentifier();
                for(GroupKey extId : extIds) {
                    //Dont add grouping keys  that dont pass the grouping validation
                    if(extId.passGroupingValidation())
                        if(!groupKeys.contains(extId))
                            groupKeys.add(extId);
                }
            }
        }
        
        //Add activity
        activities.add(activity);
    }
    
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
                    for(GroupKey extId : activity.getExternalIdentifiers().getExternalIdentifier()) {
                        if(extId.passGroupingValidation())
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
            if(prgk.passGroupingValidation()) {
                if(groupKeys.contains(prgk)) {
                    return true;
                }
            }
        } else {
            //Check existing keys
            ExternalIdentifiersContainer container = activity.getExternalIdentifiers();
            if(container != null) {
                List<? extends GroupKey> extIds = (List<? extends GroupKey>)container.getExternalIdentifier();
                for(GroupKey extId : extIds) {
                    //First check keys restrictions
                    if(extId.passGroupingValidation()) {
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
        Set<GroupKey> otherKeys = group.getGroupKeys();
        
        //The incoming groups should always contain at least one key, we should not merge activities without keys
        if(otherKeys.isEmpty()) 
            throw new IllegalArgumentException("Unable to merge a group without external identifiers");
        
        //The incoming group should always contains at least one activity, we should not merge empty activities        
        //Merge group keys
        for(GroupKey otherKey: otherKeys) {
            if(!groupKeys.contains(otherKey))
                groupKeys.add(otherKey);
        }
        
        //Merge activities
        for(GroupableActivity activity : otherActivities) {
            //We assume the activity is not already there, anyway it is a set
            activities.add(activity);
        }
    }
    
    private boolean containsKey(GroupKey key) {
        for(GroupKey existingKey : groupKeys) {
            if(existingKey.matches(key)) {
                return true;
            }
        }
        return false;
    }
    
}
