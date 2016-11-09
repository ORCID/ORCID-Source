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
package org.orcid.api.t2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.api.notifications.server.delegator.impl.NotificationsApiServiceDelegatorImpl;
import org.orcid.core.exception.OrcidNotificationNotFoundException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.NotificationValidationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermissions;
import org.orcid.persistence.dao.ProfileDao;
import org.springframework.test.util.ReflectionTestUtils;

public class NotificationsApiServiceDelegatorTest {

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private NotificationValidationManager notificationValidationManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private ProfileDao profileDao;

    @InjectMocks
    private NotificationsApiServiceDelegatorImpl notificationsApiServiceDelegator = new NotificationsApiServiceDelegatorImpl();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(notificationsApiServiceDelegator, "baseUrl", "https://orcid.org/");
    }

    @Test(expected = OrcidNotificationNotFoundException.class)
    public void testNotFoundError() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555-5555-5555-5555", ScopePathType.PREMIUM_NOTIFICATION);
        notificationsApiServiceDelegator.findPermissionNotification("1234-5678-1234-5678", 1L);
    }

    @Test
    public void testFindPermissionNotifications() {
        String clientId = "APP-5555555555555555";
        String orcidId = "some-orcid";

        NotificationPermissions notifications = new NotificationPermissions();
        notifications.setNotifications(new ArrayList<>());

        when(notificationManager.findPermissionsByOrcidAndClient(eq(orcidId), eq(clientId), anyInt(), anyInt())).thenReturn(notifications);

        SecurityContextTestUtils.setUpSecurityContext(orcidId, clientId, ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response response = notificationsApiServiceDelegator.findPermissionNotifications("some-orcid");

        NotificationPermissions retrieved = (NotificationPermissions) response.getEntity();
        assertEquals(notifications.getNotifications().size(), retrieved.getNotifications().size());
    }

}
