package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.IdentifierTypeDao;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.springframework.transaction.annotation.Transactional;

public class IdentifierTypeDaoImpl extends GenericDaoImpl<IdentifierTypeEntity, Long> implements IdentifierTypeDao {

    public IdentifierTypeDaoImpl() {
        super(IdentifierTypeEntity.class);
    }

    @Override
    @Transactional
    public IdentifierTypeEntity addIdentifierType(IdentifierTypeEntity identifierType) {
        this.persist(identifierType);
        this.flush();
        return identifierType;
    }

    /**
     * Updates id_resolution_prefix, id_validation_regex, id_deprecated
     * 
     * Ignores name (cannot be modified)
     * 
     */
    @Override
    @Transactional
    public IdentifierTypeEntity updateIdentifierType(IdentifierTypeEntity identifierType) {
        IdentifierTypeEntity id = this.find(identifierType.getId());
        id.setResolutionPrefix(identifierType.getResolutionPrefix());
        id.setValidationRegex(identifierType.getValidationRegex());
        id.setIsDeprecated(identifierType.getIsDeprecated());
        id.setIsDeprecated(identifierType.getIsDeprecated());
        id.setIsCaseSensitive(identifierType.getIsCaseSensitive());
        id.setPrimaryUse(identifierType.getPrimaryUse());
        id = this.merge(id);
        this.flush();
        return id;
    }

    @Override
    public IdentifierTypeEntity getEntityByName(String idName) {
        TypedQuery<IdentifierTypeEntity> query = entityManager.createQuery("FROM IdentifierTypeEntity WHERE id_name = :idName", IdentifierTypeEntity.class);
        query.setParameter("idName", idName);
        return query.getSingleResult();
    }

    @Override
    public List<IdentifierTypeEntity> getEntities() {
        TypedQuery<IdentifierTypeEntity> query = entityManager.createQuery("FROM IdentifierTypeEntity order by id_name", IdentifierTypeEntity.class);
        return query.getResultList();
    }

}
