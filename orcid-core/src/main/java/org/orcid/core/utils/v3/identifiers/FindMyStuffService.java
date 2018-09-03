package org.orcid.core.utils.v3.identifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.v3.identifiers.finders.Finder;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffResult;
import org.springframework.stereotype.Component;

@Component
public class FindMyStuffService {

    @Resource(name = "workManagerReadOnlyV3")
    WorkManagerReadOnly workManagerReadOnly;
    
    @Resource
    List<Finder> finders = new ArrayList<Finder>();
    
    public Map<String,FindMyStuffResult> find(String orcid){
        Map<String,FindMyStuffResult> result = new HashMap<String,FindMyStuffResult>();
        ExternalIDs existingIDs = workManagerReadOnly.getAllExternalIDs(orcid);
        //TODO check if the user has dismissed FindMyStuff
        for (Finder f:finders){
            result.put(f.getServiceName(), f.find(orcid, existingIDs));
        }
        return result;
    }
    
}