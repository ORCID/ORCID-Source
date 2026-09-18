package org.orcid.core.manager.impl;

import jakarta.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.RecoveryPhone;
import org.orcid.core.manager.RecoveryPhoneManager;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.dao.ProfileRecoveryPhoneDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.ProfileRecoveryPhoneEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class RecoveryPhoneManagerImpl implements RecoveryPhoneManager {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveryPhoneManagerImpl.class);

    private static final int LAST_FOUR_LENGTH = 4;

    @Resource
    private ProfileRecoveryPhoneDao profileRecoveryPhoneDao;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ProfileEventDao profileEventDao;

    @Override
    public RecoveryPhone getRecoveryPhone(String orcid) {
        ProfileRecoveryPhoneEntity entity = profileRecoveryPhoneDao.findByOrcid(orcid);
        if (entity == null) {
            return null;
        }
        return toRecoveryPhone(entity);
    }

    @Override
    public String getDecryptedPhoneNumber(String orcid) {
        ProfileRecoveryPhoneEntity entity = profileRecoveryPhoneDao.findByOrcid(orcid);
        if (entity == null) {
            return null;
        }
        // Decrypting lives here and not in getRecoveryPhone because the Account settings panel polls
        // that one for the mask and the dates alone; only the flows that have to text the number pay
        // for the crypto, and the result must not reach a log line, a RUM attribute or an HTTP response.
        return encryptionManager.decryptForInternalUse(entity.getEncryptedPhoneNumber());
    }

    @Override
    @Transactional
    public RecoveryPhone saveRecoveryPhone(String orcid, String e164PhoneNumber) {
        // Whether this is a first number or a replacement is the DAO's to say: it decides
        // inside its own write transaction. Asking findByOrcid here would open a second,
        // read-only connection in the middle of this write and, on a deployed environment,
        // ask a replica that may still be missing a row this record wrote seconds ago
        ProfileRecoveryPhoneDao.UpsertResult result = profileRecoveryPhoneDao.upsert(orcid,
                encryptionManager.encryptForInternalUse(e164PhoneNumber), lastFour(e164PhoneNumber));
        boolean isNew = result.isInserted();
        profileEventDao.persist(new ProfileEventEntity(orcid,
                isNew ? ProfileEventType.PROFILE_RECOVERY_PHONE_ADDED : ProfileEventType.PROFILE_RECOVERY_PHONE_UPDATED));
        LOG.info("Recovery phone {} for {}", isNew ? "added" : "updated", orcid);
        return toRecoveryPhone(result.getEntity());
    }

    @Override
    @Transactional
    public void removeRecoveryPhone(String orcid) {
        if (profileRecoveryPhoneDao.deleteByOrcid(orcid)) {
            profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.PROFILE_RECOVERY_PHONE_REMOVED));
            LOG.info("Recovery phone removed for {}", orcid);
        }
    }

    private static RecoveryPhone toRecoveryPhone(ProfileRecoveryPhoneEntity entity) {
        return new RecoveryPhone(entity.getLastFour(), entity.getDateCreated(), entity.getLastModified());
    }

    private String lastFour(String e164PhoneNumber) {
        String digits = e164PhoneNumber.replaceAll("\\D", "");
        return digits.length() <= LAST_FOUR_LENGTH ? digits : digits.substring(digits.length() - LAST_FOUR_LENGTH);
    }

}
