package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    private static final String INVALID_ID = "0000-0000-0000-0000";
    private static final String MEMBER_ID = "0000-0000-0000-0001";

    HttpServletRequest requestMock = mock(HttpServletRequest.class);

    HttpServletResponse responseMock = mock(HttpServletResponse.class);

    @Mock
    private ProfileDao profileDaoReadOnlyMock;

    @Mock
    private ClientManager clientManagerMock;

    @Mock
    private OrcidSecurityManager orcidSecurityManagerMock;

    @InjectMocks
    private AdminController adminController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(profileDaoReadOnlyMock.getGroupType(eq(INVALID_ID))).thenReturn(null);
        when(profileDaoReadOnlyMock.getGroupType(eq(MEMBER_ID))).thenReturn("PREMIUM_INSTITUTION");
        when(orcidSecurityManagerMock.isAdmin()).thenReturn(true);
    }

    @Test(expected = IllegalAccessException.class)
    public void createClient_noAdminRequestTest() throws Exception {
        when(orcidSecurityManagerMock.isAdmin()).thenReturn(false);
        adminController.createClient(requestMock, responseMock, getClient());
    }

    @Test
    public void createClient_nullClientTest() throws Exception {
        Client c = adminController.createClient(requestMock, responseMock, null);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Client object cannot be null", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyMemberIdTest() throws Exception {
        Client c = getClient();
        c.setMemberId(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Member ID is requiered", c.getErrors().get(0));
    }

    @Test
    public void createClient_invalidMemberIdTest() throws Exception {
        Client c = getClient();
        c.setMemberId(Text.valueOf(INVALID_ID));
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Member with ID " + INVALID_ID + " does not exists", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyClientNameTest() throws Exception {
        Client c = getClient();
        c.setDisplayName(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Display name is requiered", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyDescriptionTest() throws Exception {
        Client c = getClient();
        c.setShortDescription(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Description is requiered", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyWebsiteTest() throws Exception {
        Client c = getClient();
        c.setWebsite(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Website is requiered", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyRedirecturisTest() throws Exception {
        Client c = getClient();
        c.getRedirectUris().remove(0);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Redirect URIs are requiered", c.getErrors().get(0));
    }

    @Test
    public void createClient_redirectUrisMissingTypeTest() throws Exception {
        Client c = getClient();
        c.getRedirectUris().get(0).setType(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Redirect uri type missing on redirect uri https://test.orcid.org/ruri", c.getErrors().get(0));
    }

    @Test
    public void createClientTest() throws IllegalAccessException {
        when(clientManagerMock.create(any())).thenAnswer(
                (Answer<org.orcid.jaxb.model.v3.release.client.Client>) invocation -> {
                    org.orcid.jaxb.model.v3.release.client.Client c = invocation.getArgument(0, org.orcid.jaxb.model.v3.release.client.Client.class);
                    // Mock the client secret to prevent a NPE
                    c.setDecryptedSecret("SECRET");
                    // Mock client type to prevent a NPE
                    c.setClientType(ClientType.PREMIUM_UPDATER);
                    return c;
                }
        );
        Client c = getClient();
        // Mock the client id to prevent a NPE
        c.setClientId(Text.valueOf("APP-0"));
        Client newClient = adminController.createClient(requestMock, responseMock, c);
        assertTrue(c.getErrors().isEmpty());
        assertFalse(PojoUtil.isEmpty(newClient.getClientId()));
        org.orcid.jaxb.model.v3.release.client.Client modelObject = newClient.toModelObject();
        verify(clientManagerMock, times(1)).create(any(org.orcid.jaxb.model.v3.release.client.Client.class));
    }

    private Client getClient() {
        Client client = new Client();
        client.setMemberId(Text.valueOf(MEMBER_ID));
        client.setDisplayName(Text.valueOf("Client name"));
        client.setShortDescription(Text.valueOf("Short description"));
        client.setWebsite(Text.valueOf("https://test.orcid.org/website"));
        List<RedirectUri> rUris = new ArrayList<>();
        RedirectUri rUri1 = new RedirectUri();
        rUri1.setValue(Text.valueOf("https://test.orcid.org/ruri"));
        rUri1.setType(Text.valueOf("default"));
        rUris.add(rUri1);
        client.setRedirectUris(rUris);
        return client;
    }
}
