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

import java.util.UUID;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
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
public class CreateNewClientSecrets {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        final EncryptionManager encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
        final ClientDetailsDao clientDetailsDao = (ClientDetailsDao) context.getBean("clientDetailsDao");
        TransactionTemplate transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                for (ClientDetailsEntity clientDetails : clientDetailsDao.getAll()) {
                    String clientSecret = UUID.randomUUID().toString();
                    clientDetails.getClientSecrets().add(new ClientSecretEntity(encryptionManager.encryptForInternalUse(clientSecret), clientDetails));
                    clientDetailsDao.merge(clientDetails);
                    System.out.println(String.format("%s\t%s\t%s", clientDetails.getId(), clientDetails.getClientName(), clientSecret));
                }
            }
        });
        System.exit(0);
    }

}
