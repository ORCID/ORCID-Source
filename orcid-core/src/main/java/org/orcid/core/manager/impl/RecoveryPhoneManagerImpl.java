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
        return new RecoveryPhone(entity.getLastFour(), entity.getDateCreated(), entity.getLastModified());
    }

    @Override
    @Transactional
    public boolean saveRecoveryPhone(String orcid, String e164PhoneNumber) {
        boolean isNew = profileRecoveryPhoneDao.findByOrcid(orcid) == null;
        profileRecoveryPhoneDao.upsert(orcid, encryptionManager.hashForInternalUse(e164PhoneNumber), lastFour(e164PhoneNumber));
        profileEventDao.persist(new ProfileEventEntity(orcid,
                isNew ? ProfileEventType.PROFILE_RECOVERY_PHONE_ADDED : ProfileEventType.PROFILE_RECOVERY_PHONE_UPDATED));
        LOG.info("Recovery phone {} for {}", isNew ? "added" : "updated", orcid);
        return isNew;
    }

    @Override
    @Transactional
    public void removeRecoveryPhone(String orcid) {
        if (profileRecoveryPhoneDao.deleteByOrcid(orcid)) {
            profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.PROFILE_RECOVERY_PHONE_REMOVED));
            LOG.info("Recovery phone removed for {}", orcid);
        }
    }

    @Override
    public boolean matches(String orcid, String e164PhoneNumber) {
        ProfileRecoveryPhoneEntity entity = profileRecoveryPhoneDao.findByOrcid(orcid);
        if (entity == null || e164PhoneNumber == null) {
            return false;
        }
        return encryptionManager.hashMatches(e164PhoneNumber, entity.getHashedPhoneNumber());
    }

    private String lastFour(String e164PhoneNumber) {
        String digits = e164PhoneNumber.replaceAll("\\D", "");
        return digits.length() <= LAST_FOUR_LENGTH ? digits : digits.substring(digits.length() - LAST_FOUR_LENGTH);
    }

}
