package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.ProfileRecoveryPhoneEntity;

public interface ProfileRecoveryPhoneDao extends GenericDao<ProfileRecoveryPhoneEntity, Long> {

    ProfileRecoveryPhoneEntity findByOrcid(String orcid);

    /**
     * Stores the recovery phone for a record, replacing any existing one. A
     * record can only ever have a single recovery phone number.
     */
    void upsert(String orcid, String hashedPhoneNumber, String lastFour);

    boolean deleteByOrcid(String orcid);

}
