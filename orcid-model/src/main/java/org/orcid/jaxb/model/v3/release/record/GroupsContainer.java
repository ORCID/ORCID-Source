package org.orcid.jaxb.model.v3.release.record;

import java.util.Collection;

import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface GroupsContainer {

    Collection<? extends Group> retrieveGroups();

    void setLastModifiedDate(LastModifiedDate lastModifiedDate);

}
