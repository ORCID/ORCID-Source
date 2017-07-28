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
package org.orcid.frontend.web.controllers;

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class DeveloperToolsControllerTest extends BaseControllerTest {

    @Resource
    DeveloperToolsController developerToolsController;

    @Resource
    private ProfileDao profileDao;

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Before
    public void init() {
        assertNotNull(developerToolsController);
        assertNotNull(profileDao);
    }

    @Override
    protected Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");

        OrcidProfileUserDetails details = null;
        if (orcidProfile.getType() != null) {
            OrcidType orcidType = OrcidType.fromValue(orcidProfile.getType().value());
            details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(),
                    orcidProfile.getOrcidBio().getContactDetails().getEmail().get(0).getValue(),
                    orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent(), orcidType, orcidProfile.getGroupType());
        } else {
            details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(),
                    orcidProfile.getOrcidBio().getContactDetails().getEmail().get(0).getValue(),
                    orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent());
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("4444-4444-4444-4442", details.getPassword(), getRole());
        auth.setDetails(details);
        return auth;
    }

    protected List<OrcidWebRole> getRole() {
        return Arrays.asList(OrcidWebRole.ROLE_USER);
    }

    @Test
    public void testCrossSiteScriptingOnClientName() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("<script>alert('name')</script>"));
        client.setShortDescription(Text.valueOf("This is a short description"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals(developerToolsController.getMessage("manage.developer_tools.name.html"), result.getErrors().get(0));
    }

    @Test
    public void testCrossSiteScriptingOnClientDescription() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a test to show that html is <script>alert('name')</script> throws an error"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals(developerToolsController.getMessage("manage.developer_tools.description.html"), result.getErrors().get(0));
    }

    @Test    
    public void testClientValidation() throws Exception {
        // Test empty title
        Client client = new Client();
        client.setShortDescription(Text.valueOf("This is a description"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0), developerToolsController.getMessage("manage.developer_tools.name_not_empty"));

        // Test empty description
        client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setWebsite(Text.valueOf("http://client.com"));
        redirectUris = new ArrayList<RedirectUri>();
        rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        result = developerToolsController.createClient(client);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0), developerToolsController.getMessage("manage.developer_tools.description_not_empty"));

        // Test empty website
        client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a description"));
        redirectUris = new ArrayList<RedirectUri>();
        rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        result = developerToolsController.createClient(client);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0), developerToolsController.getMessage("manage.developer_tools.website_not_empty"));

        // Test empty redirect uris
        client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a description"));
        client.setWebsite(Text.valueOf("http://client.com"));
        result = developerToolsController.createClient(client);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0), developerToolsController.getMessage("manage.developer_tools.at_least_one"));
    }

    @Test    
    public void testGenerateClient() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a test"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        assertNotNull(result);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 0);
        assertNotNull(result.getClientSecret());
        assertFalse(PojoUtil.isEmpty(result.getClientSecret()));
        assertFalse(PojoUtil.isEmpty(result.getMemberId()));
        assertEquals("4444-4444-4444-4442", result.getMemberId().getValue());
    }

    @Test
    public void testUpdateClient() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a test"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        client = developerToolsController.createClient(client);
        assertNotNull(client);
        assertNotNull(client.getErrors());
        assertEquals(client.getErrors().size(), 0);
        Text clientSecret = client.getClientSecret();

        // Update values
        client.setDisplayName(Text.valueOf("Updated client name"));
        client.setShortDescription(Text.valueOf("Updated client description"));
        client.setWebsite(Text.valueOf("http://updated.com"));
        RedirectUri rUri2 = new RedirectUri();
        rUri2.setType(Text.valueOf("default"));
        rUri2.setValue(Text.valueOf("http://test2.com"));
        redirectUris.add(rUri2);
        client.setRedirectUris(redirectUris);
        Client updatedResult = developerToolsController.updateClient(client);
        assertNotNull(updatedResult);
        assertNotNull(updatedResult.getErrors());
        assertEquals(updatedResult.getErrors().size(), 0);

        Text updatedClientSecret = updatedResult.getClientSecret();

        assertEquals(updatedClientSecret.toString(), clientSecret.toString());
        assertEquals(updatedResult.getDisplayName().getValue(), "Updated client name");
        assertEquals(updatedResult.getShortDescription().getValue(), "Updated client description");
        assertEquals(updatedResult.getWebsite().getValue(), "http://updated.com");
        assertNotNull(updatedResult.getRedirectUris());
        assertEquals(updatedResult.getRedirectUris().size(), 2);
    }

    @Test
    public void testResetClientSecret() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a test"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        assertNotNull(result);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 0);
        Text clientSecret = result.getClientSecret();

        assertTrue(developerToolsController.resetClientSecret(result.getClientId().getValue()));

        ClientDetailsEntity clientDetails = clientDetailsDao.findByClientId(result.getClientId().getValue(), System.currentTimeMillis());

        assertEquals(result.getDisplayName().getValue(), clientDetails.getClientName());
        assertEquals(result.getShortDescription().getValue(), clientDetails.getClientDescription());
        assertEquals(result.getClientId().getValue(), clientDetails.getClientId());
        assertEquals(result.getWebsite().getValue(), clientDetails.getClientWebsite());
        Set<ClientSecretEntity> clientSecrets = clientDetails.getClientSecrets();
        assertNotNull(clientSecrets);
        assertEquals(2, clientSecrets.size());
        for (ClientSecretEntity clientSecretEntity : clientSecrets) {
            String secret = encryptionManager.decryptForInternalUse(clientSecretEntity.getClientSecret());
            if (!clientSecretEntity.isPrimary())
                assertEquals(clientSecret.getValue(), secret);
            else
                assertFalse(clientSecret.getValue().equals(secret));
        }
    }
}