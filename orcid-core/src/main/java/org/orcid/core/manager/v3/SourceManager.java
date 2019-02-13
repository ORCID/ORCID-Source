package org.orcid.core.manager.v3;

import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface SourceManager {

    String retrieveActiveSourceId();
    
    SourceEntity retrieveActiveSourceEntity();

    boolean isInDelegationMode();
    
    boolean isDelegatedByAnAdmin();

    ProfileEntity retrieveSourceProfileEntity();

    String retrieveRealUserOrcid();

    Source retrieveActiveSource();

}
