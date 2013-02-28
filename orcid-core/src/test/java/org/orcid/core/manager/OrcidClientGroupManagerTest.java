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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.OrcidProfileManagerImpl;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidClientGroupManagerTest extends BaseTest {

    public static final String CLIENT_GROUP = "/orcid-client-group-request.xml";

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Mock
    private OrcidIndexManager orcidIndexManager;

    @Before
    public void initMocks() throws Exception {

        OrcidProfileManagerImpl orcidProfileManagerImpl = getTargetObject(orcidProfileManager, OrcidProfileManagerImpl.class);
        orcidProfileManagerImpl.setOrcidIndexManager(orcidIndexManager);
    }

    @Test
    @Rollback(true)
    @Transactional
    public void testCreateOrcidClientGroup() {
        OrcidClientGroup group = OrcidClientGroup.unmarshall(getClass().getResourceAsStream(CLIENT_GROUP));

        OrcidClientGroup createdGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(group, ClientType.UPDATER);
        assertNotNull(createdGroup);

        assertEquals("Elsevier", createdGroup.getGroupName());
        assertEquals("orcid-admin@elsevier.com", createdGroup.getEmail());
        assertNotNull(createdGroup.getGroupOrcid());
        List<OrcidClient> createdClients = createdGroup.getOrcidClient();
        assertNotNull(createdClients);
        assertEquals(2, createdClients.size());
        Map<String, OrcidClient> createdClientsMappedByName = new HashMap<String, OrcidClient>();
        for (OrcidClient createdClient : createdClients) {
            assertNotNull(createdClient.getClientId());
            assertNotNull(createdClient.getClientSecret());
            createdClientsMappedByName.put(createdClient.getDisplayName(), createdClient);
        }
        OrcidClient complexityClient = createdClientsMappedByName.get("Ecological Complexity");
        assertNotNull(complexityClient);
        assertEquals("http://www.journals.elsevier.com/ecological-complexity", complexityClient.getWebsite());
        assertEquals("An International Journal on Biocomplexity in the Environment and Theoretical Ecology", complexityClient.getShortDescription());        
        List<RedirectUri> createdRedirectUris = complexityClient.getRedirectUris().getRedirectUri();
        assertNotNull(createdRedirectUris);
        assertEquals(2, createdRedirectUris.size());
        assertEquals("http://www.journals.elsevier.com/ecological-complexity/orcid-callback", createdRedirectUris.get(0).getValue());
        List<ScopePathType> scopesForRedirect = createdRedirectUris.get(0).getScope();
        assertTrue(scopesForRedirect.size()==2);
        assertTrue(scopesForRedirect.contains(ScopePathType.ORCID_PROFILE_CREATE) && scopesForRedirect.contains(ScopePathType.ORCID_BIO_READ_LIMITED));        
        assertEquals("https://developers.google.com/oauthplayground", createdRedirectUris.get(1).getValue());
        assertNull(createdRedirectUris.get(1).getScope());
        // Look up client details directly to check scopes
        ClientDetailsEntity complexityEntity = clientDetailsDao.find(complexityClient.getClientId());
        Set<String> clientScopeTypes = complexityEntity.getScope();
        assertNotNull(clientScopeTypes);
        assertTrue(clientScopeTypes.contains("/orcid-bio/update"));
        assertTrue(clientScopeTypes.contains("/orcid-bio/read-limited"));
        assertTrue(clientScopeTypes.contains("/orcid-works/read-limited"));
        assertFalse(clientScopeTypes.contains("/orcid-profile/create"));
        assertTrue(clientScopeTypes.contains("/authenticate"));
    }

    @Test
    @Rollback(true)
    @Transactional
    public void testCreateOrcidCreatorClientGroup() {
        OrcidClientGroup group = OrcidClientGroup.unmarshall(getClass().getResourceAsStream(CLIENT_GROUP));

        OrcidClientGroup createdGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(group, ClientType.CREATOR);
        assertNotNull(createdGroup);

        assertEquals("Elsevier", createdGroup.getGroupName());
        assertEquals("orcid-admin@elsevier.com", createdGroup.getEmail());
        assertNotNull(createdGroup.getGroupOrcid());
        List<OrcidClient> createdClients = createdGroup.getOrcidClient();
        assertNotNull(createdClients);
        assertEquals(2, createdClients.size());
        Map<String, OrcidClient> createdClientsMappedByName = new HashMap<String, OrcidClient>();
        for (OrcidClient createdClient : createdClients) {
            assertNotNull(createdClient.getClientId());
            assertNotNull(createdClient.getClientSecret());
            createdClientsMappedByName.put(createdClient.getDisplayName(), createdClient);
        }
        OrcidClient complexityClient = createdClientsMappedByName.get("Ecological Complexity");
        assertNotNull(complexityClient);
        assertEquals("http://www.journals.elsevier.com/ecological-complexity", complexityClient.getWebsite());
        assertEquals("An International Journal on Biocomplexity in the Environment and Theoretical Ecology", complexityClient.getShortDescription());
        List<RedirectUri> createdRedirectUris = complexityClient.getRedirectUris().getRedirectUri();
        assertNotNull(createdRedirectUris);
        assertEquals(2, createdRedirectUris.size());
        assertEquals("http://www.journals.elsevier.com/ecological-complexity/orcid-callback", createdRedirectUris.get(0).getValue());
        assertEquals("https://developers.google.com/oauthplayground", createdRedirectUris.get(1).getValue());
        // Look up client details directly to check scopes
        ClientDetailsEntity complexityEntity = clientDetailsDao.find(complexityClient.getClientId());
        Set<String> clientScopeTypes = complexityEntity.getScope();
        assertNotNull(clientScopeTypes);
        assertTrue(clientScopeTypes.contains("/orcid-profile/create"));
        assertTrue(clientScopeTypes.contains("/orcid-bio/read-limited"));
        assertTrue(clientScopeTypes.contains("/orcid-works/read-limited"));
    }

    @Test   
    @Rollback(true)
    @Transactional
    public void testUpdateOrcidClientGroup() {
        final OrcidClientGroup group = OrcidClientGroup.unmarshall(getClass().getResourceAsStream(CLIENT_GROUP));
        OrcidClientGroup createdGroup = transactionTemplate.execute(new TransactionCallback<OrcidClientGroup>() {
            public OrcidClientGroup doInTransaction(TransactionStatus status) {
                return orcidClientGroupManager.createOrUpdateOrcidClientGroup(group, ClientType.UPDATER);
            }
        });

        createdGroup.setEmail("admin@somethingelse.com");
        for (OrcidClient createdClient : createdGroup.getOrcidClient()) {
            if ("Ecological Complexity".equals(createdClient.getDisplayName())) {
                createdClient.setWebsite("wwww.ecologicalcomplexity.com");
            }
        }
        OrcidClientGroup updatedGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(createdGroup, ClientType.UPDATER);

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
        assertEquals(2, updatedRedirectUris.size());
        Collections.sort(updatedRedirectUris, new Comparator<RedirectUri>() {
            public int compare(RedirectUri redirectUri1, RedirectUri redirectUri2) {
                return ((String) redirectUri1.getValue()).compareToIgnoreCase((String)redirectUri1.getValue());
            }
        });
        
        assertEquals("http://www.journals.elsevier.com/ecological-complexity/orcid-callback", updatedRedirectUris.get(0).getValue());
        List<ScopePathType> scopesForRedirect  = updatedRedirectUris.get(0).getScope();
        assertTrue(scopesForRedirect.size()==2);
        assertTrue(scopesForRedirect.contains(ScopePathType.ORCID_PROFILE_CREATE) && scopesForRedirect.contains(ScopePathType.ORCID_BIO_READ_LIMITED));        
        assertEquals("https://developers.google.com/oauthplayground", updatedRedirectUris.get(1).getValue());
        assertNull(updatedRedirectUris.get(1).getScope());
    }

}
