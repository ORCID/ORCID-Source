package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.PersonalDetails;

/**
* 
* @author Angel Montenegro
* 
*/
public interface PersonalDetailsManagerReadOnly extends ManagerReadOnlyBase {
    PersonalDetails getPersonalDetails(String orcid);

    PersonalDetails getPublicPersonalDetails(String orcid);
}
