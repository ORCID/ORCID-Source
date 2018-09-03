package org.orcid.core.utils.v3.identifiers.finders;

import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffResult;

public interface Finder {

    public FindMyStuffResult find(String orcid,ExternalIDs existingIDs);
    public String getServiceName();
    
}
