package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ExternalIdentifierDaoImpl extends GenericDaoImpl<ExternalIdentifierEntity, Long> implements ExternalIdentifierDao {

    private static final String PUBLIC_VISIBILITY = "PUBLIC";
    
    public ExternalIdentifierDaoImpl() {
        super(ExternalIdentifierEntity.class);
    }

    /**
     * Removes an external identifier from database based on his ID. The ID for
     * external identifiers consists of the "orcid" of the owner and the
     * "externalIdReference" which is an identifier of the external id.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     */
    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean removeExternalIdentifier(String orcid, String externalIdReference) {
        Query query = entityManager.createQuery("delete from ExternalIdentifierEntity where orcid=:orcid and externalIdReference=:externalIdReference");
        query.setParameter("orcid", orcid);
        query.setParameter("externalIdReference", externalIdReference);
        return query.executeUpdate() > 0 ? true : false;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Cacheable(value = "dao-external-identifiers", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM ExternalIdentifierEntity WHERE orcid = :orcid order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @Override
    @Cacheable(value = "public-external-identifiers", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ExternalIdentifierEntity> getPublicExternalIdentifiers(String orcid, long lastModified) {
        return getExternalIdentifiers(orcid, PUBLIC_VISIBILITY);
    }

    @Override
    @Transactional
    public boolean updateVisibility(String orcid, Visibility visibility) {
        Query query = entityManager.createNativeQuery("UPDATE external_identifier SET visibility = :visibility WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility.name());
        return query.executeUpdate() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid, String visibility) {
        Query query = entityManager.createQuery("FROM ExternalIdentifierEntity WHERE orcid = :orcid and visibility = :visibility order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility);
        return query.getResultList();
    }

    @Override
    public ExternalIdentifierEntity getExternalIdentifierEntity(String orcid, Long id) {
        Query query = entityManager.createQuery("FROM ExternalIdentifierEntity WHERE orcid = :orcid and id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        return (ExternalIdentifierEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean removeExternalIdentifier(String orcid, Long id) {
        Query query = entityManager.createQuery("delete from ExternalIdentifierEntity where orcid=:orcid and id=:id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public void removeAllExternalIdentifiers(String orcid) {
        Query query = entityManager.createQuery("delete from ExternalIdentifierEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional    
    public void persist(ExternalIdentifierEntity externalId) {
        super.persist(externalId);
    }
    
    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public ExternalIdentifierEntity merge(ExternalIdentifierEntity externalId) {
        return super.merge(externalId);
    }
}
