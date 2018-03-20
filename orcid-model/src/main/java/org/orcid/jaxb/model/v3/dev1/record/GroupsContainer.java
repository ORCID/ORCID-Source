package org.orcid.jaxb.model.v3.dev1.record;

import java.util.Collection;

import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface GroupsContainer {

    Collection<? extends Group> retrieveGroups();

    void setLastModifiedDate(LastModifiedDate lastModifiedDate);

}
