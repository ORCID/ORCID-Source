package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IdentifierTypeDao extends GenericDao<IdentifierTypeEntity, Long>{

    @Transactional(propagation = Propagation.REQUIRED)
    public IdentifierTypeEntity addIdentifierType(IdentifierTypeEntity identifierType);

    @Transactional(propagation = Propagation.REQUIRED)
    public IdentifierTypeEntity updateIdentifierType(IdentifierTypeEntity identifierType);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public IdentifierTypeEntity getEntityByName(String idName);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public List<IdentifierTypeEntity> getEntities();
    
}
