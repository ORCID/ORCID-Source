package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.orcid.persistence.dao.IdentityProviderDao;
import org.orcid.persistence.jpa.entities.IdentityProviderEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class IdentityProviderDaoImpl extends GenericDaoImpl<IdentityProviderEntity, Long> implements IdentityProviderDao {

    public IdentityProviderDaoImpl() {
        super(IdentityProviderEntity.class);
    }

    @Override
    public IdentityProviderEntity findByProviderid(String providerid) {
        TypedQuery<IdentityProviderEntity> query = entityManager.createQuery("from IdentityProviderEntity where providerid = :providerid", IdentityProviderEntity.class);
        query.setParameter("providerid", providerid);
        List<IdentityProviderEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public void incrementFailedCount(String providerid) {
        Query query = entityManager.createQuery("update IdentityProviderEntity set lastFailed = now(), failedCount = failedCount + 1 where providerid = :providerid");
        query.setParameter("providerid", providerid);
        query.executeUpdate();
    }

}
