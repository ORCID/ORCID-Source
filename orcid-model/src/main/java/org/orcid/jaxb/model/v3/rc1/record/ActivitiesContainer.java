package org.orcid.jaxb.model.v3.rc1.record;

import java.util.Collection;
import java.util.Map;

import org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface ActivitiesContainer {

    Map<Long, ? extends Activity> retrieveActivitiesAsMap();

    Collection<? extends Activity> retrieveActivities();

    void setLastModifiedDate(LastModifiedDate lastModifiedDate);
}
