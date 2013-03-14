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
package org.orcid.core.manager.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.net.URI;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class RegistrationManagerImplTest {

    @Resource
    RegistrationManagerImpl registrationManagerImpl;

    private OrcidProfileManager orcidProfileManager;

    @Before
    public void mockDependencies() {
        RegistrationManagerImpl.REGISTRATIONS_VERIFIED_COUNTER.clear();
        orcidProfileManager = mock(OrcidProfileManager.class);
        registrationManagerImpl.setNotificationManager(mock(NotificationManager.class));
        registrationManagerImpl.setOrcidProfileManager(orcidProfileManager);
        registrationManagerImpl.setNotificationManager(mock(NotificationManager.class));
    }

    @Test
    public void testVerifyRegistration() throws Exception {
        assertTrue(RegistrationManagerImpl.REGISTRATIONS_VERIFIED_COUNTER.count() == 0);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid("xyz");
        registrationManagerImpl.verifyRegistration(orcidProfile, new URI(""));
        assertTrue(RegistrationManagerImpl.REGISTRATIONS_VERIFIED_COUNTER.count() == 1);
    }

    @Test
    public void testVerifyRegistrationNotIncrementedOnException() throws Exception {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid("xyz");
        doThrow(new RuntimeException()).when(orcidProfileManager).updateOrcidHistory(orcidProfile);
        assertTrue(RegistrationManagerImpl.REGISTRATIONS_VERIFIED_COUNTER.count() == 0);

        try {
            registrationManagerImpl.verifyRegistration(orcidProfile, new URI(""));
            fail("Should have thrown RuntimeException");
        }

        catch (RuntimeException rte) {
            assertTrue(RegistrationManagerImpl.REGISTRATIONS_VERIFIED_COUNTER.count() == 0);
        }

    }

}
