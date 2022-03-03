package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;

public interface OrgDisambiguatedExternalIdentifierDao extends GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> {

    OrgDisambiguatedExternalIdentifierEntity findByDetails(Long orgDisambiguatedId, String identifier, String identifierType);
    
    boolean exists(Long orgDisambiguatedId, String identifier, String identifierType);

    List<OrgDisambiguatedExternalIdentifierEntity> findISNIsOfIncorrectLength(int batchSize);

    List<OrgDisambiguatedExternalIdentifierEntity> findByIdentifierIdAndType(String identifier, String identifierType);    
}
