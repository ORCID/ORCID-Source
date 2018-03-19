package org.orcid.jaxb.model.record_rc3;

import org.orcid.jaxb.model.common_rc3.CreatedDate;
import org.orcid.jaxb.model.common_rc3.Filterable;
import org.orcid.jaxb.model.common_rc3.LastModifiedDate;

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
