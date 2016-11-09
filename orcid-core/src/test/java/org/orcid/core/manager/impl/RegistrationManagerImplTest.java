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
package org.orcid.core.manager.impl;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@Ignore
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class RegistrationManagerImplTest {

    @Resource
    RegistrationManagerImpl registrationManagerImpl;

    private OrcidProfileManager orcidProfileManager;

    @Before
    public void mockDependencies() {
        orcidProfileManager = mock(OrcidProfileManager.class);
        registrationManagerImpl.setNotificationManager(mock(NotificationManager.class));
        registrationManagerImpl.setOrcidProfileManager(orcidProfileManager);
        registrationManagerImpl.setNotificationManager(mock(NotificationManager.class));
    }
    
    @Test
    public void testCreateMinimalRegistration() {
        fail();
    }

    @Test
    public void testCreateMinimalRegistrationWithExistingEmailNotAutoDeprecatable() {
        fail();
    }
    
    @Test
    public void testCreateMinimalRegistrationWithExistingEmailThatCanBeAutoDeprecated() {
        fail();
    }
}
