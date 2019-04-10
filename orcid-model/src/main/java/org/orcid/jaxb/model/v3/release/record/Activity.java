package org.orcid.jaxb.model.v3.release.record;

import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.Filterable;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;

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
