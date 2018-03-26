package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ShibbolethAccountDao;
import org.orcid.persistence.jpa.entities.ShibbolethAccountEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 *
 */
public class ShibbolethAccountDaoImpl extends GenericDaoImpl<ShibbolethAccountEntity, Long> implements ShibbolethAccountDao {

    public ShibbolethAccountDaoImpl() {
        super(ShibbolethAccountEntity.class);
    }

    @Override
    public ShibbolethAccountEntity findByRemoteUserAndShibIdentityProvider(String remoteUser, String shibIdentityProvider) {
        TypedQuery<ShibbolethAccountEntity> query = entityManager.createQuery(
                "from ShibbolethAccountEntity where remoteUser = :remoteUser and shibIdentityProvider = :shibIdentityProvider", ShibbolethAccountEntity.class);
        query.setParameter("remoteUser", remoteUser);
        query.setParameter("shibIdentityProvider", shibIdentityProvider);
        List<ShibbolethAccountEntity> results = query.getResultList();
        return results != null && !results.isEmpty() ? results.get(0) : null;
    }

    @Override
    public List<ShibbolethAccountEntity> findByOrcid(String orcid) {
        TypedQuery<ShibbolethAccountEntity> query = entityManager.createQuery("from ShibbolethAccountEntity where orcid = :orcid", ShibbolethAccountEntity.class);
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void removeByIdAndOrcid(Long id, String orcid) {
        Query query = entityManager.createQuery("delete from ShibbolethAccountEntity where id = :id and orcid = :orcid");
        query.setParameter("id", id);
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

}
