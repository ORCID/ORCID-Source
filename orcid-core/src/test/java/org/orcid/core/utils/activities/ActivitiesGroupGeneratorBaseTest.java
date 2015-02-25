package org.orcid.core.utils.activities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.record.ActivityWithExternalIdentifiers;
import org.orcid.jaxb.model.record.ExternalIdentifier;
import org.orcid.jaxb.model.record.ExternalIdentifiersContainer;

public class ActivitiesGroupGeneratorBaseTest {
    /**
     * Check that a activity belongs to any of the given groups, and, check that all his ext ids also belongs to the group
     * */
    public void checkWorkIsOnGroups(ActivityWithExternalIdentifiers activity, List<ActivitiesGroup> groups) {
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
        checkWorkExternalIdentifiers(activity, group);
    }
    
    /**
     * Check that the given activitys belongs to the same group in a list of given groups
     * */
    public void checkWorksBelongsToTheSameGroup(List<ActivitiesGroup> groups, ActivityWithExternalIdentifiers ... activities) {
        ActivityWithExternalIdentifiers first = activities[0];
        
        assertNotNull(first);
        
        ActivitiesGroup theGroup = getGroupThatContainsWork(groups, first);
        
        assertNotNull(theGroup);
        
        for(ActivityWithExternalIdentifiers activity : activities) {
            assertTrue(theGroup.belongsToGroup(activity));
        }
    }
    
    
    /**
     * Check that the given works belongs to the same group in a list of given groups
     * */
    public void checkWorksDontBelongsToTheSameGroup(List<ActivitiesGroup> groups, ActivityWithExternalIdentifiers ... activities) {                
        for(int i = 0; i < activities.length; i++) {
            ActivityWithExternalIdentifiers a1 = activities[i];
            ActivitiesGroup theGroup = getGroupThatContainsWork(groups, a1);
            for(int j = i+1; j < activities.length; j++){
                assertFalse("activity[" + i + "] and activity["+ j + "] belongs to the same group", theGroup.belongsToGroup(activities[j]));
            }
        }                                
    }        
    
    /**
     * Returns the group that contains the given work
     * */
    public ActivitiesGroup getGroupThatContainsWork(List<ActivitiesGroup> groups, ActivityWithExternalIdentifiers activity) {
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
     * Checks that all the external identifiers in the work are contained in the group external identifiers
     * */
    public void checkWorkExternalIdentifiers(ActivityWithExternalIdentifiers activity, ActivitiesGroup group) {
        ExternalIdentifiersContainer workExtIdsContainer = activity.getExternalIdentifiers();
        List workExtIds = workExtIdsContainer.getExternalIdentifier();
        Set<ExternalIdentifier> groupExtIds = group.getExternalIdentifiers();
        for(Object o : workExtIds) {
            ExternalIdentifier workExtId = (ExternalIdentifier) o;
            assertTrue(groupExtIds.contains(workExtId));
        }
    }
}
