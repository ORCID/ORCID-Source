package org.orcid.core.utils.v3.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.v3.rc2.record.GroupAble;
import org.orcid.jaxb.model.v3.rc2.record.GroupableActivity;

public class ActivitiesGroupGenerator {    

    protected List<ActivitiesGroup> groups = new ArrayList<ActivitiesGroup>();
    
    private Map<GroupAble, ActivitiesGroup> lookup = new HashMap<GroupAble, ActivitiesGroup>();
    
    public void group(GroupableActivity activity) {
        if(groups.isEmpty()) {
            //If it is the first activity, create a new group for it
            createNewGroup(activity);
        } else {            
            //If it is not the first activity, check which groups it belongs to
            List<ActivitiesGroup> belongsTo = generateBelongsToList(activity);
            
            //If it doesnt belong to any group, create a new group for it
            if(belongsTo.isEmpty()) {
                createNewGroup(activity);
            } else {
                //Get the first group it belongs to
                ActivitiesGroup firstGroup = belongsTo.get(0);
                firstGroup.add(activity);
                
                //If it belongs to other groups, merge them into the first one
                if(belongsTo.size() > 1) {
                    for(int i = 1; i < belongsTo.size(); i++){
                        mergeAndRemoveGroup(firstGroup, belongsTo.get(i));
                    }                        
                }
                updateLookupKeys(firstGroup);
            }
        }
    }
    
    public List<ActivitiesGroup> getGroups() {
        return groups;
    }
    
    protected ActivitiesGroup createNewGroup(GroupableActivity activity) {
        ActivitiesGroup newGroup = new ActivitiesGroup(activity);
        groups.add(newGroup);
        updateLookupKeys(newGroup);
        return newGroup;
    }
    
    protected List<ActivitiesGroup> generateBelongsToList(GroupableActivity activity) {
        List<ActivitiesGroup> belongsTo = new ArrayList<ActivitiesGroup>();
        ActivitiesGroup thisGroup = new ActivitiesGroup(activity);
        for (GroupAble g :thisGroup.getGroupKeys()){
            if (lookup.containsKey(g)) {
                belongsTo.add(lookup.get(g));
            }
        }
        return belongsTo;
    }
    
    protected void mergeAndRemoveGroup(ActivitiesGroup keep, ActivitiesGroup discard) {
        if (keep != discard){
            keep.merge(discard);
            groups.remove(discard);                            
        }
    }
    
    protected void updateLookupKeys(ActivitiesGroup group) {
        for (GroupAble g : group.getGroupKeys()){
            lookup.put(g, group);
        }
    }
}
