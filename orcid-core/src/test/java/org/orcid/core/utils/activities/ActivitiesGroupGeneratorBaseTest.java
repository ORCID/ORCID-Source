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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.record_rc4.ExternalIdentifiersContainer;
import org.orcid.jaxb.model.record_rc4.GroupAble;
import org.orcid.jaxb.model.record_rc4.GroupableActivity;

public class ActivitiesGroupGeneratorBaseTest {
    /**
     * Check that a activity belongs to any of the given groups, and, check that all his ext ids also belongs to the group
     * */
    public void checkActivityIsOnGroups(GroupableActivity activity, List<ActivitiesGroup> groups) {
        int groupIndex = -1;
        for(int i = 0; i < groups.size(); i++) {
            ActivitiesGroup group = groups.get(i);
            assertNotNull(group.getActivities());
            if(group.getActivities().contains(activity)) {
                groupIndex = i;
                break;
            }
        }
        
        //Check the activity belongs to a group
        assertFalse("Work doesnt belong to any group", -1 == groupIndex);
        ActivitiesGroup group = groups.get(groupIndex);
        //Check the external ids are contained in the group ext ids
        checkExternalIdentifiers(activity, group);
    }
    
    /**
     * Check that the given activitys belongs to the same group in a list of given groups
     * */
    public void checkActivitiesBelongsToTheSameGroup(List<ActivitiesGroup> groups, GroupableActivity ... activities) {
        GroupableActivity first = activities[0];
        
        assertNotNull(first);
        
        ActivitiesGroup theGroup = getGroupThatContainsActivity(groups, first);
        
        assertNotNull(theGroup);
        
        for(GroupableActivity activity : activities) {
            assertTrue(theGroup.belongsToGroup(activity));
        }
    }
    
    
    /**
     * Check that the given activities belongs to the same group in a list of given groups
     * */
    public void checkActivitiesDontBelongsToTheSameGroup(List<ActivitiesGroup> groups, GroupableActivity ... activities) {                
        for(int i = 0; i < activities.length; i++) {
            GroupableActivity a1 = activities[i];
            ActivitiesGroup theGroup = getGroupThatContainsActivity(groups, a1);
            for(int j = i+1; j < activities.length; j++){
                assertFalse("activity[" + i + "] and activity["+ j + "] belongs to the same group", theGroup.belongsToGroup(activities[j]));
            }
        }                                
    }        
    
    /**
     * Returns the group that contains the given activity
     * */
    public ActivitiesGroup getGroupThatContainsActivity(List<ActivitiesGroup> groups, GroupableActivity activity) {
        ActivitiesGroup theGroup = null;
        for(ActivitiesGroup group : groups) {
            if(group.belongsToGroup(activity)) {
                theGroup = group;
                break;
            }
        }
        return theGroup;
    }
    
    /**
     * Checks that all the external identifiers in the activity are contained in the group external identifiers
     * */
    public void checkExternalIdentifiers(GroupableActivity activity, ActivitiesGroup group) {
        ExternalIdentifiersContainer extIdsContainer = activity.getExternalIdentifiers();
        List<? extends GroupAble> extIds = extIdsContainer.getExternalIdentifier();
        Set<GroupAble> groupExtIds = group.getGroupKeys();
        for(Object o : extIds) {
            GroupAble extId = (GroupAble) o;
            //If the ext id pass the grouping validation, it must be in the ext ids list
            if(extId.isGroupAble())
                assertTrue(groupExtIds.contains(extId));
        }
    }
}
