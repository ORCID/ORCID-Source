package org.orcid.core.utils.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.record_v2.GroupAble;
import org.orcid.jaxb.model.record_v2.GroupableActivity;

public class ActivitiesGroupGenerator {    

    private List<ActivitiesGroup> groups = new ArrayList<ActivitiesGroup>();
    
    private Map<GroupAble, ActivitiesGroup> lookup = new HashMap<GroupAble, ActivitiesGroup>();
    
    public void group(GroupableActivity activity) {
        if(groups.isEmpty()) {
            //If it is the first activity, create a new group for it
            ActivitiesGroup newGroup = new ActivitiesGroup(activity);
            groups.add(newGroup);
            for (GroupAble g :newGroup.getGroupKeys()){
                lookup.put(g, newGroup);
            }
        } else {            
            //If it is not the first activity, check which groups it belongs to
            List<ActivitiesGroup> belongsTo = new ArrayList<ActivitiesGroup>();
            ActivitiesGroup thisGroup = new ActivitiesGroup(activity);
            for (GroupAble g :thisGroup.getGroupKeys()){
                if (lookup.containsKey(g)) {
                    belongsTo.add(lookup.get(g)); 
                }
            }
            
            //If it doesnt belong to any group, create a new group for it
            if(belongsTo.isEmpty()) {
                ActivitiesGroup newGroup = new ActivitiesGroup(activity);
                groups.add(newGroup);
                for (GroupAble g :newGroup.getGroupKeys()){
                    lookup.put(g, newGroup);
                }
            } else {
                //Get the first group it belongs to
                ActivitiesGroup firstGroup = belongsTo.get(0);
                firstGroup.add(activity);
                
                //If it belongs to other groups, merge them into the first one
                if(belongsTo.size() > 1) {
                    for(int i = 1; i < belongsTo.size(); i++){
                        //Merge the group
                        if (firstGroup != belongsTo.get(i)){
                            firstGroup.merge(belongsTo.get(i));
                            //Remove it from the list of groups
                            groups.remove(belongsTo.get(i));             
                        }
                    }                        
                }
                for (GroupAble g :firstGroup.getGroupKeys()){ 
                    lookup.put(g, firstGroup);
                }
            }
        }
    }
    
    public List<ActivitiesGroup> getGroups() {
        return groups;
    }

}
