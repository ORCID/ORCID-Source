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
package org.orcid.integration.blackbox.api.v2.release.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.notifications.NotificationsApiClientImpl;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.notification.permission_v2.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class NotificationsTest {

    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String testUser1OrcidId;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    @Resource(name = "notificationsClient_v2")
    private NotificationsApiClientImpl<org.orcid.jaxb.model.notification.permission_v2.NotificationPermission> notificationsClient;

    @Resource
    private OauthHelper oauthHelper;

    @Test
    public void testGetNotificationToken() throws JSONException {
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);
        assertNotNull(accessToken);
    }

    @Test
    public void createPermissionNotification() throws JSONException {
        NotificationPermission notification = unmarshallFromPath("/notification_2.0/samples/notification-permission-2.0.xml");
        notification.setPutCode(null);
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);

        ClientResponse response = notificationsClient.addPermissionNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String locationPath = response.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0/" + testUser1OrcidId + "/notification-permission/\\d+"));
    }

    @Test
    public void flagAsArchived() throws JSONException {
        NotificationPermission notification = unmarshallFromPath("/notification_2.0/samples/notification-permission-2.0.xml");
        notification.setPutCode(null);
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);

        ClientResponse postResponse = notificationsClient.addPermissionNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0/" + testUser1OrcidId + "/notification-permission/\\d+"));
        String putCodeString = locationPath.substring(locationPath.lastIndexOf('/') + 1);
        Long putCode = Long.valueOf(putCodeString);

        ClientResponse viewResponse = notificationsClient.viewPermissionNotificationXml(testUser1OrcidId, putCode, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), viewResponse.getStatus());
        NotificationPermission retrievedNotification = viewResponse.getEntity(NotificationPermission.class);
        assertNotNull(retrievedNotification);
        assertNull(retrievedNotification.getArchivedDate());

        ClientResponse archiveResponse = notificationsClient.flagAsArchivedPermissionNotificationXml(testUser1OrcidId, putCode, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), archiveResponse.getStatus());

        ClientResponse viewAfterArchiveResponse = notificationsClient.viewPermissionNotificationXml(testUser1OrcidId, putCode, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), viewAfterArchiveResponse.getStatus());
        NotificationPermission retrievedAfterArchiveNotification = viewAfterArchiveResponse.getEntity(NotificationPermission.class);
        assertNotNull(retrievedAfterArchiveNotification.getArchivedDate());
    }

    @Test
    public void createPermissionNotificationWithTrailingSpaceInAuthorizationUrl() throws JSONException {
        NotificationPermission notification = unmarshallFromPath("/notification_2.0/samples/notification-permission-2.0.xml");
        notification.setPutCode(null);
        AuthorizationUrl authUrl = notification.getAuthorizationUrl();
        authUrl.setUri(authUrl.getUri() + "    ");
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);

        ClientResponse response = notificationsClient.addPermissionNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNull(response.getLocation());
    }
    
    @Test
    public void createPermissionNotificationWithUnencodedSpaceInAuthorizationPath() throws JSONException {
        NotificationPermission notification = unmarshallFromPath("/notification_2.0/samples/notification-permission-2.0.xml");
        notification.setPutCode(null);
        AuthorizationUrl authUrl = notification.getAuthorizationUrl();
        authUrl.setUri(null);
        authUrl.setPath("/oauth/authorize?client_id=0000-0003-4223-0632&response_type=code&scope=/read-limited /activities/update&redirect_uri=https://developers.google.com/oauthplayground");
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);

        ClientResponse response = notificationsClient.addPermissionNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNull(response.getLocation());
    }

    @Test
    public void createPermissionNotificationWithBlankAuthorizationUri() throws JSONException {
        NotificationPermission notification = unmarshallFromPath("/notification_2.0/samples/notification-permission-2.0.xml");
        notification.setPutCode(null);
        AuthorizationUrl authUrl = notification.getAuthorizationUrl();
        authUrl.setUri("");
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);

        ClientResponse postResponse = notificationsClient.addPermissionNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0/" + testUser1OrcidId + "/notification-permission/\\d+"));
        String putCodeString = locationPath.substring(locationPath.lastIndexOf('/') + 1);
        Long putCode = Long.valueOf(putCodeString);

        ClientResponse viewResponse = notificationsClient.viewPermissionNotificationXml(testUser1OrcidId, putCode, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), viewResponse.getStatus());
        NotificationPermission retrievedNotification = viewResponse.getEntity(NotificationPermission.class);
        assertNotNull(retrievedNotification);
        assertTrue(retrievedNotification.getAuthorizationUrl().getPath().endsWith(authUrl.getPath()));
        assertFalse(retrievedNotification.getAuthorizationUrl().getPath().startsWith("http"));
        assertTrue(retrievedNotification.getAuthorizationUrl().getUri().startsWith("http"));
    }

    @Test
    public void createPermissionNotificationWithAbsentAuthorizationUriElement() throws JSONException {
        NotificationPermission notification = unmarshallFromPath("/notification_2.0/samples/notification-permission-2.0.xml");
        notification.setPutCode(null);
        AuthorizationUrl authUrl = notification.getAuthorizationUrl();
        authUrl.setUri("");
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);

        ClientResponse postResponse = notificationsClient.addPermissionNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0/" + testUser1OrcidId + "/notification-permission/\\d+"));
        String putCodeString = locationPath.substring(locationPath.lastIndexOf('/') + 1);
        Long putCode = Long.valueOf(putCodeString);

        ClientResponse viewResponse = notificationsClient.viewPermissionNotificationXml(testUser1OrcidId, putCode, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), viewResponse.getStatus());
        NotificationPermission retrievedNotification = viewResponse.getEntity(NotificationPermission.class);
        assertNotNull(retrievedNotification);
        assertTrue(retrievedNotification.getAuthorizationUrl().getPath().endsWith(authUrl.getPath()));
        assertFalse(retrievedNotification.getAuthorizationUrl().getPath().startsWith("http"));
        assertTrue(retrievedNotification.getAuthorizationUrl().getUri().startsWith("http"));
    }

    public NotificationPermission unmarshallFromPath(String path) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            NotificationPermission notification = unmarshall(reader);
            return notification;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    public NotificationPermission unmarshall(Reader reader) {
        try {
            JAXBContext context = JAXBContext.newInstance(NotificationPermission.class.getPackage().getName());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (NotificationPermission) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }
    
    @Test
    public void createNotificationInvalidWorkIDType() throws JSONException {
        NotificationPermission notification = unmarshallFromPath("/notification_2.0/samples/notification-permission-2.0.xml");
        notification.setPutCode(null);
        notification.getItems().getItems().get(0).getExternalIdentifier().setType("invalid");
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);

        ClientResponse response = notificationsClient.addPermissionNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

}
