package org.orcid.core.cli;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
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
 * @author Will Simpson
 * 
 */
public class MigrateEncryptedData {

    private ProfileDao profileDao;
    private EncryptionManager encryptionManager;
    private ClientDetailsDao clientDetailsDao;
    private TransactionTemplate transactionTemplate;
    private static Logger LOG = LoggerFactory.getLogger(MigrateEncryptedData.class);
    private static final int CHUNK_SIZE = 1000;

    public static void main(String... args) {
        new MigrateEncryptedData().migrate();
    }

    private void migrate() {
        init();
        migrateProfiles();
        migrateClientDetails();
    }

    private void migrateProfiles() {
        Date start = new Date();
        @SuppressWarnings("unchecked")
        List<ProfileEntity> profiles = Collections.EMPTY_LIST;
        do {
            profiles = profileDao.findLastModifiedBefore(start, CHUNK_SIZE);
            for (ProfileEntity profileEntity : profiles) {
                LOG.info("Migrating encrypted data for profile: {}", profileEntity.getId());
                profileEntity.setLastModified(new Date());
                profileDao.merge(profileEntity);
            }
        } while (!profiles.isEmpty());
    }

    private void migrateClientDetails() {
        Date start = new Date();
        @SuppressWarnings("unchecked")
        List<ClientDetailsEntity> clientDetailsEntities = Collections.EMPTY_LIST;
        do {
            clientDetailsEntities = clientDetailsDao.findLastModifiedBefore(start, CHUNK_SIZE);
            for (final ClientDetailsEntity clientDetails : clientDetailsEntities) {
                LOG.info("Migrating secret for client: {}", clientDetails.getClientId());
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        ClientDetailsEntity retrievedClientDetails = clientDetailsDao.find(clientDetails.getClientId());
                        String unencryptedClientSecret = retrievedClientDetails.getClientSecret();
                        String encryptedClientSecret = encryptionManager.encryptForInternalUse(unencryptedClientSecret);
                        retrievedClientDetails.setClientSecretForJpa(encryptedClientSecret);
                        retrievedClientDetails.setLastModified(new Date());
                        clientDetailsDao.merge(retrievedClientDetails);
                    }
                });

            }
        } while (!clientDetailsEntities.isEmpty());
    }

    @SuppressWarnings("resource")
    private ProfileDao init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
        clientDetailsDao = (ClientDetailsDao) context.getBean("clientDetailsDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
        return profileDao;
    }

}
