package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers;

public interface ExternalIdentifierManagerReadOnly {
    
    /**
     * Return the list of public external identifiers associated to a specific
     * profile
     * 
     * @param orcid
     * @return the list of public external identifiers associated with the orcid
     *         profile
     */
    PersonExternalIdentifiers getPublicExternalIdentifiers(String orcid);

    /**
     * Return the list of external identifiers associated to a specific profile
     * 
     * @param orcid
     * @return the list of external identifiers associated with the orcid
     *         profile
     */
    PersonExternalIdentifiers getExternalIdentifiers(String orcid);

    /**
     * Retrieve a external identifier from database
     * 
     * @param id
     * @return the externalIdentifierEntity associated with the parameter id
     */
    PersonExternalIdentifier getExternalIdentifier(String orcid, Long id);
}
