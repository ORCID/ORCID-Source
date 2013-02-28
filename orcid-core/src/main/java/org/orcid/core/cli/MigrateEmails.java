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
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.ProfileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class MigrateEmails {

    private ProfileDao profileDao;
    private OrcidProfileManager orcidProfileManager;
    private TransactionTemplate transactionTemplate;
    private static Logger LOG = LoggerFactory.getLogger(MigrateEmails.class);
    private static final int CHUNK_SIZE = 1000;

    public static void main(String... args) {
        new MigrateEmails().migrate();
    }

    private void migrate() {
        init();
        migrateProfiles();
    }

    private void migrateProfiles() {
        long startTime = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        List<String> orcids = Collections.EMPTY_LIST;
        int doneCount = 0;
        do {
            orcids = profileDao.findOrcidsNeedingEmailMigration(CHUNK_SIZE);
            for (final String orcid : orcids) {
                LOG.info("Migrating emails for profile: {}", orcid);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                        // Save it straight back - it will be saved back in the
                        // new DB table automatically
                        orcidProfileManager.updateOrcidProfile(orcidProfile);
                    }
                });
                doneCount++;
            }
        } while (!orcids.isEmpty());
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.info("Finished migrating emails: doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        orcidProfileManager = (OrcidProfileManager) context.getBean("orcidProfileManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }

}
