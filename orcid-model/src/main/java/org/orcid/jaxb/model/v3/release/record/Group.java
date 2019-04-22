package org.orcid.jaxb.model.v3.release.record;

import java.util.Collection;

import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;

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
