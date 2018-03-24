package org.orcid.core.cli;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 * */
public class AddGrantTypeToExistingClients {

    
    @Option(name = "-g", usage = "Grant types to add ('client_credentials', 'refresh_token', 'authorization_code', 'implicit')")
    private String newGrantTypes;
    
    @Option(name = "-t", usage = "CSV client types, must be one in ClientType, if null, assume the change applies to all client types")
    private String clientTypes;
    
    private Set<String> grantTypes = new HashSet<String>();
    
    private Set<ClientType> allowedClientTypes = new HashSet<ClientType>();
    
    private ClientDetailsManager clientDetailsManager;
    private TransactionTemplate transactionTemplate;

    private int clientsUpdated = 0;    
    
    public static void main(String [] args) {
        AddGrantTypeToExistingClients addGrantTypeToExistingClients = new AddGrantTypeToExistingClients();
        CmdLineParser parser = new CmdLineParser(addGrantTypeToExistingClients);
        try {           
            parser.parseArgument(args);
            addGrantTypeToExistingClients.validateParameters(parser);
            addGrantTypeToExistingClients.init();
            addGrantTypeToExistingClients.process();
            System.out.println();
            System.out.println();
            System.out.println(addGrantTypeToExistingClients.getClientsUpdated() + " clients were updated");
            System.out.println();
            System.out.println();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }        
        System.exit(0);
    }
    
    @SuppressWarnings("resource")
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        clientDetailsManager = (ClientDetailsManager) context.getBean("clientDetailsManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
    
    public void process() {         
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<ClientDetailsEntity> clients = clientDetailsManager.getAll();
                for (ClientDetailsEntity client : clients) {
                    // Only updater clients should be updated
                    if (isInAllowedClientTypes(client)) {
                        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(client.getId());
                        updateGrantTypes(clientDetails);
                    }
                }
            }
        });
    }
    
    private boolean isInAllowedClientTypes(ClientDetailsEntity client) {
        if(allowedClientTypes == null || allowedClientTypes.isEmpty())
            return true;
        
        for(ClientType clientType : allowedClientTypes) {
            if(clientType.equals(client.getClientType()))
                return true;
        }
        
        return false;
    }
    
    private void updateGrantTypes(ClientDetailsEntity clientDetails) {        
        for(String grantType : grantTypes) {            
            boolean alreadyHaveGrantType = false;
            for (String existingGrantType : clientDetails.getAuthorizedGrantTypes()) {
                if (grantType.equals(existingGrantType)) {
                    alreadyHaveGrantType = true;
                    break;
                }
            }

            if (!alreadyHaveGrantType) {
                ClientAuthorisedGrantTypeEntity newGrantType = new ClientAuthorisedGrantTypeEntity();
                newGrantType.setGrantType(grantType);
                newGrantType.setClientDetailsEntity(clientDetails);
                clientDetails.getClientAuthorizedGrantTypes().add(newGrantType);
                
                clientDetailsManager.merge(clientDetails);
                clientsUpdated += 1;
                System.out.println("Client " + clientDetails.getId() + " has been updated");                
            } else {
                System.out.println("Client " + clientDetails.getId() + " already have the " + grantType + " scope");
            }
        }                
    }        
    
    public void validateParameters(CmdLineParser parser) throws CmdLineException {
        if(PojoUtil.isEmpty(newGrantTypes)) {
            throw new CmdLineException(parser, "-s parameter must not be null");
        } else {
            String [] grantTypesArray = newGrantTypes.split(",");
            for(String grantType : grantTypesArray) {
                grantType = grantType.trim();                 
                if(!grantType.equals("refresh_token") && !grantType.equals("authorization_code") && !grantType.equals("client_credentials") && !grantType.equals("implicit")) {
                    throw new CmdLineException(parser, "Invalid grantType: " + grantType);
                }
                if(!PojoUtil.isEmpty(grantType)){
                    grantTypes.add(grantType);
                }                                                       
            }
        }
        
        if(!PojoUtil.isEmpty(clientTypes)) {
            String [] clientTypesArray = clientTypes.split(",");
            for(String clientType : clientTypesArray) {
                try {
                    allowedClientTypes.add(ClientType.fromValue(clientType));
                } catch(IllegalArgumentException ie) {
                    throw new CmdLineException(parser, "Invalid client type: " + clientType);
                }
            }            
        }
    }

    public int getClientsUpdated() {
        return clientsUpdated;
    }            
}
