package org.orcid.core.cli;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.EmailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class PopulateEmailHash {
    private static Logger LOG = LoggerFactory.getLogger(PopulateEmailHash.class);
    private EmailDao emailDao;
    private EncryptionManager encryptionManager;
    private TransactionTemplate transactionTemplate;

    @Option(name = "-b", usage = "Batch size")
    private Integer batchSize;

    public static void main(String... args) {
        PopulateEmailHash element = new PopulateEmailHash();
        CmdLineParser parser = new CmdLineParser(element);
        try {
            parser.parseArgument(args);
            // If it is null or too big
            if (element.batchSize == null || element.batchSize > 1000000) {
                element.batchSize = 50000;
            }
        } catch (CmdLineException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            System.exit(1);
        }
        element.migrate();
    }

    private void migrate() {
        init();
        process();
        System.exit(0);
    }

    private void process() {
        LOG.debug("Starting migration process");
        long startTime = System.currentTimeMillis();        
        long doneCount = 0;
        List<String> emailsToHash = Collections.emptyList();
        do {
            emailsToHash = emailDao.getEmailsToHash(batchSize);
            doneCount += emailsToHash.size();
            migrateList(emailsToHash);
        } while (emailsToHash != null && !emailsToHash.isEmpty());
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.info("Finished hashing emails: doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);       
    }

    private void migrateList(final List<String> emailsToHash) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (String email : emailsToHash) {
                    LOG.info("Migrating " + email);
                    String emailHash = encryptionManager.hashForInternalUse(email.trim().toLowerCase());
                    emailDao.populateEmailHash(email, emailHash);
                }
            }
        });
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        emailDao = (EmailDao) context.getBean("emailDao");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
}
