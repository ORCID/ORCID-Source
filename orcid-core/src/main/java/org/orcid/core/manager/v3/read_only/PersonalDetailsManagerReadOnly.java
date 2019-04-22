package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.release.record.PersonalDetails;

/**
* 
* @author Angel Montenegro
* 
*/
public interface PersonalDetailsManagerReadOnly extends ManagerReadOnlyBase {
    PersonalDetails getPersonalDetails(String orcid);

    PersonalDetails getPublicPersonalDetails(String orcid);
}
