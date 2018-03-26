package org.orcid.frontend.web.controllers;

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.pojo.ajaxForm.Checkbox;
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
public class ClientsControllerTest extends BaseControllerTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml");

    @Resource
    private ClientsController controller;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Override
    protected Authentication getAuthentication() {
        OrcidProfileUserDetails details = new OrcidProfileUserDetails("5555-5555-5555-5558", "5555-5555-5555-5558@user.com",
                "e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("5555-5555-5555-5558", null,
                Arrays.asList(OrcidWebRole.ROLE_PREMIUM_INSTITUTION));
        auth.setDetails(details);
        return auth;
    }

    @Test
    public void emptyClientTest() {
        Client client = controller.getEmptyClient();
        client = controller.createClient(client);
        assertNotNull(client);
        List<String> errors = client.getErrors();
        assertEquals(4, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.display_name.empty")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.website.empty")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.short_description.empty")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));
    }

    @Test
    public void testInvalidName() {
        Client client = controller.getEmptyClient();
        client.setRedirectUris(new ArrayList<RedirectUri>());
        client.setDisplayName(Text.valueOf("This is a <a>invalid</a> name"));
        client.setShortDescription(Text.valueOf("This is a valid description"));
        client.setWebsite(Text.valueOf("http://www.orcid.org"));
        client = controller.createClient(client);
        assertNotNull(client);
        assertEquals(1, client.getErrors().size());
        assertEquals(controller.getMessage("manage.developer_tools.group.error.display_name.html"), client.getErrors().get(0));
    }

    @Test
    public void testInvalidDescription() {
        Client client = controller.getEmptyClient();
        client.setRedirectUris(new ArrayList<RedirectUri>());
        client.setDisplayName(Text.valueOf("This is a valid name"));
        client.setShortDescription(Text.valueOf("This is a <a>invalid</a> description"));
        client.setWebsite(Text.valueOf("http://www.orcid.org"));
        client = controller.createClient(client);
        assertNotNull(client);
        assertEquals(1, client.getErrors().size());
        assertEquals(controller.getMessage("manage.developer_tools.group.error.short_description.html"), client.getErrors().get(0));
    }

    @Test
    public void testInvalidWebsite() {
        Client client = controller.getEmptyClient();
        client.setRedirectUris(new ArrayList<RedirectUri>());
        client.setDisplayName(Text.valueOf("This is a valid name"));
        client.setShortDescription(Text.valueOf("This is a valid description"));
        client.setWebsite(Text.valueOf("http:://orcid.org"));
        client = controller.createClient(client);
        assertNotNull(client);
        assertEquals(1, client.getErrors().size());
        assertEquals(controller.getMessage("common.invalid_url"), client.getErrors().get(0));
    }

    @Test
    public void createInvalidClientTest() {
        // Test invalid fields
        Client client = controller.getEmptyClient();
        String _151chars = RandomStringUtils.randomAlphanumeric(151);
        client.setDisplayName(Text.valueOf(_151chars));
        client.setShortDescription(Text.valueOf("description"));
        client.setWebsite(Text.valueOf("http://site.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(Text.valueOf(""));
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);
               
        client = controller.createClient(client);
        List<String> errors = client.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.display_name.150")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));
        
        // Test invalid redirect uris
        client = controller.getEmptyClient();
        client.setDisplayName(Text.valueOf("Name"));
        client.setShortDescription(Text.valueOf("Description"));
        client.setWebsite(Text.valueOf("http://mysite.com"));

        redirectUris = new ArrayList<RedirectUri>();
        one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(new Text());
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);
        client = controller.createClient(client);
        errors = client.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));

        RedirectUri two = new RedirectUri();
        two.setType(Text.valueOf("grant-read-wizard"));
        two.setValue(new Text());
        redirectUris = new ArrayList<RedirectUri>();
        redirectUris.add(two);
        client.setRedirectUris(redirectUris);

        client = controller.createClient(client);
        errors = client.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.empty_scopes")));
    }

    @Test
    public void editInvalidClientTest() {
        // Test invalid fields
        Client client = controller.getEmptyClient();
        String _151chars = RandomStringUtils.randomAlphanumeric(151);
        client.setDisplayName(Text.valueOf(_151chars));
        client.setShortDescription(Text.valueOf("description"));
        client.setWebsite(Text.valueOf("http://site.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(Text.valueOf(""));
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);
        
        client = controller.editClient(client);
        List<String> errors = client.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.display_name.150")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));
        
        // Test invalid redirect uris
        client = controller.getEmptyClient();
        client.setDisplayName(Text.valueOf("Name"));
        client.setShortDescription(Text.valueOf("Description"));
        client.setWebsite(Text.valueOf("http://mysite.com"));

        redirectUris = new ArrayList<RedirectUri>();
        one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(new Text());
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);
        client = controller.editClient(client);
        errors = client.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));

        RedirectUri two = new RedirectUri();
        two.setType(Text.valueOf("grant-read-wizard"));
        two.setValue(new Text());
        redirectUris = new ArrayList<RedirectUri>();
        redirectUris.add(two);
        client.setRedirectUris(redirectUris);

        client = controller.editClient(client);
        errors = client.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.empty_scopes")));
    }

    @Test
    public void getClientsTest() {
        List<Client> clients = controller.getClients();
        assertNotNull(clients);
        assertEquals(2, clients.size());
        Client client1 = clients.get(0);
        assertEquals("APP-5555555555555555", client1.getClientId().getValue());

        Client client2 = clients.get(1);
        assertEquals("APP-5555555555555556", client2.getClientId().getValue());
    }

    @Test
    public void addClientTest() {
        List<Client> clients = controller.getClients();
        int clientsSoFar = clients.size();
        assertTrue(clientsSoFar > 0);

        Client client = new Client();
        client.setAllowAutoDeprecate(Checkbox.valueOf(true));
        client.setClientId(Text.valueOf("XXXXXX"));
        client.setDisplayName(Text.valueOf("My client name"));
        client.setMemberId(Text.valueOf("0000-0000-0000-0000"));
        client.setMemberName(Text.valueOf("My member name"));
        client.setPersistentTokenEnabled(Checkbox.valueOf(true));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri r1 = new RedirectUri();
        r1.setValue(Text.valueOf("http://orcid.org"));
        r1.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        redirectUris.add(r1);
        client.setRedirectUris(redirectUris);
        client.setShortDescription(Text.valueOf("My short description"));
        client.setWebsite(Text.valueOf("http://orcid.org"));
        client = controller.createClient(client);
        assertTrue(client.getErrors().isEmpty());
        assertNotNull(client);
        assertNotNull(client.getClientId());
        assertTrue(client.getClientId().getValue().startsWith("APP-"));
        assertFalse(PojoUtil.isEmpty(client.getClientSecret()));

        clients = controller.getClients();
        assertTrue(clients.size() > clientsSoFar);
        boolean found = false;
        for (Client c : clients) {
            if (client.getClientId().getValue().equals(c.getClientId().getValue())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void editClientTest() {
        List<Client> clients = controller.getClients();
        int clientsSoFar = clients.size();
        assertNotNull(clients);
        assertEquals(2, clients.size());
        Client client = clients.get(0);
        assertEquals("APP-5555555555555555", client.getClientId().getValue());

        String random = RandomStringUtils.randomAlphanumeric(20);

        client.getDisplayName().setValue("Source Client 1 Updated");
        client.getShortDescription().setValue("Updated client description");
        client.getWebsite().setValue("http://orcid.org/" + random);
        RedirectUri newRedirectUri = new RedirectUri();
        newRedirectUri.setValue(Text.valueOf("http://orcid.org/" + random));
        newRedirectUri.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        client.getRedirectUris().add(newRedirectUri);

        client = controller.editClient(client);
        assertTrue(client.getErrors().isEmpty());

        clients = controller.getClients();
        assertTrue(clients.size() == clientsSoFar);
        boolean found = false;
        for (Client c : clients) {
            if (client.getClientId().getValue().equals(c.getClientId().getValue())) {
                assertEquals("Source Client 1 Updated", client.getDisplayName().getValue());
                assertEquals("Updated client description", client.getShortDescription().getValue());
                assertEquals("http://orcid.org/" + random, client.getWebsite().getValue());
                boolean rUriFound = false;
                for(RedirectUri rUri : client.getRedirectUris()) {
                    if(rUri.getValue().getValue().equals("http://orcid.org/" + random)) {
                        assertEquals(RedirectUriType.DEFAULT.value(), rUri.getType().getValue());
                        rUriFound = true;
                    }
                }
                assertTrue(rUriFound);
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}