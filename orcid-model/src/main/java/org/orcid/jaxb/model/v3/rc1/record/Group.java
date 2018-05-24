package org.orcid.jaxb.model.v3.rc1.record;

import java.util.Collection;

import org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate;

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
