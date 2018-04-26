package org.orcid.core.cli;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
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
public class MigrateEmailFrequencyData {
    private static Logger LOG = LoggerFactory.getLogger(MigrateEmailFrequencyData.class);
    private EmailFrequencyDao emailFrequencyDao;
    private TransactionTemplate transactionTemplate;

    @Option(name = "-b", usage = "Batch size")
    private Integer batchSize;
    
    public static void main(String... args) {
        MigrateEmailFrequencyData element = new MigrateEmailFrequencyData();
        CmdLineParser parser = new CmdLineParser(element);
        try {
            parser.parseArgument(args);
            //If it is null or too big
            if(element.batchSize == null || element.batchSize > 1000000) {
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
        List<Object[]> elements = Collections.emptyList();
        do {
            elements = emailFrequencyDao.findOrcidsToMigrate(this.batchSize);
            for (final Object[] element : elements) {                
                String orcid = (String) element[0];
                Float frequency = (Float) element[1];
                Boolean amend = (Boolean) element[2];
                Boolean admin = (Boolean) element[3];
                Boolean member = (Boolean) element[4];
                Boolean news = (Boolean) element[5];
                LOG.info("Migrating email_frequencies for profile: {}", orcid);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        Date now = new Date();
                        EmailFrequencyEntity entity = new EmailFrequencyEntity();
                        entity.setDateCreated(now);
                        entity.setLastModified(now);
                        entity.setOrcid(orcid);
                        entity.setId(UUID.randomUUID().toString());
                        if (admin != null && admin) {
                            entity.setSendAdministrativeChangeNotifications(frequency);
                        } else {
                            entity.setSendAdministrativeChangeNotifications(SendEmailFrequency.NEVER.floatValue());
                        }
                        if (amend != null && amend) {
                            entity.setSendChangeNotifications(frequency);
                        } else {
                            entity.setSendChangeNotifications(SendEmailFrequency.NEVER.floatValue());
                        }

                        if (member != null && member) {
                            entity.setSendMemberUpdateRequests(frequency);
                        } else {
                            entity.setSendMemberUpdateRequests(SendEmailFrequency.NEVER.floatValue());
                        }

                        if (news != null && news) {
                            if(frequency < SendEmailFrequency.NEVER.floatValue()) {
                                entity.setSendQuarterlyTips(true);
                            } else {
                                entity.setSendQuarterlyTips(false);
                            }                            
                        }
                        emailFrequencyDao.persist(entity);
                    }
                });
            }
        } while (elements != null && !elements.isEmpty());

        LOG.debug("Finished migration process");
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        emailFrequencyDao = (EmailFrequencyDao) context.getBean("emailFrequencyDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
}
