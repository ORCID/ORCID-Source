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
package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.notifications.NotificationsApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-notifications-context.xml" })
public class NotificationsTest {

    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String testUser1OrcidId;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    @Resource
    private NotificationsApiClientImpl notificationsClient;

    @Test
    public void testGetNotificationToken() throws JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
    }

    @Test
    public void createAddActivitiesNotification() throws JSONException {
        NotificationAddActivities notification = unmarshallFromPath("/notification-add-activities.xml");
        notification.setPutCode(null);
        String accessToken = getAccessToken();
        ClientResponse response = notificationsClient.addAddActivitiesNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String locationPath = response.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v1.0/4444-4444-4444-4444/notifications/add-activities/\\d+"));
    }

    private String getAccessToken() throws JSONException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", client1ClientId);
        params.add("client_secret", client1ClientSecret);
        params.add("grant_type", "client_credentials");
        params.add("scope", ScopePathType.NOTIFICATION.value());
        ClientResponse clientResponse = t2OAuthClient.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(200, clientResponse.getStatus());
        String body = clientResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        return accessToken;
    }

    public NotificationAddActivities unmarshallFromPath(String path) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            NotificationAddActivities notification = unmarshall(reader);
            return notification;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    public NotificationAddActivities unmarshall(Reader reader) {
        try {
            JAXBContext context = JAXBContext.newInstance(NotificationAddActivities.class.getPackage().getName());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (NotificationAddActivities) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }

}
