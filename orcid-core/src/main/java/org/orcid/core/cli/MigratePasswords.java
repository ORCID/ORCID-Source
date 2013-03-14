/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.cli;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class MigratePasswords {

    private ProfileDao profileDao;
    private EncryptionManager encryptionManager;
    private static Logger LOG = LoggerFactory.getLogger(MigratePasswords.class);
    private static final int CHUNK_SIZE = 1000;

    public static void main(String... args) {
        new MigratePasswords().migrate();
    }

    private void migrate() {
        init();
        Date start = new Date();
        @SuppressWarnings("unchecked")
        List<ProfileEntity> profiles = Collections.EMPTY_LIST;
        do {
            profiles = profileDao.findLastModifiedBefore(start, CHUNK_SIZE);
            for (ProfileEntity profileEntity : profiles) {
                LOG.info("Migrating password for profile: {}", profileEntity.getId());
                String encryptedPassword = profileEntity.getEncryptedPassword();
                if (encryptedPassword != null) {
                    String decryptedPassword = encryptionManager.legacyDecryptForInternalUse(encryptedPassword);
                    String hashedPassword = encryptionManager.hashForInternalUse(decryptedPassword);
                    profileEntity.setEncryptedPassword(hashedPassword);
                }
                profileEntity.setLastModified(new Date());
                profileDao.merge(profileEntity);
            }
        } while (!profiles.isEmpty());
    }

    private ProfileDao init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
        return profileDao;
    }

}
