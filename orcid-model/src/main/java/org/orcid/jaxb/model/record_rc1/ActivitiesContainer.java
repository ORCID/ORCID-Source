package org.orcid.jaxb.model.record_rc1;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface ActivitiesContainer {

    Map<Long, ? extends Activity> retrieveActivitiesAsMap();

    Collection<? extends Activity> retrieveActivities();

}
