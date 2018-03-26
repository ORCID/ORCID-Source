package org.orcid.jaxb.model.message;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface Activity extends VisibilityType {

    String retrieveSourcePath();

    String getPutCode();
    
    CreatedDate getCreatedDate();
    
    void setCreatedDate(CreatedDate value);
    
    LastModifiedDate getLastModifiedDate();
    
    void setLastModifiedDate(LastModifiedDate value);

}
