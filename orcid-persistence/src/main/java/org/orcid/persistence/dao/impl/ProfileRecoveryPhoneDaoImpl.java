package org.orcid.persistence.dao.impl;

import java.util.List;

import jakarta.persistence.Query;

import org.orcid.persistence.dao.ProfileRecoveryPhoneDao;
import org.orcid.persistence.jpa.entities.ProfileRecoveryPhoneEntity;
import org.springframework.transaction.annotation.Transactional;

public class ProfileRecoveryPhoneDaoImpl extends GenericDaoImpl<ProfileRecoveryPhoneEntity, Long> implements ProfileRecoveryPhoneDao {

    public ProfileRecoveryPhoneDaoImpl() {
        super(ProfileRecoveryPhoneEntity.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProfileRecoveryPhoneEntity findByOrcid(String orcid) {
        Query query = entityManager.createQuery("FROM ProfileRecoveryPhoneEntity WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        List<ProfileRecoveryPhoneEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public void upsert(String orcid, String hashedPhoneNumber, String lastFour) {
        // dateCreated and lastModified are stamped by the BaseEntity lifecycle callbacks
        ProfileRecoveryPhoneEntity existing = findByOrcid(orcid);
        if (existing == null) {
            ProfileRecoveryPhoneEntity entity = new ProfileRecoveryPhoneEntity();
            entity.setOrcid(orcid);
            entity.setHashedPhoneNumber(hashedPhoneNumber);
            entity.setLastFour(lastFour);
            this.persist(entity);
        } else {
            existing.setHashedPhoneNumber(hashedPhoneNumber);
            existing.setLastFour(lastFour);
            this.merge(existing);
        }
    }

    @Override
    @Transactional
    public boolean deleteByOrcid(String orcid) {
        Query query = entityManager.createQuery("DELETE FROM ProfileRecoveryPhoneEntity WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

}
