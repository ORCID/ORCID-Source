package org.orcid.core.manager;

import org.orcid.jaxb.model.message.OrcidProfile;

/**
 * Class to persist OrcidProfile objects to an index for searching in future.
 * This is used in collaboration with the OrcidProfileManager so that when Orcid
 * Profile information is persisted, more indexing terms are added. The
 * OrcidSearchManager can query the index based on criteria added at this stage.
 * 
 * 
 * @author jamesb
 * @See OrcidProfile
 * @See OrcidSearchManager
 * @See OrcidProfileManager
 * 
 */
public interface OrcidIndexManager {

    void persistProfileInformationForIndexingIfNecessary(OrcidProfile orcidProfile);
    
    void persistProfileInformationForIndexing(OrcidProfile orcidProfile);

    void deleteOrcidProfile(OrcidProfile orcidProfile);

    void deleteOrcidProfile(String orcid);

}
