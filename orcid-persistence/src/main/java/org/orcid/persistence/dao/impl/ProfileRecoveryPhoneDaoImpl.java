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

    /**
     * Reads go to the read-only pool, as every DAO read does since PD-13463. Inside
     * {@link #upsert} this method is called on {@code this}, not through the proxy, so
     * that lookup stays in the write transaction: an existence check that decides
     * between insert and update must not be answered by a replica.
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public ProfileRecoveryPhoneEntity findByOrcid(String orcid) {
        Query query = entityManager.createQuery("FROM ProfileRecoveryPhoneEntity WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        List<ProfileRecoveryPhoneEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public UpsertResult upsert(String orcid, String encryptedPhoneNumber, String lastFour) {
        // The lookup is a call on this, not on the proxy, so it runs inside this write
        // transaction on the primary: the existence check that decides insert or update
        // has to see the row this same connection may have written a moment ago
        ProfileRecoveryPhoneEntity existing = findByOrcid(orcid);
        if (existing == null) {
            ProfileRecoveryPhoneEntity entity = new ProfileRecoveryPhoneEntity();
            entity.setOrcid(orcid);
            entity.setEncryptedPhoneNumber(encryptedPhoneNumber);
            entity.setLastFour(lastFour);
            this.persist(entity);
            // dateCreated and lastModified were stamped by the BaseEntity lifecycle callback
            return new UpsertResult(entity, true);
        }
        existing.setEncryptedPhoneNumber(encryptedPhoneNumber);
        existing.setLastFour(lastFour);
        this.merge(existing);
        // The update callback that stamps lastModified runs at flush, and the transaction
        // this joined commits after the caller has already built its answer from the row
        this.flush();
        return new UpsertResult(existing, false);
    }

    @Override
    @Transactional
    public boolean deleteByOrcid(String orcid) {
        Query query = entityManager.createQuery("DELETE FROM ProfileRecoveryPhoneEntity WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

}
