package org.orcid.core.cli;

import java.util.Date;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class AddReadPublicScopeToNoneInstitutionMembers {

    private ClientDetailsDao clientDetailsDao;
    private ProfileDao profileDao;        
    private TransactionTemplate transactionTemplate;
    
    private int clientsUpdated = 0;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        AddReadPublicScopeToNoneInstitutionMembers mine = new AddReadPublicScopeToNoneInstitutionMembers();
        mine.execute();

    }
    
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        clientDetailsDao = (ClientDetailsDao) context.getBean("clientDetailsDao");
        profileDao = (ProfileDao) context.getBean("profileDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
    
    private void processClients() {        
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<ProfileEntity> clients = profileDao.findProfilesByOrcidType(OrcidType.CLIENT);
                for(ProfileEntity client : clients) {
                    //Only updater clients should be updated
                    if(client.getClientType().equals(ClientType.PREMIUM_UPDATER) || client.getClientType().equals(ClientType.UPDATER)) {
                        ClientDetailsEntity clientDetails = clientDetailsDao.find(client.getId());
                        updateScopes(clientDetails);         
                    }
                }
            }
        });
    }
    
    private void updateScopes(ClientDetailsEntity clientDetails) {
        String readPublicScope = ScopePathType.READ_PUBLIC.value();
        boolean alreadyHaveReadPublicScope = false;
        for(ClientScopeEntity scope : clientDetails.getClientScopes()) {
            if(readPublicScope.equals(scope.getScopeType())) {
                alreadyHaveReadPublicScope = true;
                break;
            }
        }
        
        if(!alreadyHaveReadPublicScope) {
            ClientScopeEntity clientScope = new ClientScopeEntity();
            clientScope.setClientDetailsEntity(clientDetails);
            clientScope.setScopeType(ScopePathType.READ_PUBLIC.value());
            clientScope.setDateCreated(new Date());
            clientScope.setLastModified(new Date());
            clientDetails.getClientScopes().add(clientScope);
            clientDetailsDao.merge(clientDetails);
            clientsUpdated += 1;
            System.out.println("Client " + clientDetails.getId() + " has been updated");
        } else {
            System.out.println("Client " + clientDetails.getId() + " already have the /read-public scope");
        }
        
        
    }
    
    
    
    private void finish() {
        System.out.println("Number of clients updated:" + clientsUpdated);  
        System.exit(0);
    }
    
    public void execute(){
        init();
        processClients();
        finish();
    }

}
