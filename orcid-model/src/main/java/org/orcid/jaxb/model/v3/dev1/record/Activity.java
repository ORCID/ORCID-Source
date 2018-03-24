package org.orcid.jaxb.model.v3.dev1.record;

import org.orcid.jaxb.model.v3.dev1.common.CreatedDate;
import org.orcid.jaxb.model.v3.dev1.common.Filterable;
import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface Activity extends Filterable {

    Long getPutCode();
    
    void setPutCode(Long putCode);
    
    String getPath();
    
    void setPath(String path);
    
    CreatedDate getCreatedDate();
    
    void setCreatedDate(CreatedDate value);
    
    LastModifiedDate getLastModifiedDate();
    
    void setLastModifiedDate(LastModifiedDate value);
        
}
