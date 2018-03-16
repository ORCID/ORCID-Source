package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;

public interface OtherNameManagerReadOnly {
    OtherNames getOtherNames(String orcid);
    
    OtherNames getPublicOtherNames(String orcid);
    
    OtherNames getMinimizedOtherNames(String orcid);
    
    OtherName getOtherName(String orcid, Long putCode);
}
