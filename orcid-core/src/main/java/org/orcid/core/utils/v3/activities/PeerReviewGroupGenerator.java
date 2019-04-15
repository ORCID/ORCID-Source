package org.orcid.core.utils.v3.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;

public class PeerReviewGroupGenerator {    

    private List<PeerReviewGroup> groups = new ArrayList<PeerReviewGroup>();
    
    private Map<PeerReviewGroupKey, PeerReviewGroup> lookup = new HashMap<PeerReviewGroupKey, PeerReviewGroup>();
    
    
    public void group(PeerReviewSummary summary) {
        if(groups.isEmpty()) {
            PeerReviewGroup newGroup = new PeerReviewGroup(summary);
            groups.add(newGroup);
            for (PeerReviewGroupKey key :newGroup.getGroupKeys()){
                lookup.put(key, newGroup);
            }
        } else {            
            //If it is not the first activity, check which groups it belongs to
            List<PeerReviewGroup> belongsTo = new ArrayList<PeerReviewGroup>();
            PeerReviewGroup thisGroup = new PeerReviewGroup(summary);
            for (PeerReviewGroupKey g :thisGroup.getGroupKeys()){
                if (lookup.containsKey(g))
                    belongsTo.add(lookup.get(g));
            }
            
            //If it doesnt belong to any group, create a new group for it
            if(belongsTo.isEmpty()) {
                PeerReviewGroup newGroup = new PeerReviewGroup(summary);
                groups.add(newGroup);
                for (PeerReviewGroupKey g :newGroup.getGroupKeys()){
                    lookup.put(g, newGroup);
                }
            } else {
                //Get the first group it belongs to
                PeerReviewGroup firstGroup = belongsTo.get(0);
                firstGroup.add(summary);
                
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
                for (PeerReviewGroupKey g :thisGroup.getGroupKeys()){
                    lookup.put(g, firstGroup);
                }
            }
        }
    }
    
    public List<PeerReviewGroup> getGroups() {
        return groups;
    }
}
