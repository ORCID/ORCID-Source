package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;

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
