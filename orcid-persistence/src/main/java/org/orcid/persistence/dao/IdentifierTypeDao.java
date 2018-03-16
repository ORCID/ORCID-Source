package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;

public interface IdentifierTypeDao extends GenericDao<IdentifierTypeEntity, Long>{

    public IdentifierTypeEntity addIdentifierType(IdentifierTypeEntity identifierType);

    public IdentifierTypeEntity updateIdentifierType(IdentifierTypeEntity identifierType);
    
    public IdentifierTypeEntity getEntityByName(String idName);

    public List<IdentifierTypeEntity> getEntities();
    
}
