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
package org.orcid.core.oauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 15/03/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ClientDetailsManagerTest extends DBUnitTest {

    @Resource
    private ClientDetailsManager clientDetailsManager;

    private static String CLIENT_NAME = "the name";
    private static String CLIENT_DESCRIPTION = "the description";
    private static String CLIENT_WEBSITE = "http://website.com";
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    @Rollback
    @Transactional
    public void testLoadClientByClientId() throws Exception {
        List<ClientDetailsEntity> all = clientDetailsManager.getAll();
        assertEquals(5, all.size());
        for (ClientDetailsEntity clientDetailsEntity : all) {
            ClientDetails clientDetails = clientDetailsManager.loadClientByClientId(clientDetailsEntity.getId());
            assertNotNull(clientDetails);
            checkClientDetails(clientDetails);
        }
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateClientDetailsFromStrings() throws Exception {
        String clientId = "4444-4444-4444-4499";
        String clientSecret = "Zq7ldGbUvzbEMNysSbbUq4dLRrxEUApgdcofn8xDke4=";
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

        ClientDetailsEntity clientDetails = clientDetailsManager.createClientDetails("4444-4444-4444-4499", CLIENT_NAME, CLIENT_DESCRIPTION, CLIENT_WEBSITE, clientId, clientSecret, clientScopes, clientResourceIds,
                clientAuthorizedGrantTypes, clientRegisteredRedirectUris, clientGrantedAuthorities);
        assertNotNull(clientDetails);
        checkClientDetails(clientDetails);
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

        ClientDetailsEntity clientDetails = clientDetailsManager.createClientDetails("4444-4444-4444-4446", CLIENT_NAME, CLIENT_DESCRIPTION, CLIENT_WEBSITE, clientScopes, clientResourceIds, clientAuthorizedGrantTypes,
                clientRegisteredRedirectUris, clientGrantedAuthorities);
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

        clientDetailsManager.createClientDetails("8888-9999-9999-9999", CLIENT_NAME, CLIENT_DESCRIPTION, CLIENT_WEBSITE, clientScopes, clientResourceIds, clientAuthorizedGrantTypes, clientRegisteredRedirectUris,
                clientGrantedAuthorities);
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteClientDetail() throws Exception {
        List<ClientDetailsEntity> all = clientDetailsManager.getAll();
        assertEquals(5, all.size());
        for (ClientDetailsEntity clientDetailsEntity : all) {
            clientDetailsManager.deleteClientDetail(clientDetailsEntity.getId());
        }
        all = clientDetailsManager.getAll();
        assertEquals(0, all.size());
    }

    private void checkClientDetails(ClientDetailsEntity clientDetails) {
        assertNotNull(clientDetails);
        assertEquals(clientDetails.getClientDescription(), CLIENT_DESCRIPTION);
        assertEquals(clientDetails.getClientName(), CLIENT_NAME);
        checkClientDetails((ClientDetails)clientDetails);
    }

    private void checkClientDetails(ClientDetails clientDetails) {
        String clientId = clientDetails.getClientId();
        assertNotNull(clientId);
        Set<String> registeredRedirectUris = clientDetails.getRegisteredRedirectUri();
        assertNotNull(registeredRedirectUris);
        assertEquals(1, registeredRedirectUris.size());
        Collection<GrantedAuthority> authorities = clientDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        Set<String> authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        assertNotNull(authorizedGrantTypes);
        assertEquals(3, authorizedGrantTypes.size());
        String clientSecret = clientDetails.getClientSecret();
        assertNotNull(clientSecret);
        Set<String> resourceIds = clientDetails.getResourceIds();
        assertNotNull(resourceIds);
        assertEquals(1, resourceIds.size());
        Set<String> scope = clientDetails.getScope();
        assertNotNull(scope);
        int expectedNumberOfScopes = "4444-4444-4444-4445".equals(clientDetails.getClientId()) ? 5 : 1;
        assertEquals(expectedNumberOfScopes, scope.size());
    }
}
