package org.orcid.core.cli;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class UpdateSecretKeyStatus {
    private ClientDetailsDao clientDetailsDao;
    private ClientSecretDao clientSecretDao;
    private TransactionTemplate transactionTemplate;
    
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        clientDetailsDao = (ClientDetailsDao) context.getBean("clientDetailsDao");
        clientSecretDao = (ClientSecretDao) context.getBean("clientSecretDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
    
    public ClientSecretEntity getNewestClientSecret(Set<ClientSecretEntity> clientSecrets) {
        ClientSecretEntity latest = null;
        if(clientSecrets != null && !clientSecrets.isEmpty()) {
            Iterator<ClientSecretEntity> it = clientSecrets.iterator();
            while(it.hasNext()) {
                ClientSecretEntity actual = it.next();
                if(latest != null) {
                    Date actualLatest = latest.getDateCreated();
                    Date newLatest = actual.getDateCreated();
                    if(actualLatest.compareTo(newLatest) < 0) {
                        latest = actual;
                    }                    
                } else {
                    latest = actual;
                }
                
            }
        }
        return latest;
    }
        
    public void updateClientSecretKeys(final ClientDetailsEntity clientDetails) {  
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //#1 revoke all keys
                clientSecretDao.revokeAllKeys(clientDetails.getClientId());
                //#2 look for the newest key
                ClientSecretEntity newestClientSecret = getNewestClientSecret(clientDetails.getClientSecrets());
                //#3 set the newest client secret as the primary
                clientSecretDao.setAsPrimary(newestClientSecret);
                //#4 update the last modified to the client_details entity
                clientDetailsDao.updateLastModified(clientDetails.getClientId());
            }
        });
    }
    
    public void execute() {
        List<ClientDetailsEntity> allClientDetails = clientDetailsDao.getAll();
        if(allClientDetails != null && !allClientDetails.isEmpty()) {
            for(ClientDetailsEntity clientDetails : allClientDetails) {
                System.out.println("Processing client: " + clientDetails.getClientId());
                updateClientSecretKeys(clientDetails);
                System.out.println("Done with client: " + clientDetails.getClientId());
            }
        }
    }
    
    private void finish() { 
        System.out.println("Done");
        System.exit(0);
    }
    
    public static void main(String [] args) {
        UpdateSecretKeyStatus update = new UpdateSecretKeyStatus();
        update.init();
        update.execute();
        update.finish();
    }
}
