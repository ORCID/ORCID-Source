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

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.notifications.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.test.DBUnitTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class NotificationsApiServiceDelegatorTest extends DBUnitTest {

    @Resource
    private NotificationsApiServiceDelegator notificationsApiServiceDelegator;

    @Test(expected = OrcidNotFoundException.class)
    public void testNotFoundError() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555-5555-5555-5555", ScopePathType.PREMIUM_NOTIFICATION);
        notificationsApiServiceDelegator.findAddActivitiesNotification("1234-5678-1234-5678", 1L);
    }

}
