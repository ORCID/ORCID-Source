package org.orcid.core.cli;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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

    public static void main(String... args) {
        new MigrateEmailFrequencyData().migrate();
    }

    private void migrate() {
        init();
        migrateAddress();
        System.exit(0);
    }

    private void migrateAddress() {
        LOG.debug("Starting migration process");
        List<Object[]> elements = Collections.emptyList();
        //SELECT orcid, 
        //send_email_frequency_days, 
        //send_change_notifications, 
        //send_administrative_change_notifications, 
        //send_orcid_news, 
        //send_member_update_requests FROM profile WHERE orcid NOT IN (SELECT orcid FROM email_frequency)
        do {
            elements = emailFrequencyDao.findOrcidsToProfess(10000);
            for(final Object[] element : elements) {
                String orcid = (String) element[0];
                Float frequency = (Float)element[1];
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
                        entity.setSendAdministrativeChangeNotifications();
                        entity.setSendChangeNotifications();
                        entity.setSendMemberUpdateRequests();
                        entity.setSendQuarterlyTips();
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
