package org.orcid.jaxb.model.v3.rc2.record;

import java.util.Collection;

import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface GroupsContainer {

    Collection<? extends Group> retrieveGroups();

    void setLastModifiedDate(LastModifiedDate lastModifiedDate);

}
