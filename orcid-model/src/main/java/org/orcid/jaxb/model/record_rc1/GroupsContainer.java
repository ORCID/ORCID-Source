package org.orcid.jaxb.model.record_rc1;

import java.util.Collection;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface GroupsContainer {

    Collection<? extends Group> retrieveGroups();

}
