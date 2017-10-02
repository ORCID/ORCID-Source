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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.clientgroup.RedirectUris;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidClientGroupManagerTest extends BaseTest {
    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Mock
    private OrcidIndexManager orcidIndexManager;
    
    @Mock
    private SourceManager mockSourceManager;

    private OrcidClientGroup group = null;
    private final String email = "orcid-admin@elsevier.com"; 
    
    {
        group = new OrcidClientGroup();
        group.setGroupName("Elsevier");
        group.setEmail(email);
        group.setType(MemberType.PREMIUM);
        
        OrcidClient client1 = new OrcidClient();
        client1.setType(ClientType.UPDATER);
        client1.setDisplayName("Ecological Complexity");
        client1.setWebsite("http://www.journals.elsevier.com/ecological-complexity");
        client1.setIdp("IdP-1");
        client1.setShortDescription("An International Journal on Biocomplexity in the Environment and Theoretical Ecology");
        RedirectUris rUris = new RedirectUris();
        RedirectUri r1 = new RedirectUri();
        r1.setScope(Arrays.asList(ScopePathType.fromValue("/orcid-profile/create"), ScopePathType.fromValue("/orcid-bio/read-limited")));
        r1.setType(RedirectUriType.GRANT_READ_WIZARD);
        r1.setValue("http://www.journals.elsevier.com/ecological-complexity/orcid-callback");
        rUris.getRedirectUri().add(r1);
        client1.setRedirectUris(rUris);        
        
        OrcidClient client2 = new OrcidClient();
        client2.setType(ClientType.UPDATER);
        client2.setDisplayName("Ecological Economics");
        client2.setWebsite("http://www.journals.elsevier.com/ecological-economics");
        client2.setIdp("IdP-2");
        client2.setShortDescription("The Transdisciplinary Journal of the International Society for Ecological Economics (ISEE)");
        RedirectUris rUris2 = new RedirectUris();
        RedirectUri r2 = new RedirectUri();
        r2.setScope(Arrays.asList(ScopePathType.fromValue("/orcid-bio/read-limited")));
        r2.setType(RedirectUriType.DEFAULT);
        r2.setValue("http://www.journals.elsevier.com/ecological-economics/orcid-callback");
        rUris2.getRedirectUri().add(r2);
        client2.setRedirectUris(rUris2);
        
        group.getOrcidClient().add(client1);
        group.getOrcidClient().add(client2);
    }
    
    @Before
    public void initMocks() throws Exception {
        removeDBUnitData(Collections.<String> emptyList());        
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "orcidIndexManager", orcidIndexManager);         
        
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", mockSourceManager);
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(null);
        when(mockSourceManager.retrieveSourceOrcid()).thenReturn(null);
        
        SecurityContextTestUtils.setUpSecurityContextForAnonymous();
        group.setEmail(group.getEmail() + System.currentTimeMillis());
    }

    @After
    public void after() {
        group.setEmail(email);
    }
    
    @Test    
    public void testCreateOrcidClientGroup() {
        OrcidClientGroup createdGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
        assertNotNull(createdGroup);

        assertEquals(group.getGroupName(), createdGroup.getGroupName());
        assertEquals(group.getEmail(), createdGroup.getEmail());
        assertNotNull(createdGroup.getGroupOrcid());
        List<OrcidClient> createdClients = createdGroup.getOrcidClient();
        assertNotNull(createdClients);
        assertEquals(2, createdClients.size());
        Map<String, OrcidClient> createdClientsMappedByName = new HashMap<String, OrcidClient>();
        for (OrcidClient createdClient : createdClients) {
            assertNotNull(createdClient.getClientId());
            assertNotNull(createdClient.getClientSecret());
            assertNotNull(createdClient.getIdp());
            createdClientsMappedByName.put(createdClient.getDisplayName(), createdClient);
        }
        OrcidClient complexityClient = createdClientsMappedByName.get("Ecological Complexity");
        assertNotNull(complexityClient);
        assertEquals("http://www.journals.elsevier.com/ecological-complexity", complexityClient.getWebsite());
        assertEquals("An International Journal on Biocomplexity in the Environment and Theoretical Ecology", complexityClient.getShortDescription());

        List<RedirectUri> createdRedirectUris = complexityClient.getRedirectUris().getRedirectUri();
        assertNotNull(createdRedirectUris);
        assertEquals(1, createdRedirectUris.size());
        assertEquals("http://www.journals.elsevier.com/ecological-complexity/orcid-callback", createdRedirectUris.get(0).getValue());
        assertEquals("grant-read-wizard", createdRedirectUris.get(0).getType().value());
        List<ScopePathType> scopesForRedirect = createdRedirectUris.get(0).getScope();
        assertTrue(scopesForRedirect.size() == 2);
        assertTrue(scopesForRedirect.contains(ScopePathType.ORCID_PROFILE_CREATE) && scopesForRedirect.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertEquals("http://www.journals.elsevier.com/ecological-complexity/orcid-callback", createdRedirectUris.get(0).getValue());
        assertEquals("grant-read-wizard", createdRedirectUris.get(0).getType().value());
        // Look up client details directly to check scopes
        ClientDetailsEntity complexityEntity = clientDetailsManager.findByClientId(complexityClient.getClientId());
        Set<String> clientScopeTypes = complexityEntity.getScope();
        assertNotNull(clientScopeTypes);
        assertTrue(clientScopeTypes.contains("/orcid-bio/update"));
        assertTrue(clientScopeTypes.contains("/orcid-bio/read-limited"));
        assertTrue(clientScopeTypes.contains("/orcid-works/read-limited"));
        assertFalse(clientScopeTypes.contains("/orcid-profile/create"));
        assertTrue(clientScopeTypes.contains("/authenticate"));        
    }

    @Test
    public void testCreateOrcidCreatorClientGroup() {
        OrcidClientGroup createdGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
        assertNotNull(createdGroup);

        assertEquals(group.getGroupName(), createdGroup.getGroupName());
        assertEquals(group.getEmail(), createdGroup.getEmail());
        assertNotNull(createdGroup.getGroupOrcid());
        List<OrcidClient> createdClients = createdGroup.getOrcidClient();
        assertNotNull(createdClients);
        assertEquals(2, createdClients.size());
        Map<String, OrcidClient> createdClientsMappedByName = new HashMap<String, OrcidClient>();
        for (OrcidClient createdClient : createdClients) {
            assertNotNull(createdClient.getClientId());
            assertNotNull(createdClient.getClientSecret());
            assertNotNull(createdClient.getIdp());
            createdClientsMappedByName.put(createdClient.getDisplayName(), createdClient);
        }
        OrcidClient complexityClient = createdClientsMappedByName.get("Ecological Complexity");
        assertNotNull(complexityClient);
        assertEquals("http://www.journals.elsevier.com/ecological-complexity", complexityClient.getWebsite());
        assertEquals("An International Journal on Biocomplexity in the Environment and Theoretical Ecology", complexityClient.getShortDescription());
        List<RedirectUri> createdRedirectUris = complexityClient.getRedirectUris().getRedirectUri();
        assertNotNull(createdRedirectUris);
        assertEquals(1, createdRedirectUris.size());
        assertEquals("http://www.journals.elsevier.com/ecological-complexity/orcid-callback", createdRedirectUris.get(0).getValue());
        // Look up client details directly to check scopes
        ClientDetailsEntity complexityEntity = clientDetailsManager.findByClientId(complexityClient.getClientId());
        Set<String> clientScopeTypes = complexityEntity.getScope();
        assertNotNull(clientScopeTypes);
        assertTrue(clientScopeTypes.contains("/orcid-profile/read-limited"));
        assertTrue(clientScopeTypes.contains("/orcid-bio/read-limited"));
        assertTrue(clientScopeTypes.contains("/orcid-works/read-limited"));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testUpdateOrcidClientGroup() {
        OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
            }
        });

        createdGroup.setEmail("admin@somethingelse.com");
        for (OrcidClient createdClient : createdGroup.getOrcidClient()) {
            if ("Ecological Complexity".equals(createdClient.getDisplayName())) {
                createdClient.setWebsite("wwww.ecologicalcomplexity.com");
            }
        }
        OrcidClientGroup updatedGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(createdGroup);

        assertNotNull(updatedGroup);
        assertEquals("Elsevier", updatedGroup.getGroupName());
        assertEquals("admin@somethingelse.com", updatedGroup.getEmail());
        assertNotNull(updatedGroup.getGroupOrcid());
        List<OrcidClient> updatedClients = updatedGroup.getOrcidClient();
        assertNotNull(updatedClients);
        assertEquals(2, updatedClients.size());
        Map<String, OrcidClient> updatedClientsMappedByName = new HashMap<String, OrcidClient>();
        for (OrcidClient upatedClient : updatedClients) {
            assertNotNull(upatedClient.getClientId());
            assertNotNull(upatedClient.getClientSecret());
            updatedClientsMappedByName.put(upatedClient.getDisplayName(), upatedClient);
        }
        OrcidClient complexityClient = updatedClientsMappedByName.get("Ecological Complexity");
        assertNotNull(complexityClient);
        assertEquals("wwww.ecologicalcomplexity.com", complexityClient.getWebsite());
        assertEquals("An International Journal on Biocomplexity in the Environment and Theoretical Ecology", complexityClient.getShortDescription());
        List<RedirectUri> updatedRedirectUris = complexityClient.getRedirectUris().getRedirectUri();
        assertNotNull(updatedRedirectUris);
        assertEquals(1, updatedRedirectUris.size());
        Collections.sort(updatedRedirectUris, new Comparator<RedirectUri>() {
            public int compare(RedirectUri redirectUri1, RedirectUri redirectUri2) {
                return ((String) redirectUri1.getValue()).compareToIgnoreCase((String) redirectUri1.getValue());
            }
        });

        assertEquals("http://www.journals.elsevier.com/ecological-complexity/orcid-callback", updatedRedirectUris.get(0).getValue());
        List<ScopePathType> scopesForRedirect = updatedRedirectUris.get(0).getScope();
        assertTrue(scopesForRedirect.size() == 2);
        assertTrue(scopesForRedirect.contains(ScopePathType.ORCID_PROFILE_CREATE) && scopesForRedirect.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testGetOrcidClientList() {
        OrcidClientGroup createdGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);

        createdGroup = orcidClientGroupManager.retrieveOrcidClientGroup(createdGroup.getGroupOrcid());

        assertNotNull(createdGroup);
        assertNotNull(createdGroup.getOrcidClient());
        assertEquals(2, createdGroup.getOrcidClient().size());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddMoreThanOneClientToBasicGroup() {
        final OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupName("Elsevier");
        group.setEmail("basic-member@elsevier.com" + System.currentTimeMillis());        
        group.setType(MemberType.BASIC);
        
        OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
            }
        });

        RedirectUris redirectUris = new RedirectUris();
        RedirectUri redirectUri = new RedirectUri("http://uri.com");
        redirectUris.getRedirectUri().add(redirectUri);

        OrcidClient client1 = new OrcidClient();
        client1.setDisplayName("Name");
        client1.setRedirectUris(redirectUris);
        client1.setShortDescription("Description");
        client1.setType(ClientType.UPDATER);
        client1.setWebsite("http://site.com");

        // Add one client
        try {
            orcidClientGroupManager.createAndPersistClientProfile(createdGroup.getGroupOrcid(), client1);
        } catch (OrcidClientGroupManagementException e) {
            fail();
        }

        OrcidClient client2 = new OrcidClient();
        client2.setDisplayName("Name");
        client2.setRedirectUris(redirectUris);
        client2.setShortDescription("Description");
        client2.setType(ClientType.UPDATER);
        client2.setWebsite("http://site.com");

        // Add other client should fail
        try {
            orcidClientGroupManager.createAndPersistClientProfile(createdGroup.getGroupOrcid(), client2);
            fail();
        } catch (OrcidClientGroupManagementException e) {
        }
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddCreatorToBasicGroup() {
        final OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupName("Elsevier");
        group.setEmail("basic-member@elsevier.com" + System.currentTimeMillis());            
        group.setType(MemberType.PREMIUM);
        
        OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
            }
        });

        RedirectUris redirectUris = new RedirectUris();
        RedirectUri redirectUri = new RedirectUri("http://uri.com");
        redirectUris.getRedirectUri().add(redirectUri);

        OrcidClient client1 = new OrcidClient();
        client1.setDisplayName("Name");
        client1.setRedirectUris(redirectUris);
        client1.setShortDescription("Description");
        client1.setType(ClientType.CREATOR);
        client1.setWebsite("http://site.com");

        // Add one creator client to a basic group should fail
        try {
            OrcidClient orcidClient = orcidClientGroupManager.createAndPersistClientProfile(createdGroup.getGroupOrcid(), client1);
            assertEquals(ClientType.UPDATER, orcidClient.getType());
        } catch (OrcidClientGroupManagementException e) {

        }
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddMoreThanOneClientToBasicInstitutionGroup() {
        final OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupName("Elsevier");
        group.setEmail("basic-institution-member@elsevier.com" + System.currentTimeMillis());
        group.setType(MemberType.BASIC_INSTITUTION);
        
        OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
            }
        });

        RedirectUris redirectUris = new RedirectUris();
        RedirectUri redirectUri = new RedirectUri("http://uri.com");
        redirectUris.getRedirectUri().add(redirectUri);

        OrcidClient client1 = new OrcidClient();
        client1.setDisplayName("Name");
        client1.setRedirectUris(redirectUris);
        client1.setShortDescription("Description");
        client1.setType(ClientType.CREATOR);
        client1.setWebsite("http://site.com");

        // Add one client
        try {
            orcidClientGroupManager.createAndPersistClientProfile(createdGroup.getGroupOrcid(), client1);
        } catch (OrcidClientGroupManagementException e) {
            fail();
        }

        OrcidClient client2 = new OrcidClient();
        client2.setDisplayName("Name");
        client2.setRedirectUris(redirectUris);
        client2.setShortDescription("Description");
        client2.setType(ClientType.CREATOR);
        client2.setWebsite("http://site.com");

        // Add other client should fail
        try {
            orcidClientGroupManager.createAndPersistClientProfile(createdGroup.getGroupOrcid(), client2);
            fail();
        } catch (OrcidClientGroupManagementException e) {
        }
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddUpdaterToBasicInstitutionGroup() {
        final OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupName("Elsevier");
        group.setEmail("basic-institution-member@elsevier.com" + System.currentTimeMillis());
        group.setType(MemberType.BASIC_INSTITUTION);
        
        OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
            }
        });

        RedirectUris redirectUris = new RedirectUris();
        RedirectUri redirectUri = new RedirectUri("http://uri.com");
        redirectUris.getRedirectUri().add(redirectUri);

        OrcidClient client1 = new OrcidClient();
        client1.setDisplayName("Name");
        client1.setRedirectUris(redirectUris);
        client1.setShortDescription("Description");
        client1.setType(ClientType.UPDATER);
        client1.setWebsite("http://site.com");

        // Add one creator client to a basic group should fail
        try {
            OrcidClient orcidClient = orcidClientGroupManager.createAndPersistClientProfile(createdGroup.getGroupOrcid(), client1);
            assertEquals(orcidClient.getType(), ClientType.CREATOR);
        } catch (OrcidClientGroupManagementException e) {

        }
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddCreatorToPremiumGroup() {
        final OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupName("Elsevier");
        group.setEmail("premium-member@elsevier.com" + System.currentTimeMillis());
        group.setType(MemberType.PREMIUM);
        
        OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
            }
        });

        RedirectUris redirectUris = new RedirectUris();
        RedirectUri redirectUri = new RedirectUri("http://uri.com");
        redirectUris.getRedirectUri().add(redirectUri);

        OrcidClient client1 = new OrcidClient();
        client1.setDisplayName("Name");
        client1.setRedirectUris(redirectUris);
        client1.setShortDescription("Description");
        client1.setType(ClientType.CREATOR);
        client1.setWebsite("http://site.com");

        // Add one creator client to a premium group should fail
        try {
            OrcidClient orcidClient = orcidClientGroupManager.createAndPersistClientProfile(createdGroup.getGroupOrcid(), client1);
            assertEquals(orcidClient.getType(), ClientType.PREMIUM_UPDATER);
        } catch (OrcidClientGroupManagementException e) {

        }
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddUpdaterToPremiumInstitutionGroup() {
        final OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupName("Elsevier");
        group.setEmail("premium-institution-member@elsevier.com" + System.currentTimeMillis());
        group.setType(MemberType.PREMIUM_INSTITUTION);
        
        OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
            }
        });

        RedirectUris redirectUris = new RedirectUris();
        RedirectUri redirectUri = new RedirectUri("http://uri.com");
        redirectUris.getRedirectUri().add(redirectUri);

        OrcidClient client1 = new OrcidClient();
        client1.setDisplayName("Name");
        client1.setRedirectUris(redirectUris);
        client1.setShortDescription("Description");
        client1.setType(ClientType.UPDATER);
        client1.setWebsite("http://site.com");

        // Add one creator client to a premium group should fail
        try {
            OrcidClient orcidClient = orcidClientGroupManager.createAndPersistClientProfile(createdGroup.getGroupOrcid(), client1);
            assertEquals(orcidClient.getType(), ClientType.PREMIUM_CREATOR);
        } catch (OrcidClientGroupManagementException e) {

        }
    }
    
    @Test
    public void testUpdateGroup() {
        final OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group);
            }
        });
        
        assertNotNull(createdGroup);
        assertEquals(MemberType.PREMIUM, createdGroup.getType());
        
        Set<String> premiumScopes = ClientType.premiumUpdaterScopes();
        
        //Get existing clients
        List<ClientDetailsEntity> clients = clientDetailsManager.findByGroupId(createdGroup.getGroupOrcid());
        assertEquals(2, clients.size());
        for(ClientDetailsEntity clientDetails : clients) {
            assertEquals(ClientType.PREMIUM_UPDATER, clientDetails.getClientType());
            assertTrue(clientDetails.isScoped());
            assertEquals(premiumScopes.size(), clientDetails.getScope().size());
        }

        //Update the group type
        createdGroup.setType(MemberType.PREMIUM_INSTITUTION);
        
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                orcidClientGroupManager.updateGroup(createdGroup);
            }
        });        
        
        Set<String> premiumInstitutionScopes = ClientType.premiumCreatorScopes();
        
        clients = clientDetailsManager.findByGroupId(createdGroup.getGroupOrcid());
        assertEquals(2, clients.size());
        for(ClientDetailsEntity clientDetails : clients) {
            assertEquals(ClientType.PREMIUM_CREATOR, clientDetails.getClientType());
            assertTrue(clientDetails.isScoped());
            assertEquals(premiumInstitutionScopes.size(), clientDetails.getScope().size());
        }                
    }

}