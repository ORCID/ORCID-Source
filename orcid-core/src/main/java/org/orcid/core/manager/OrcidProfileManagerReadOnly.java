package org.orcid.core.manager;

import java.util.Date;

import org.orcid.jaxb.model.message.OrcidProfile;

/**
 * @author Will Simpson
 */
public interface OrcidProfileManagerReadOnly {

    /**
     * Retrieves the orcid external identifiers given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    OrcidProfile retrieveClaimedExternalIdentifiers(String orcid);

    /**
     * Retrieves the orcid bio given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    OrcidProfile retrieveClaimedOrcidBio(String orcid);

    /**
     * Retrieves the orcid affiliations given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the affiliations populated
     */
    OrcidProfile retrieveClaimedAffiliations(String orcid);

    /**
     * Retrieves the orcid fundings given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the funding list populated
     */
    OrcidProfile retrieveClaimedFundings(String orcid);

    /**
     * Retrieves the orcid works given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the works populated
     */
    OrcidProfile retrieveClaimedOrcidWorks(String orcid);

    /**
     * Retrieves the orcid profile given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the full orcid profile
     */
    OrcidProfile retrieveOrcidProfile(String orcid);

    OrcidProfile retrieveOrcidProfile(String orcid, LoadOptions loadOptions);

    OrcidProfile retrieveClaimedOrcidProfile(String orcid);
    
    OrcidProfile retrieveClaimedOrcidProfile(String orcid, LoadOptions loadOptions);
    
    OrcidProfile retrieveFreshOrcidProfile(String orcid, LoadOptions loadOptions);
    
    OrcidProfile retrievePublicOrcidProfile(String orcid);
    
    OrcidProfile retrievePublicOrcidProfile(String orcid, LoadOptions loadOptions);
    
    Date retrieveLastModifiedDate(String orcid);
    
}
