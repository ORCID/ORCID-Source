package org.orcid.core.oauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 15/03/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class ClientDetailsManagerTest extends DBUnitTest {

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    private static String CLIENT_NAME = "the name";
    private static String CLIENT_DESCRIPTION = "the description";
    private static String CLIENT_WEBSITE = "http://website.com";

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/ClientDetailsEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @Test
    @Rollback
    @Transactional
    public void testLoadClientByClientId() throws Exception {
        List<ClientDetailsEntity> all = clientDetailsManager.getAll();
        String[] clientExceptionList = new String[] { "APP-5555555555555555", "APP-5555555555555556", "APP-5555555555555557", "APP-5555555555555558",
                "APP-6666666666666666" };     
        assertEquals(12, all.size());
        for (ClientDetailsEntity clientDetailsEntity : all) {
            ClientDetails clientDetails = clientDetailsManager.loadClientByClientId(clientDetailsEntity.getId());
            assertNotNull(clientDetails);
            boolean exceptionClients = Arrays.stream(clientExceptionList).anyMatch(x -> x.equals(clientDetailsEntity.getId()));
            if (!exceptionClients) {
                checkClientDetails(clientDetails);
            }
        }
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateClientDetailsWithRandomSecret() throws Exception {
        Set<String> clientScopes = new HashSet<String>();
        clientScopes.add("/orcid-profile/create");
        Set<String> clientResourceIds = new HashSet<String>();
        clientResourceIds.add("orcid-t2-api");
        Set<String> clientAuthorizedGrantTypes = new HashSet<String>();
        clientAuthorizedGrantTypes.add("client_credentials");
        clientAuthorizedGrantTypes.add("authorization_code");
        clientAuthorizedGrantTypes.add("refresh_token");
        Set<RedirectUri> clientRegisteredRedirectUris = new HashSet<RedirectUri>();
        clientRegisteredRedirectUris.add(new RedirectUri("http://www.google.com/"));
        List<String> clientGrantedAuthorities = new ArrayList<String>();
        clientGrantedAuthorities.add("ROLE_ADMIN");

        ClientDetailsEntity clientDetails = clientDetailsManager.createClientDetails("4444-4444-4444-4446", CLIENT_NAME, CLIENT_DESCRIPTION, null, CLIENT_WEBSITE,
                ClientType.CREATOR, clientScopes, clientResourceIds, clientAuthorizedGrantTypes, clientRegisteredRedirectUris, clientGrantedAuthorities, true);
        assertNotNull(clientDetails);
        checkClientDetails(clientDetails);
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback
    @Transactional
    public void testCreateClientDetailsWithNonExistentOrcid() throws Exception {
        Set<String> clientScopes = new HashSet<String>();
        clientScopes.add("/orcid-profile/create");
        Set<String> clientResourceIds = new HashSet<String>();
        clientResourceIds.add("orcid-t2-api");
        Set<String> clientAuthorizedGrantTypes = new HashSet<String>();
        clientAuthorizedGrantTypes.add("client_credentials");
        clientAuthorizedGrantTypes.add("authorization_code");
        clientAuthorizedGrantTypes.add("refresh_token");
        Set<RedirectUri> clientRegisteredRedirectUris = new HashSet<RedirectUri>();
        clientRegisteredRedirectUris.add(new RedirectUri("http://www.google.com/"));
        List<String> clientGrantedAuthorities = new ArrayList<String>();
        clientGrantedAuthorities.add("ROLE_ADMIN");

        clientDetailsManager.createClientDetails("8888-9999-9999-9999", CLIENT_NAME, CLIENT_DESCRIPTION, null, CLIENT_WEBSITE, ClientType.CREATOR, clientScopes,
                clientResourceIds, clientAuthorizedGrantTypes, clientRegisteredRedirectUris, clientGrantedAuthorities, true);
    }    

    @Test
    public void testAddScopesToClient() {
        ClientDetailsEntity clientDetails = clientDetailsDao.find("APP-6666666666666666");
        
        Set<String> scopes = new HashSet<>();
        scopes.add("some-scope");
        scopes.add("another-scope");
        clientDetailsManager.addScopesToClient(scopes, clientDetails);
        
        ClientDetailsEntity updated = clientDetailsDao.find("APP-6666666666666666");
        
        assertFalse(clientDetails.getClientScopes().size() == updated.getClientScopes().size());
        assertTrue(clientDetails.getClientScopes().size() == updated.getClientScopes().size() - 2);

        boolean foundFirstScope = false;
        boolean foundSecondScope = false;
        for (ClientScopeEntity clientScope : updated.getClientScopes()) {
            if ("some-scope".equals(clientScope.getScopeType())) {
                foundFirstScope = true;
            } else if ("another-scope".equals(clientScope.getScopeType())) {
                foundSecondScope = true;
            } 
        }
        
        assertTrue(foundFirstScope);
        assertTrue(foundSecondScope);
    }

    private void checkClientDetails(ClientDetailsEntity clientDetails) {
        assertNotNull(clientDetails);
        assertEquals(clientDetails.getClientDescription(), CLIENT_DESCRIPTION);
        assertEquals(clientDetails.getClientName(), CLIENT_NAME);
        checkClientDetails((ClientDetails) clientDetails);
    }

    private void checkClientDetails(ClientDetails clientDetails) {
        String clientId = clientDetails.getClientId();
        assertNotNull(clientId);
        Set<String> registeredRedirectUris = clientDetails.getRegisteredRedirectUri();
        assertNotNull(registeredRedirectUris);
        if (clientDetails.getClientId().equals("4444-4444-4444-4445") || clientDetails.getClientId().equals("4444-4444-4444-4498")) {
            assertEquals(2, registeredRedirectUris.size());
        } else {
            assertEquals(1, registeredRedirectUris.size());
        }
        
        Collection<GrantedAuthority> authorities = clientDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        Set<String> authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        assertNotNull(authorizedGrantTypes);
        if (clientDetails.getClientId().equals("4444-4444-4444-4498")) {
            assertEquals(2, authorizedGrantTypes.size());
        } else if (!clientDetails.getClientId().equals("APP-1234567898765432")) {
            assertEquals(3, authorizedGrantTypes.size());
        }
        
        String clientSecret = clientDetails.getClientSecret();
        assertNotNull(clientSecret);
        Set<String> resourceIds = clientDetails.getResourceIds();
        assertNotNull(resourceIds);
        if (!clientDetails.getClientId().equals("4444-4444-4444-4498") && !clientDetails.getClientId().equals("APP-1234567898765432")) {
            assertEquals(1, resourceIds.size());
        }
        Set<String> scope = clientDetails.getScope();
        assertNotNull(scope);
        int expectedNumberOfScopes = "4444-4444-4444-4445".equals(clientDetails.getClientId()) ? 23 : "4444-4444-4444-4443".equals(clientDetails.getClientId()) ? 2 : "APP-1234567898765432".equals(clientDetails.getClientId()) ? 0 : 1;
        assertEquals(expectedNumberOfScopes, scope.size());
    }
}
