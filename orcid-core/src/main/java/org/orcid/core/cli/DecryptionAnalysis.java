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

import java.util.List;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.ProfileDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class DecryptionAnalysis {

    private ProfileDao profileDao;
    private EncryptionManager encryptionManager;

    public static void main(String... args) {
        new DecryptionAnalysis().analyse();
    }

    private void analyse() {
        init();
        analyseProfiles();
    }

    private void analyseProfiles() {
        List<Object[]> profileInfos = profileDao.findInfoForDecryptionAnalysis();
        for (Object[] profileInfo : profileInfos) {
            String orcid = (String) profileInfo[0];
            String encryptedSecurityAnswer = (String) profileInfo[1];
            String decryptedSecurityAnswer = encryptionManager.decryptForInternalUse(encryptedSecurityAnswer);
            String doubleDecryptedSecurityAnswer = null;
            try {
                doubleDecryptedSecurityAnswer = encryptionManager.decryptForInternalUse(decryptedSecurityAnswer);
            } catch (EncryptionOperationNotPossibleException e) {
                // Nothing needed here for now.
            }
            System.out.println(String.format("%s\t%s\t%s\t%s", orcid, encryptedSecurityAnswer, decryptedSecurityAnswer, doubleDecryptedSecurityAnswer));
        }
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
    }

}
