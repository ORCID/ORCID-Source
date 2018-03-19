package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface SourceManager {

    String retrieveSourceOrcid();
    
    SourceEntity retrieveSourceEntity();

    boolean isInDelegationMode();
    
    ProfileEntity retrieveSourceProfileEntity();

    String retrieveRealUserOrcid();

}
