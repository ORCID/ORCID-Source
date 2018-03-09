package org.orcid.core.manager.v3;

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
    
    boolean isDelegatedByAnAdmin();

    ProfileEntity retrieveSourceProfileEntity();

    String retrieveRealUserOrcid();

}
