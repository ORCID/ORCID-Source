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
