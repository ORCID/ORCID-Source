package org.orcid.core.utils.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.record_v2.GroupAble;
import org.orcid.jaxb.model.record_v2.GroupableActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivitiesGroupGenerator {    

    private List<ActivitiesGroup> groups = new ArrayList<ActivitiesGroup>();
    
    private Map<GroupAble, ActivitiesGroup> lookup = new HashMap<GroupAble, ActivitiesGroup>();
    
    private static final Logger LOG = LoggerFactory.getLogger(ActivitiesGroupGenerator.class);
    
    public void group(GroupableActivity activity) {
        groupDebug("Examining activity " + activity.toString());
        if(groups.isEmpty()) {
            //If it is the first activity, create a new group for it
            groupDebug("Creating first group with activity");
            ActivitiesGroup newGroup = new ActivitiesGroup(activity);
            groupDebug("Created new group " + newGroup);
            groups.add(newGroup);
            for (GroupAble g :newGroup.getGroupKeys()){
                groupDebug("Associating key " + g.getGroupId() + " groupable with group " + newGroup);
                lookup.put(g, newGroup);
            }
        } else {            
            //If it is not the first activity, check which groups it belongs to
            groupDebug("Finding groups that activity belongs to");
            List<ActivitiesGroup> belongsTo = new ArrayList<ActivitiesGroup>();
            ActivitiesGroup thisGroup = new ActivitiesGroup(activity);
            for (GroupAble g :thisGroup.getGroupKeys()){
                if (lookup.containsKey(g)) {
                    groupDebug("Found group that activity belings to");
                    belongsTo.add(lookup.get(g)); 
                }
            }
            
            //If it doesnt belong to any group, create a new group for it
            if(belongsTo.isEmpty()) {
                groupDebug("Activity doesn't belong to any group, creating new one");
                ActivitiesGroup newGroup = new ActivitiesGroup(activity);
                groupDebug("Created new group " + newGroup);
                groups.add(newGroup);
                for (GroupAble g :newGroup.getGroupKeys()){
                    groupDebug("Associating key " + g.getGroupId() + " groupable with group");
                    lookup.put(g, newGroup);
                }
            } else {
                //Get the first group it belongs to
                groupDebug("Getting first group that activity belongs to");
                ActivitiesGroup firstGroup = belongsTo.get(0);
                groupDebug("Adding activity to group");
                firstGroup.add(activity);
                
                //If it belongs to other groups, merge them into the first one
                if(belongsTo.size() > 1) {
                    groupDebug("ACtivity belongs to more than one group");
                    for(int i = 1; i < belongsTo.size(); i++){
                        //Merge the group
                        if (firstGroup != belongsTo.get(i)){
                            groupDebug("Merging group " + belongsTo.get(i) + " to group " + firstGroup);
                            firstGroup.merge(belongsTo.get(i));
                            //Remove it from the list of groups
                            groupDebug("Removing group " + belongsTo.get(i) + " from list of groups");
                            groups.remove(belongsTo.get(i));                            
                        }
                    }                        
                }
                for (GroupAble g :thisGroup.getGroupKeys()){
                    groupDebug("Associating key " + g.getGroupId() + " groupable with group " + firstGroup);
                    lookup.put(g, firstGroup);
                }
            }
        }
        
        //TODO: make sure this orders correctly
        //TODO: look at v1.2 post/put work....
    }
    
    public List<ActivitiesGroup> getGroups() {
        return groups;
    }
    

    private void groupDebug(String string) {
        if (Features.WORK_GROUP_LOGGING.isActive()) {
            LOG.info("### ACTIVITIESGROUPGENERATOR GROUP LOGGING: " + string);
        }
    }
}
