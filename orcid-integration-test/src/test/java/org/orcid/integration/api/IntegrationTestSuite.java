package org.orcid.integration.api;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(ClasspathSuite.class)
@ClassnameFilters({".*\\.integration\\..*", "!.*\\.blackbox\\..*"})
public class IntegrationTestSuite {

}
