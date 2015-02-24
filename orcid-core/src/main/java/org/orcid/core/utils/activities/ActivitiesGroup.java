package org.orcid.core.utils.activities;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.ExternalIdentifiers;
import org.orcid.jaxb.model.record.ExternalIdentifiersHolder;

public class ActivitiesGroup {
    List<ExternalIdentifiers> externalIdentifiers;
    List<ExternalIdentifiersHolder> activities; 
    
    public void add(ExternalIdentifiersHolder activity) {
        if(externalIdentifiers == null)
            externalIdentifiers = new ArrayList<ExternalIdentifiers>();
        if(activities == null)
            activities = new ArrayList<ExternalIdentifiersHolder>();
        
        ExternalIdentifiers extIds = activity.getExternalIdentifiers(); 
        
        if(extIds == null || extIds.)
        
        
    }
}
