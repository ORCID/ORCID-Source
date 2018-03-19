package org.orcid.jaxb.model.message;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface ActivitiesContainer {

    Map<String, ? extends Activity> retrieveActivitiesAsMap();

    Collection<? extends Activity> retrieveActivities();

}
