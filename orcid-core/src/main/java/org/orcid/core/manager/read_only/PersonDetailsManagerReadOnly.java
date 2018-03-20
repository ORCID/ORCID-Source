package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.Person;

public interface PersonDetailsManagerReadOnly {
    Person getPersonDetails(String orcid);
    
    Person getPublicPersonDetails(String orcid);
}
