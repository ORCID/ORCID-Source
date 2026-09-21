package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.springframework.transaction.annotation.Transactional;

public interface OrgDisambiguatedExternalIdentifierDao extends GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> {

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    OrgDisambiguatedExternalIdentifierEntity findByDetails(Long orgDisambiguatedId, String identifier, String identifierType);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean exists(Long orgDisambiguatedId, String identifier, String identifierType);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgDisambiguatedExternalIdentifierEntity> findISNIsOfIncorrectLength(int batchSize);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgDisambiguatedExternalIdentifierEntity> findByIdentifierIdAndType(String identifier, String identifierType);    
}
