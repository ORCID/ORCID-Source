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

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.record.GroupableActivity;

public class ActivitiesGroupGenerator {    

    private List<ActivitiesGroup> groups = new ArrayList<ActivitiesGroup>();
    
    
    public void group(GroupableActivity activity) {
        if(groups.isEmpty()) {
            //If it is the first activity, create a new group for it
            ActivitiesGroup newGroup = new ActivitiesGroup(activity);
            groups.add(newGroup);
        } else {            
            //If it is not the first activity, check which groups it belongs to
            List<Integer> belongsTo = new ArrayList<Integer>();
            for(int i = 0; i < groups.size(); i++) {
                ActivitiesGroup group = groups.get(i);
                if(group.belongsToGroup(activity)) {
                    belongsTo.add(i);
                }
            }
            
            //If it doesnt belong to any group, create a new group for it
            if(belongsTo.isEmpty()) {
                ActivitiesGroup newGroup = new ActivitiesGroup(activity);
                groups.add(newGroup);
            } else {
                //Get the first group it belongs to
                ActivitiesGroup firstGroup = groups.get(belongsTo.get(0));
                firstGroup.add(activity);
                
                //If it belongs to other groups, merge them into the first one
                if(belongsTo.size() > 1) {
                    for(int i = 1; i < belongsTo.size(); i++){
                        //Merge the group
                        firstGroup.merge(groups.get(i));
                        //Remove it from the list of groups
                        groups.remove(i);
                    }                        
                }
            }
        }
    }
    
    public List<ActivitiesGroup> getGroups() {
        return groups;
    }
}
