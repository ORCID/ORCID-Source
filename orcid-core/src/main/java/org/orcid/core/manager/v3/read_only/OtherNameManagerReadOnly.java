package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;

public interface OtherNameManagerReadOnly {
    OtherNames getOtherNames(String orcid);
    
    OtherNames getPublicOtherNames(String orcid);
    
    OtherNames getMinimizedOtherNames(String orcid);
    
    OtherName getOtherName(String orcid, Long putCode);
}
