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
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.notifications.NotificationsApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
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

    @Resource
    private OauthHelper oauthHelper;

    @Test
    public void testGetNotificationToken() throws JSONException {
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);
        assertNotNull(accessToken);
    }

    @Test
    public void createAddActivitiesNotification() throws JSONException {
        NotificationAddActivities notification = unmarshallFromPath("/notification_2.0_rc1/samples/notification-add-activities-2.0_rc1.xml");
        notification.setPutCode(null);
        String accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.PREMIUM_NOTIFICATION);

        ClientResponse response = notificationsClient.addAddActivitiesNotificationXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String locationPath = response.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0_rc1/" + testUser1OrcidId + "/notifications/add-activities/\\d+"));
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
