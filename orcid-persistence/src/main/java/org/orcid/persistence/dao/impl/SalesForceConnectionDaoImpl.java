package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.SalesForceConnectionDao;
import org.orcid.persistence.jpa.entities.SalesForceConnectionEntity;

/**
 * @author Will Simpson
 */
public class SalesForceConnectionDaoImpl extends GenericDaoImpl<SalesForceConnectionEntity, Long> implements SalesForceConnectionDao {

    public SalesForceConnectionDaoImpl() {
        super(SalesForceConnectionEntity.class);
    }

    @Override
    public SalesForceConnectionEntity findByOrcidAndAccountId(String orcid, String accountId) {
        TypedQuery<SalesForceConnectionEntity> query = entityManager
                .createQuery("from SalesForceConnectionEntity where orcid = :orcid and salesForceAccountId = :accountId", SalesForceConnectionEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("accountId", accountId);
        List<SalesForceConnectionEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<SalesForceConnectionEntity> findByOrcid(String orcid) {
        TypedQuery<SalesForceConnectionEntity> query = entityManager.createQuery("from SalesForceConnectionEntity where orcid = :orcid",
                SalesForceConnectionEntity.class);
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @Override
    public List<SalesForceConnectionEntity> findByAccountId(String accountId) {
        TypedQuery<SalesForceConnectionEntity> query = entityManager.createQuery("from SalesForceConnectionEntity where salesForceAccountId = :salesForceAccountId",
                SalesForceConnectionEntity.class);
        query.setParameter("salesForceAccountId", accountId);
        return query.getResultList();
    }

}
