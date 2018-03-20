package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.BackupCodeDao;
import org.orcid.persistence.jpa.entities.BackupCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupCodeManagerImpl implements BackupCodeManager {

    private static final int BACKUP_CODE_LENGTH = 10;
    
    private static final int BACKUP_CODE_BATCH_SIZE = 14;

    protected static final Logger LOGGER = LoggerFactory.getLogger(BackupCodeManagerImpl.class);
    
    @Resource
    protected BackupCodeDao backupCodeDao;
    
    @Resource
    protected EncryptionManager encryptionManager;
    
    @Override
    public boolean verify(String orcid, String code) {
        List<BackupCodeEntity> backupCodes = backupCodeDao.getUnusedBackupCodes(orcid);
        for (BackupCodeEntity backupCode : backupCodes) {
            if (encryptionManager.hashMatches(code, backupCode.getHashedCode())) {
                backupCode.setUsedDate(new Date());
                backupCodeDao.merge(backupCode);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> createBackupCodes(String orcid) {
        List<String> rawCodes = new ArrayList<>();
        for (int i = 0; i < BACKUP_CODE_BATCH_SIZE; i++) {
            String rawCode = RandomStringUtils.random(BACKUP_CODE_LENGTH, true, true);
            rawCodes.add(rawCode);

            BackupCodeEntity backupCode = new BackupCodeEntity();
            backupCode.setDateCreated(new Date());
            backupCode.setLastModified(new Date());
            backupCode.setOrcid(orcid);
            backupCode.setHashedCode(encryptionManager.hashForInternalUse(rawCode));
            backupCodeDao.merge(backupCode);
        }
        return rawCodes;
    }

    @Override
    public void removeUnusedBackupCodes(String orcid) {
        backupCodeDao.removedUsedBackupCodes(orcid);
    }


}
