package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;

public interface OrgDisambiguatedExternalIdentifierDao extends GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> {

    OrgDisambiguatedExternalIdentifierEntity findByDetails(Long orgDisambiguatedId, String identifier, String identifierType);
    
    boolean exists(Long orgDisambiguatedId, String identifier, String identifierType);    
}
