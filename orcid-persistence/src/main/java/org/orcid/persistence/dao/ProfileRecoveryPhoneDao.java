package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.ProfileRecoveryPhoneEntity;

public interface ProfileRecoveryPhoneDao extends GenericDao<ProfileRecoveryPhoneEntity, Long> {

    ProfileRecoveryPhoneEntity findByOrcid(String orcid);

    /**
     * Stores the recovery phone for a record, replacing any existing one. A
     * record can only ever have a single recovery phone number.
     *
     * @return the row as it now stands, flushed so its dates are the stored
     *         ones, and whether this call created it. A caller that needs to
     *         know what was written reads it from here rather than looking the
     *         row up again: {@link #findByOrcid} runs on the read-only
     *         transaction manager, which is a second connection inside a write
     *         and, in a deployed environment, a replica that may not yet carry
     *         the row.
     */
    UpsertResult upsert(String orcid, String encryptedPhoneNumber, String lastFour);

    boolean deleteByOrcid(String orcid);

    /** What an upsert did: the stored row and whether it was created by that call. */
    final class UpsertResult {
        private final ProfileRecoveryPhoneEntity entity;
        private final boolean inserted;

        public UpsertResult(ProfileRecoveryPhoneEntity entity, boolean inserted) {
            this.entity = entity;
            this.inserted = inserted;
        }

        public ProfileRecoveryPhoneEntity getEntity() {
            return entity;
        }

        public boolean isInserted() {
            return inserted;
        }
    }

}
