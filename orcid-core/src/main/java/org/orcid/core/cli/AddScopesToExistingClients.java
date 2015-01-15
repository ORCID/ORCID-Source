/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 * */
public class AddScopesToExistingClients {

    
    @Option(name = "-s", usage = "CSV new scopes to add, they must be valid values in the ScopePathType enum")
    private String newScopes;
    
    @Option(name = "-t", usage = "CSV client types, must be one in ClientType, if null, assume the change applies to all client types")
    private String clientTypes;
    
    private Set<ScopePathType> scopes = new HashSet<ScopePathType>();
    
    private Set<ClientType> allowedClientTypes = new HashSet<ClientType>();
    
    private EntityManager entityManager;
    
    private TransactionTemplate transactionTemplate;    
    
    public void process() {
        this.init();        
        this.updateExistingClients(this.getScopes(), this.getAllowedClientTypes());
    }
    
    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        EntityManagerFactory emf = (EntityManagerFactory) context.getBean("entityManagerFactory");        
        entityManager = emf.createEntityManager();        
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
    
    public void validateParameters(CmdLineParser parser) throws CmdLineException {
        if(PojoUtil.isEmpty(newScopes)) {
            throw new CmdLineException(parser, "-s parameter must not be null");
        } else {
            String [] scopesArray = newScopes.split(",");
            for(String scope : scopesArray) {
                try {
                    scopes.add(ScopePathType.fromValue(scope));
                } catch(IllegalArgumentException ie) {
                    throw new CmdLineException(parser, "Invalid scope: " + scope);
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
            
    private void updateExistingClients(final Set<ScopePathType> newScopes, final Set<ClientType> clientTypes) {        
        TypedQuery<String> clientIdsQuery = entityManager.createQuery("select id from ClientDetailsEntity", String.class);
        List<String> clientIds = clientIdsQuery.getResultList();
        for(String clientId : clientIds) {
            if(clientTypes == null || clientTypes.isEmpty() || isClientOfType(clientId, clientTypes))
                updateClientScopes(clientId, newScopes);
        }
    }
    
    private boolean isClientOfType(String clientId, Set<ClientType> allowedClientTypes) {
        TypedQuery<ClientType> clientTypeQuery = entityManager.createQuery("select clientType from ClientDetailsEntity where id=:clientId", ClientType.class);
        clientTypeQuery.setParameter("clientId", clientId);
        ClientType clientType = clientTypeQuery.getSingleResult();
        for(ClientType allowedClientType : allowedClientTypes) {
            if(clientType.equals(allowedClientType))
                return true;
        }
        
        return false;
    }
        
    private void updateClientScopes(String clientId, Set<ScopePathType> newScopes) {
        Set<String> newScopesAsStrings = new HashSet<String>();
        for(ScopePathType scope : newScopes)
            newScopesAsStrings.add(scope.getContent());
        
        
        TypedQuery<String> clientScopes = entityManager.createQuery("select scopeType from ClientScopeEntity where clientDetailsEntity.id=:clientId and scopeType in :newScopes", String.class);
        clientScopes.setParameter("clientId", clientId);
        clientScopes.setParameter("newScopes", newScopesAsStrings);
        List<String> scopeAlreadyInClient = clientScopes.getResultList();
        
        boolean modified = false;
        
        for(ScopePathType newScope : newScopes) {
            String newScopeString = newScope.getContent();
            if(!scopeAlreadyInClient.contains(newScopeString)) {                
                insertNewScopesToClient(clientId, newScopeString);
                modified = true;
            }
        }
        
        //If modified, update the last client modified date
        if(modified) {
            Query updateLastModified = entityManager.createNativeQuery("update client_details set last_modified=now() where client_details_id=:clientId");
            updateLastModified.setParameter("clientId", clientId);
            updateLastModified.executeUpdate();
        }
    }
            
    public Set<ScopePathType> getScopes() {
        return scopes;
    }

    public Set<ClientType> getAllowedClientTypes() {
        return allowedClientTypes;
    }
    
    @Transactional
    private void insertNewScopesToClient(final String clientId, final String scope) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //Insert new scope                
                Query updateScope = entityManager.createNativeQuery("insert into client_scope(client_details_id, scope_type, date_created, last_modified) values(:clientId, :newScope,now(),now())");
                updateScope.setParameter("clientId", clientId);
                updateScope.setParameter("newScope", scope);
                updateScope.executeUpdate();
            }
        });
    }

    public static void main(String [] args) {
        AddScopesToExistingClients addScopesToExistingClients = new AddScopesToExistingClients();
        CmdLineParser parser = new CmdLineParser(addScopesToExistingClients);
        try {           
            parser.parseArgument(args);
            addScopesToExistingClients.validateParameters(parser);
            addScopesToExistingClients.process();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }        
        System.exit(0);
    }
}
