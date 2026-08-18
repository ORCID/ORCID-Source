package org.orcid.core.manager.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.orcid.core.exception.UserAlreadyUsing2FAException;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.pojo.AuthChallenge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TwoFactorAuthenticationManagerImpl implements TwoFactorAuthenticationManager {

    private static final Logger LOG = LoggerFactory.getLogger(TwoFactorAuthenticationManagerImpl.class);

    private static final String APP_NAME = "orcid.org";

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private BackupCodeManager backupCodeManager;

    @Resource
    private ProfileEventDao profileEventDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    @Transactional
    public String getQRCode(String orcid) {
        if (userUsing2FA(orcid)) {
            // don't allow generation of new code if user already using
            throw new UserAlreadyUsing2FAException();
        }
        // generate secret but don't switch on using2FA - user may abort process
        String base32Random = Base32.random();
        String secret = encryptionManager.encryptForInternalUse(base32Random);
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                profileDao.update2FASecret(orcid, secret);
                return true;
            }
        });
        Email email = emailManagerReadOnly.findPrimaryEmail(orcid);
        // generatate URL for QR code per
        // https://github.com/google/google-authenticator/wiki/Key-Uri-Format
        // do not URL encode - authenticator app throws error
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, email.getEmail(), base32Random,
                APP_NAME);
    }

    @Override
    @Transactional
    public List<String> enable2FA(String orcid) {
        LOG.info("2FA enabled for %s", orcid);
        return transactionTemplate.execute(new TransactionCallback<List<String>>() {
            @Override
            public List<String> doInTransaction(TransactionStatus status) {
                profileDao.enable2FA(orcid);
                List<String> codes = backupCodeManager.createBackupCodes(orcid);
                profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.PROFILE_2FA_ENABLED));
                return codes;
            }
        });
    }

    @Override
    @Transactional
    public void disable2FA(String orcid) {
        LOG.warn("2FA disabled for %s", orcid);
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                profileDao.disable2FA(orcid);
                backupCodeManager.removeUnusedBackupCodes(orcid);
                profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.PROFILE_2FA_DISABLED));
                return true;
            }
        });
    }

    @Override
    @Transactional
    public void adminDisable2FA(String orcid, String adminOrcidId) {
        String message = String.format("Admin %s have disabled 2FA for %s", adminOrcidId, orcid);
        LOG.warn(message);
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                profileDao.disable2FA(orcid);
                backupCodeManager.removeUnusedBackupCodes(orcid);
                profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.PROFILE_2FA_DISABLED_BY_ADMIN, message));
                return true;
            }
        });
    }

    @Override
    public boolean userUsing2FA(String orcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        return profile.getUsing2FA();
    }

    @Override
    public boolean verificationCodeIsValid(String code, String orcid) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        return verificationCodeIsValid(code, profileEntity);
    }

    @Override
    public boolean verificationCodeIsValid(String code, ProfileEntity profileEntity) {
        code = code.replaceAll("\\s", "");
        if (!validLong(code)) {
            return false;
        }

        String decryptedSecret = encryptionManager.decryptForInternalUse(profileEntity.getSecretFor2FA());
        Totp totp = new Totp(decryptedSecret);
        return totp.verify(code.replaceAll("\\s", ""));
    }

    private boolean validLong(String code) {
        try {
            Long.parseLong(code);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getSecret(String orcid) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        return encryptionManager.decryptForInternalUse(profileEntity.getSecretFor2FA());
    }

    @Override
    public boolean validateTwoFactorAuthForm(String orcid, AuthChallenge form) {
        if (!userUsing2FA(orcid)) {
            return true;
        }

        form.setTwoFactorEnabled(true);

        String code = form.getTwoFactorCode();
        String recovery = form.getTwoFactorRecoveryCode();
        boolean hasCode = code != null && !code.isEmpty();
        boolean hasRecovery = recovery != null && !recovery.isEmpty();

        // used when 2fa is not immediately provided
        // i.e. tells the UI that 2fa is enabled and the user needs to provide a code
        if (!hasCode && !hasRecovery) {
            return false;
        }

        if (hasRecovery) {
            if (backupCodeManager.verify(orcid, recovery)) {
                return true;
            } else {
                form.setInvalidTwoFactorRecoveryCode(true);
                return false;
            }
        } else {
            if (verificationCodeIsValid(code, orcid)) {
                return true;
            } else {
                form.setInvalidTwoFactorCode(true);
                return false;
            }
        }
    }
}
