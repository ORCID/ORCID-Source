package org.orcid.jaxb.model.record_rc1;

import java.util.Collection;

/**
 * 
 * @author Will Simpson
 *
 */
public interface Group {

    Collection<? extends GroupableActivity> getActivities();
    
}
