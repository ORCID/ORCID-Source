package org.orcid.jaxb.model.record_v2;

import java.util.Collection;

import org.orcid.jaxb.model.common_v2.LastModifiedDate;

/**
 * 
 * @author Will Simpson
 *
 */
public interface Group {

    Collection<? extends GroupableActivity> getActivities();

    void setLastModifiedDate(LastModifiedDate lastModifiedDate);
    
    LastModifiedDate getLastModifiedDate();
    
    ExternalIDs getIdentifiers();
}
