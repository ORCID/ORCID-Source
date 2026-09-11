package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.springframework.transaction.annotation.Transactional;

public interface OrgDisambiguatedExternalIdentifierDao extends GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> {

    @Transactional(readOnly = true)
    OrgDisambiguatedExternalIdentifierEntity findByDetails(Long orgDisambiguatedId, String identifier, String identifierType);
    
    @Transactional(readOnly = true)
    boolean exists(Long orgDisambiguatedId, String identifier, String identifierType);

    @Transactional(readOnly = true)
    List<OrgDisambiguatedExternalIdentifierEntity> findISNIsOfIncorrectLength(int batchSize);

    @Transactional(readOnly = true)
    List<OrgDisambiguatedExternalIdentifierEntity> findByIdentifierIdAndType(String identifier, String identifierType);    
}
