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

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

/** Usually run with -Dorg.orcid.config.file=classpath:test-web.properties,classpath:test-client.properties
 * 
 * before first run, execute test orcid-integration-test/src/test/java/org/orcid/integration/whitebox/SetUpClientsAndUsers.java
 * 
 * @author Will Simpson
 *
 */
@RunWith(ClasspathSuite.class)
@ClassnameFilters({".*\\.blackbox\\..*"})
public class BlackBoxTestSuite {

}
