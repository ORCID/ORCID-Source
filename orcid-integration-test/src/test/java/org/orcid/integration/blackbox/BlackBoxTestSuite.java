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
package org.orcid.integration.blackbox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.orcid.integration.blackbox.api.NotificationsTest;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.integration.blackbox.web.works.AddWorksTest;
import org.orcid.integration.blackbox.web.works.PrivacyWorksTest;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ SigninTest.class, AddWorksTest.class, PrivacyWorksTest.class, NotificationsTest.class })
public class BlackBoxTestSuite {

}
